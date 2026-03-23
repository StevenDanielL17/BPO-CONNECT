#!/bin/bash
echo "Starting BPOConnect Backend..."
mvn clean install -DskipTests
mvn spring-boot:run
