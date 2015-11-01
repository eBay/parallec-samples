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

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;

import java.util.Map;

/**
 * The Class HttpBasicMinimumApp.
 * With basic response handling.
 * Does not use response context
 */
public class HttpBasicMinimumApp {
    
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        ParallelClient pc = new ParallelClient();
        pc.prepareHttpGet("")
                .setConcurrency(1000)
                .setTargetHostsFromString(
                        "www.parallec.io www.jeffpei.com www.restcommander.com")
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println(res.getResponseContent());
                    }
                });
        pc.releaseExternalResources();
    }
}
