#! /bin/bash
set -e
set -v
mvn clean package dependency:copy-dependencies
export UIMA_CLASSPATH=./target/:./target/dependency/
deployAsyncService.sh ./src/main/resources/hw2-114297-aae-deploy.xml -brokerUrl tcp://localhost:61616

