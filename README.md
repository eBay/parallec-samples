# parallec-samples


##sample-apps 
------
Sample Applications demonstrate how to use parallec.io library. 

Each file is independent with a main function and can be run directly. (TCP/HTTP Async API requires the sample servers to start in advance, Sample servers included here as singel file runnable too).


| Sample App Location | Overview                                                                                                                                                                                                         |
|:-------------------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|         [HTTP](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/http)        | Basic Http.  Handle Async APIs with auto progress polling. Asyn run Parallel Task with progress polling. Result aggregation. Provided a sample HttpServer will async job submission/polling API to test with.  Send to elastic search. Request template with variable replacements.   |
|         [SSH](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/ssh)         | Sample Parallel SSH. Need to input the userName, ip, then [password / keyfile path]. Tested with EC2 instance in AWS.                                                                                            |
|         [PING](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/ping)        | Sample Parallel Ping App.                                                                                                                                                                                        |
|         [TCP](https://github.com/eBay/parallec-samples/tree/master/sample-apps/src/main/java/io/parallec/sample/app/tcp)         | Sample Parallel TCP app. Provided a sample TCP Echo Server  to test with.                                                                                                                                        |

#####Screenshots

![Screenshot](http://www.parallec.io/images/screenshots/elastic-aggre-web3.png) after running Http3WebAgrregateToElasticSearchMinApp.


##sample-spark-server
------
Sample Single File Web Server in Spark with Parallec . Require JDK 1.8+ due to Spark server.
 
- SparkServer: http://sparkjava.com

####Build & Run
Build:

	mvn clean compile assembly:single

Run: in folder: parallec-samples/sample-spark-server/target: (if not conduct ping, no need of sudo)
	
	sudo java -jar parallec-sample-spark-server-0.9.0-jar-with-dependencies.jar
	
	

#### Server APIs 

The APIs tries to get target hosts from a local file in the same path.  

```
 get("/http/:filename"
 get("/ssh/:filename/:concurrency/:showDetail"
```

targetHostFile is relative to this path. If in IDE, put into the same folder as the pom.xml.
 * if run as jar. just same folder as the executable jar file.

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

End.
 
 
 
 
 