 1 > here are the images explaining the project purpose 

![image](https://github.com/user-attachments/assets/57e065e3-c771-47fd-993d-c0e906db0ce3)
![image](https://github.com/user-attachments/assets/d3557c0b-7416-4a53-b261-789efa443d34)

2 > how to run the project 

Prerequisites
Before running the project, ensure you have the following installed:

Java 17 or higher

Apache hadoop

Apache Maven

Apache Kafka

Apache Spark

Apache Cassandra

MySQL

( hadoop for spark )

Hadoop HDFS (optional, can use AWS S3 or Google Drive as alternative , but i am using hdfs for image storage )


1. Clone the Repository
 
2. Create a MySQL database named bigdata 
   Import the data from csv files
3. create a topic named SAU 
navigate to bin file where you installed kafaka then :
     run zookeeper server ;
  ./zookeeper-server-start.sh ../config/zookeeper.properties   NOTE: this could be a little different depending on the OS that you are using 
    run kafka server ;
   ./kafka-server-start.sh ../config/server.properties

4. then run the data generator .py ; 
   navigate to the dir where io_device located : 
   cd 3rdProject/
   then run it ; 
   python3 iot_devices.py

5. for running the streamhandler class ;
    navigate to :
    cd 3rdProject/StreamHandler/
   
     then run it with the command ; 
       sbt package && spark-submit --class StreamHandler --master local[*] --packages "org.apache.spark:spark-sql-kafka-0-10_2.13:3.5.3,com.datastax.spark:spark-cassandra-connector_2.13:3.5.1,com.datastax.cassandra:cassandra-driver-core:4.0.0"  target/scala-2.13/stream-handler_2.13-1.0.jar  (NOTE you have to set up sbt build first ) 
   
6. last step ;
    run you java app and access via ;
    http://localhost:8080
