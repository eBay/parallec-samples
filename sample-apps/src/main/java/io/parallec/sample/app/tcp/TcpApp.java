package io.parallec.sample.app.tcp;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;

import java.util.Map;

/**
 * requirement: first run Sample TCP Echo Server on port 10081 
 * in package io.parallec.sample.app.tcp.sampleserver.
 * 
 * Then run this app
 * @author Yuanteng (Jeff) Pei
 *
 */
public class TcpApp {
    
    public static void main(String[] args) {
        ParallelClient pc = new ParallelClient();
        pc.prepareTcp("requestMonitor")
        .setTargetHostsFromString("localhost")
        .setTcpPort(10081)
        .execute(new ParallecResponseHandler() {
            public void onCompleted(ResponseOnSingleTask res,
                    Map<String, Object> responseContext) {
                System.out.println("Responose:" + res.getResponseContent() + " host: "
                        + res.getHost() + " errmsg: "
                        + res.getErrorMessage());
            }
        });
        pc.releaseExternalResources();
    }// end func
}
