package io.parallec.sample.app.ping;

import io.parallec.core.HostsSourceType;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTask;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.bean.ping.PingMode;
import io.parallec.core.util.PcStringUtils;

import java.util.Map;

/**
 * Notice default ping mode is INET_ADDRESS_REACHABLE_NEED_ROOT
 * which requires **ROOT permission** for accurate results in ICMP. 
 * 
 * @author Yuanteng (Jeff) Pei
 *
 */
public class Ping100WebApp {
    
    public static void main(String[] args) {
        ParallelClient pc = new ParallelClient();
        ParallelTask task = pc.preparePing().setConcurrency(1500)
                .setTargetHostsFromLineByLineText("http://www.parallec.io/userdata/sample_target_hosts_top100_old.txt",
                        HostsSourceType.URL)
                .setPingMode(PingMode.INET_ADDRESS_REACHABLE_NEED_ROOT)
                .setPingNumRetries(1)
                .setPingTimeoutMillis(500)
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println(res.toString());
                    }
                });

        System.out.println("Task Pretty Print: \n" +
                PcStringUtils.renderJson(task.getAggregateResultCountSummary()));
        System.out.println("Total Duration: " + task.getDurationSec());
        pc.releaseExternalResources();
    }// end func
}
