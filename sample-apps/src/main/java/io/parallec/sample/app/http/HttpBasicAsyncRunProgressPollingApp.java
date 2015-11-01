/*  
Copyright [2013-2015] eBay Software Foundation
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package io.parallec.sample.app.http;

import io.parallec.core.HostsSourceType;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTask;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.util.PcStringUtils;

import java.util.Map;

/**
 * The app that use async mode to run a parallel task, and then poll the progress
 * and show an aggregation
 */
public class HttpBasicAsyncRunProgressPollingApp {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        ParallelClient pc = new ParallelClient();

        ParallelTask task = pc.prepareHttpGet("").async()
                .setConcurrency(500)
                .setTargetHostsFromLineByLineText("http://www.parallec.io/userdata/sample_target_hosts_top100_old.txt",
                         HostsSourceType.URL)
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println("Responose Code:"
                                + res.getStatusCode() + " host: "
                                + res.getHost());
                    }
                });

        while (!task.isCompleted()) {
            try {
                Thread.sleep(100L);
                System.err.println(String.format(
                        "POLL_JOB_PROGRESS (%.5g%%)  PT jobid: %s",
                        task.getProgress(), task.getTaskId()));
                pc.logHealth();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out
        .println("Result Summary\n "
                + PcStringUtils.renderJson(task
                        .getAggregateResultFullSummary()));
        
        System.out
                .println("Result Brief Summary\n "
                        + PcStringUtils.renderJson(task
                                .getAggregateResultCountSummary()));
        pc.releaseExternalResources();
    }
}
