<a href="http://www.parallec.io"><img alt="Parallec-logo" src="http://www.parallec.io/images/parallec-logo.png" width="325"></a>



- [Sample Applications](#sample-apps)
- [Sample Spark Server](#sample-spark-server)

##sample-apps <a name="sample-apps"></a>
------
Sample Applications demonstrate how to use [parallec.io](http://www.parallec.io) library. 

Each file is independent with a main function and can be run directly. (TCP/HTTP Async API requires the sample HTTP/TCP servers to start in advance. Sample servers included here are independent executable files too).

**More** comprehensive examples are available in the [test cases](https://github.com/eBay/parallec/tree/master/src/test/java/io/parallec/core).

| Sample App Location | Overview                                                                                                                                                                                                         |
|:-------------------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|         [HTTP](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/http)        | Basic Http.  Handle Async APIs with auto progress polling. Asyn run Parallel Task with progress polling. Result aggregation. Provided a sample HttpServer will async job submission/polling API to test with.  Send to elastic search. Request template with variable replacements.   |
|         [SSH](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/ssh)         | Sample Parallel SSH. Need to input the userName, ip, then [password / keyfile path]. Tested with EC2 instance in AWS.                                                                                            |
|         [PING](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/ping)        | Sample Parallel Ping App.                                                                                                                                                                                        |
|         [TCP](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/tcp)         | Sample Parallel TCP app. Includes a sample [TCP Echo Server](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/tcp/sampleserver/TcpEchoServer.java)  to test with.                                                                                                                                        |

####HTTP
- [HttpBasicMinimumApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/HttpBasicMinimumApp.java): 10 lines minimum example of hitting 3 websites.

- [HttpBasicAsyncRunProgressPollingApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/HttpBasicAsyncRunProgressPollingApp.java): Use async mode to run a parallel task, and then poll the progress and show an aggregation on status code.

- [Http100WebAggregateToElasticSearchApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/Http100WebAggregateToElasticSearchApp.java): Hitting 100 common websites to get these status code to elastic search, and visualized in Kibana in 20 lines. Usage of response context to pass elastic search client [demo video](https://www.youtube.com/watch?v=sDIP_Ujxkl4)

- [Http3WebAgrregateToElasticSearchMinApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/Http3WebAgrregateToElasticSearchMinApp.java): Usage of FilterRegex.

- [HttpDiffRequestsDiffServersApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/HttpDiffRequestsDiffServersApp.java): Different requests to different target URLs. Request template.
- [HttpDiffRequestsSameServerApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/HttpDiffRequestsSameServerApp.java): Different requests to same target server. Request template. setReplaceVarMapToSingleTargetSingleVar().

- [HttpAsyncApiPollableJob.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/HttpAsyncApiPollableJob.java): demos to handle async APIs with auto progress polling. Task level concurrency control.  (require starts the [Sample Web Server with Async API](HttpServerSampleAsyncApiWithPollableJob) first)

####Set Target Hosts
please refer to the documentation. 

###Usage

You may simple fork the project, or copy and paste indivisual files after getting the correct dependencies 

Maven

```xml
<dependency>
	<groupId>io.parallec</groupId>
	<artifactId>parallec-core</artifactId>
	<version>0.9.0</version>
</dependency>
```

Gradle

```xml
compile 'io.parallec:parallec-core:0.9.0'
```


#####Screenshots

Executing [Http3WebAgrregateToElasticSearchMinApp.java](https://github.com/eBay/parallec-samples/blob/master/sample-apps/src/main/java/io/parallec/sample/app/http/Http3WebAgrregateToElasticSearchMinApp.java), visualized in Kibana.

With elasticsearch-1.3.4 and kibana-3.1.2

![Screenshot](http://www.parallec.io/images/screenshots/elastic-aggre-web3.png) 


##[sample-spark-server](https://github.com/eBay/parallec-samples/blob/master/sample-spark-server/src/main/java/io/parallec/ebay/server/ParallecSparkServer.java) <a name="sample-spark-server"></a>

------
Sample [Single File](https://github.com/eBay/parallec-samples/blob/master/sample-spark-server/src/main/java/io/parallec/ebay/server/ParallecSparkServer.java) Web Server in Spark with Parallec . Require JDK 1.8+ due to Spark server.
 
- SparkServer: http://sparkjava.com

####Build & Run
######Build:

Fork the project and run:

	mvn clean compile assembly:single

######Run: 
In folder: parallec-samples/sample-spark-server/target: (if not conduct ping, no need of sudo)
	
	sudo java -jar parallec-sample-spark-server-0.9.0-jar-with-dependencies.jar


#### Server APIs 

The APIs tries to get target hosts from a local file in the same path.  

```
 get("/http/:filename"
 get("/ssh/:filename/:concurrency/:showDetail"
```

targetHostFile location: 

- If in IDE, put into the same folder as the pom.xml.
- If run as jar. just same folder as the executable jar file.

APIs:

- localhost:4567/ssh/targetHostFile/200/true  (require update login user/password)
- localhost:4567/ping/targetHostFile  ; need root
- localhost:4567/http/targetHostFile :  use elastic search

######Sample Output

http://localhost:4567/http/targetHostFile



```
Parallec: completed HTTP and sent to elastic search 
:3 Servers in 0.575 seconds. Results:  Results: [200 OK COUNT: 3 ]:
	www.parallec.io
	www.jeffpei.com
	www.ebay.com
###################################

At 2015.11.01.10.15.47.398-0800
```	 

Thanks for trying Parallec.io. Please submit a git issue for any questions you have.

## Author and Contributors
#### Original Author
Yuanteng (Jeff) Pei

#### Contributors

Your name here

## Licenses

Code licensed under Apache License v2.0

© 2015 eBay Software Foundation


 
 
 
 