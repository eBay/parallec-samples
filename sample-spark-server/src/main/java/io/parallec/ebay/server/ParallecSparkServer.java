package io.parallec.ebay.server;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static spark.Spark.before;
import static spark.Spark.get;
import io.parallec.core.HostsSourceType;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTask;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.config.ParallelTaskConfig;
import io.parallec.core.util.PcDateUtils;
import io.parallec.core.util.PcStringUtils;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;

/**
 * Sample Single File Server in Spark. Require JDK 1.8+ due to Spark server.
 * http://sparkjava.com/documentation.html
 * <br><br>
 * Build: mvn clean compile assembly:single
 * <br>
 * Run: in parallec-samples/sample-spark-server/target: sudo java -jar parallec-sample-spark-server-0.9.0-jar-with-dependencies.jar
 * <br><br>
 * run API example: 
 * <br><br>
 * localhost:4567/ssh/targetHostFile/200/true  (require update login user/password)
 * <br>
 * localhost:4567/ping/targetHostFile  ; need root
 * <br>
 * localhost:4567/http/targetHostFile :  use elastic search
 * 
 * targetHostFile is relative to this path. If in IDE, put into the same folder as the pom.xml.
 * if run as jar. just same folder as the executable jar file.
 * 
 * @author Yuanteng (Jeff) Pei
 *
 */
public class ParallecSparkServer {
    

    public static String userName = "yourUserNameforSsh";
    public static String password = "yourPasswordforSsh";
    
    public static void main(String[] args) {

        before((request, response) -> response.type("text/plain"));

        get("/",
                (request, response) -> "Welcome to Parallec Sample Single File Server in Spark. Require JDK 1.8+ due to Spark server. "
                        + "run API example: \n"
                        + "localhost:4567/ssh/targetHostFile/200/true\n"
                        + "localhost:4567/http/targetHostFile\n"
                        + "localhost:4567/ping/targetHostFile (Must Run as ROOT / sudo)\n"
                        + "targetHostFile is a local file with host name line by line that in same dir of this executabe jar");

        get("/http/:filename", (request, response) -> {
            String fileName = request.params(":filename");
            String res = checkSiteStatus(fileName);
            response.status(200);
            return "Parallec: completed HTTP and sent to elastic search \n:" + res + "\nAt "
            + PcDateUtils.getNowDateTimeStrStandard();
        });

        get("/ssh/:filename/:concurrency/:showDetail",
                (request, response) -> {
                    String fileName = request.params(":filename");
                    int concurrency = Integer.parseInt(request
                            .params(":concurrency"));
                    boolean showDetail = Boolean.parseBoolean(request
                            .params(":showDetail"));
                    String res = scalableSsh(fileName, concurrency, showDetail);
                    response.status(200);
                    return "Parallec: completed SSH \n" + res + "\nAt "
                            + PcDateUtils.getNowDateTimeStrStandard();
                });

        get("/ping/:filename", (request, response) -> {
            String fileName = request.params(":filename");
            String res = scalablePing(fileName);
            response.status(200);
            return "Parallec: completed Ping \n  " + res + "\nAt "
                    + PcDateUtils.getNowDateTimeStrStandard();
        });

        get("/shutdown",
                (request, response) -> {
                    releaseResources();
                    response.status(200);
                    return "relased all resources at "
                            + PcDateUtils.getNowDateTimeStrStandard();
                });
    }


    public static String scalableSsh(String fileName, int concurrency,
            boolean showDetail) {

        ParallelClient pc = new ParallelClient();

        ParallelTask task = pc
                .prepareSsh()
                .setConcurrency(concurrency)
                .setTargetHostsFromLineByLineText(fileName,
                        HostsSourceType.LOCAL_FILE)
                .setSshCommandLine("date; ").setSshUserName(userName)
                .setSshPassword(password)
                .execute(new ParallecResponseHandler() {
                    @Override
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println("Responose:" + res.toString()
                                + " host: " + res.getHost() + " errmsg: "
                                + res.getErrorMessage());
                    }
                });

        String res = task.getRequestNumActual()
                + " Servers in "
                + task.getDurationSec()
                + " seconds. Results:\n\n"
                + (showDetail ? task.getAggregatedResultHumanStr()
                        : PcStringUtils.renderJson(task
                                .getAggregateResultCountSummary()));

        System.out.println("Task Pretty Print: \n " + res);
        return res;
    }

    public static String scalablePing(String fileName) {
        ParallelClient pc = new ParallelClient();
        ParallelTask task = pc
                .preparePing()
                .setConcurrency(1500)
                .setTargetHostsFromLineByLineText(fileName,
                        HostsSourceType.LOCAL_FILE)
                .execute(new ParallecResponseHandler() {
                    @Override
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        ;// logger.info(res.toString());
                    }
                });
        String res = task.getRequestNumActual()
                + " Servers in "
                + task.getDurationSec()
                + " seconds. Results: "
                + task.getAggregatedResultHumanStr();
        System.out.println("Task summary: \n " + res);
        return res;
    }// end func

    public static void releaseResources() {
        ParallelClient parallec = new ParallelClient();
        parallec.releaseExternalResources();
    }

    public static String checkSiteStatus(String fileName) {

        ParallelClient pc = new ParallelClient();
        HashMap<String, Object> responseContext = new HashMap<String, Object>();
        Node node = nodeBuilder().node();
        responseContext.put("Client", node.client());
        ParallelTask task = pc
                .prepareHttpGet("")
                .setTargetHostsFromLineByLineText(fileName,
                        HostsSourceType.LOCAL_FILE)
                .setResponseContext(responseContext).setConfig(genConfig())
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        Map<String, Object> metricMap = new HashMap<String, Object>();
                        metricMap.put("StatusCode", res.getStatusCode()
                                .replaceAll(" ", "_"));
                        metricMap.put("LastUpdated",
                                PcDateUtils.getNowDateTimeStrStandard());
                        metricMap.put("NodeGroupType", fileName);
                        Client client = (Client) responseContext.get("Client");
                        client.prepareIndex("local", "parallec", res.getHost())
                                .setSource(metricMap).execute();
                    }
                });

        String res = task.getRequestNumActual()
                + " Servers in "
                + task.getDurationSec()
                + " seconds. Results: "
                + " Results: "
                + task.getAggregatedResultHumanStr();

        node.close();

        System.out.println("Task summary: \n " + res);
        return res;

    }// end func

    public static ParallelTaskConfig genConfig() {
        ParallelTaskConfig config = new ParallelTaskConfig();
        config.setActorMaxOperationTimeoutSec(20);
        config.setAutoSaveLogToLocal(true);
        config.setSaveResponseToTask(true);
        return config;
    }

}
