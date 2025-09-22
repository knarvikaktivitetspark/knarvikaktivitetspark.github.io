#!/bin/bash -e

./build.sh

mvn package exec:java -Pserve