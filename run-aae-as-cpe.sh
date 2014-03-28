#! /bin/bash
set -e
set -v
export UIMA_CLASSPATH=./target/:./target/dependency/
cpeGui.sh
