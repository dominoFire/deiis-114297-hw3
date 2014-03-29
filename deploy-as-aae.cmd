@ECHO ON
mvn clean package dependency:copy-dependencies
SET UIMA_CLASSPATH=.\target\;.\target\dependency\
deployAsyncService.cmd .\src\main\resources\hw2-114297-aae-deploy.xml -brokerUrl tcp://localhost:61616
