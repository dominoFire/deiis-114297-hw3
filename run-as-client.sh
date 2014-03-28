#! /bin/bash
set -e
set -v
runRemoteAsyncAE.sh tcp://localhost:61616 hw2-114297-aaeQueue -c ./src/main/resources/descriptors/FileSystemCollectionReader.xml 
