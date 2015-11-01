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

import io.parallec.core.FilterRegex;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.bean.StrStrMap;

import java.util.HashMap;
import java.util.Map;


/**
 * Different requests to different target URLs.
 * Extract out the progress with 3 different APIs
 * http://www.parallec.io/job_a.html
 * http://www.jeffpei.com/job_b.html 
 * http://www.restsuperman.com/job_c.html
 * 
 */
public class HttpDiffRequestsDiffServersApp {

    /**
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        
        ParallelClient pc = new ParallelClient();
        Map<String, StrStrMap> replacementVarMapNodeSpecific = new HashMap<String, StrStrMap>();
        replacementVarMapNodeSpecific.put("www.parallec.io",
                new StrStrMap().addPair("JOB_ID", "job_a"));
        replacementVarMapNodeSpecific.put("www.jeffpei.com",
                new StrStrMap().addPair("JOB_ID", "job_b"));
        replacementVarMapNodeSpecific.put("www.restcommander.com",
                new StrStrMap().addPair("JOB_ID", "job_c"));

        pc.prepareHttpGet("/$JOB_ID.html")
                .setTargetHostsFromString(
                        "www.parallec.io www.jeffpei.com www.restcommander.com")
                .setReplacementVarMapNodeSpecific(replacementVarMapNodeSpecific)
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        String extractedString = new FilterRegex(
                                ".*<td>JobProgress</td>\\s*<td>(.*?)</td>.*")
                                .filter(res.getResponseContent());
                        System.out.println("[Extracted String]: progress:"
                                + extractedString + " host: " + res.getHost());
                        //System.out.println(res.toString());
                    }
                });
 
        pc.releaseExternalResources();
    }
}
