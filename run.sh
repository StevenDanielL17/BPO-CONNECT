#!/bin/bash
echo "Starting BPOConnect Backend..."
if [ -f .env ]; then
	set -a
	. ./.env
	set +a
fi
mvn clean install -DskipTests
mvn spring-boot:run
