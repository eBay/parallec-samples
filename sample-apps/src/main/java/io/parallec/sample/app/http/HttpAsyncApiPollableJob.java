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

import io.parallec.core.ParallecHeader;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTask;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.actor.poll.HttpPollerProcessor;

import java.util.Map;

/**
 * The Class HttpAsyncApiPoll.
 * To demonstrate the Async HTTP Server
 * Procedure:
 * 
 * 1. Start Sample HTTP server: in HttpServerSampleAsyncApiWithPollableJob first (just java run application)
 * 2. Run this as a java application.
 * 
 * You will need to define a 
 * 
 */
public class HttpAsyncApiPollableJob {

    /**
     * Generate sample http poller.
     *
     * @return the http poller processor
     */
    public static HttpPollerProcessor generateSampleHttpPoller() {

        // Init the poller
        String pollerType = "CronusAgentPoller";
        String successRegex = ".*\"progress\"\\s*:\\s*(100).*}";
        String failureRegex = ".*\"error\"\\s*:\\s*(.*).*}";
        String jobIdRegex = ".*\"/status/(.*?)\".*";
        String progressRegex = ".*\"progress\"\\s*:\\s*([0-9]*).*}";
        int progressStuckTimeoutSeconds = 600;
        int maxPollError = 5;
        long pollIntervalMillis = 2000L;
        String jobIdPlaceHolder = "$JOB_ID";
        String pollerRequestTemplate = "/status/" + jobIdPlaceHolder;

        HttpPollerProcessor httpPollerProcessor = new HttpPollerProcessor(
                pollerType, successRegex, failureRegex, jobIdRegex,
                progressRegex, progressStuckTimeoutSeconds, pollIntervalMillis,
                pollerRequestTemplate, jobIdPlaceHolder, maxPollError);

        return httpPollerProcessor;
    }
    
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String [] args) {

        HttpPollerProcessor httpPollerProcessor = generateSampleHttpPoller();
        
        ParallelClient pc = new ParallelClient();
        
        ParallelTask task = pc
                .prepareHttpPost("/submitJob")
                .setHttpHeaders(
                        new ParallecHeader().addPair("authorization",
                                "SAMPLE_AUTH_KEY"))
                .setHttpPort(10080).setConcurrency(1500)
                .setTargetHostsFromString("localhost").setHttpPollable(true)
                .setHttpPollerProcessor(httpPollerProcessor)
                .execute(new ParallecResponseHandler() {
                    @Override
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println("getPollingHistory:"
                                + res.getPollingHistory() + " host: "
                                + res.getHost());
                        System.out.println(res.toString());
                    }
                });
        System.out.println("Task Pretty Print: \n " + task.prettyPrintInfo());
        pc.releaseExternalResources();
    }
    
}
