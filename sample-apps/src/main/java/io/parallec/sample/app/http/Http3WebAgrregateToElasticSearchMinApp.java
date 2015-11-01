package io.parallec.sample.app.http;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import io.parallec.core.FilterRegex;
import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.util.PcDateUtils;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;

/**
 * Sample results visualized in Kibana as in:
 * http://www.parallec.io/images/screenshots/elastic-aggre-web3.png
 * 
 * hitting
 * http://www.parallec.io/validateInternals.html
 * http://www.jeffpei.com/validateInternals.html
 * http://www.restcommander.com/validateInternals.html
 * 
 * @author Yuanteng (Jeff) Pei
 */
public class Http3WebAgrregateToElasticSearchMinApp {

	
	public static void main(String[] args) {

		ParallelClient pc = new ParallelClient();
		org.elasticsearch.node.Node node = nodeBuilder().node(); //elastic client initialize
		HashMap<String, Object> responseContext = new HashMap<String, Object>();
		responseContext.put("Client", node.client());
		
		pc.prepareHttpGet("/validateInternals.html")
				.setTargetHostsFromString("www.parallec.io www.jeffpei.com www.restcommander.com")
				.setResponseContext(responseContext)
				.execute( new ParallecResponseHandler() {
					public void onCompleted(ResponseOnSingleTask res,
							Map<String, Object> responseContext) {
						String cpu = new FilterRegex(".*<td>CPU-Usage-Percent</td>\\s*<td>(.*?)</td>[\\s\\S]*")
								.filter(res.getResponseContent());
						String memory = new FilterRegex(".*<td>Memory-Used-KB</td>\\s*<td>(.*?)</td>[\\s\\S]*")
						.filter(res.getResponseContent());
						Map<String, Object> metricMap = new HashMap<String, Object>();
						metricMap.put("CpuUsage", cpu); metricMap.put("MemoryUsage", memory);
						metricMap.put("LastUpdated",PcDateUtils.getNowDateTimeStrStandard());
						metricMap.put("NodeGroupType", "Web3");
						System.out.println("cpu:" + cpu + " host: " + res.getHost() );
						Client client = (Client) responseContext.get("Client");
						client.prepareIndex("local", "vi", res.getHost()).setSource(metricMap).execute();
					}
				});
		node.close(); pc.releaseExternalResources();
	}

}
