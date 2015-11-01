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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.Asserts;

/**
 * 
 * Example of sending Different requests to same target server. note that the target host must
 * match the key: e.g. hitting
 * http://www.parallec.io/userdata/sample_weather_48824.txt
 * http://www.parallec.io/userdata/sample_weather_95037.txt
 * 
 * You may fire thousands of APIs to the same server in this way.
 * 
 * This is the easiest way to conduct variable replacement (only 1 variable)
 * more complex examples are in the test cases:
 * https://github.com/eBay/parallec/blob/master/src/test/java/io/parallec/core/
 * main/http/request/template/ParallelClientVarReplacementHostSpecificTest.java
 * 
 */
public class HttpDiffRequestsSameServerApp {

    public static void main(String[] args) {

        ParallelClient pc = new ParallelClient();

        Map<String, Object> responseContext = new HashMap<String, Object>();
        responseContext.put("temp", null);

        pc.prepareHttpGet("/userdata/sample_weather_$ZIP.txt")
                .setReplaceVarMapToSingleTargetSingleVar("ZIP",
                    Arrays.asList("95037","48824"), "www.parallec.io")
                .setResponseContext(responseContext)
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        String temp = new FilterRegex("(.*)").filter(res
                                .getResponseContent());
                        System.out.println("\n!!Temperature: " + temp
                                + " TargetHost: " + res.getHost());
                        responseContext.put("temp", temp);
                    }
                });

        int tempGlobal = Integer.parseInt((String) responseContext.get("temp"));
        Asserts.check(
                tempGlobal <= 100 && tempGlobal >= 0,
                " Fail to extract output from sample weather API. Fail different request to same server test");

        pc.releaseExternalResources();
       
    }
}
