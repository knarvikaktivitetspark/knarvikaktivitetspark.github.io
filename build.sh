#!/bin/bash -e

if [ ! -d node_modules ]; then
  npm install
fi

mvn clean package exec:java