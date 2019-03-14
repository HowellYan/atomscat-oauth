#!/usr/bin/env bash
mvn clean install -DskipTests;
cp ./target/root.jar ./bin/dockerfile/java-app
docker build --rm -t atomscat-oauth ./bin/dockerfile/java-app/;

#运行： docker run --name atomscat-oauth -e JAVA_OPTIONS="-Duser.timezone=GMT+8 -Xms128m -Xmx256m -Dspring.profiles.active=prod" --link=atomscat-oauth-mysql:atomscat-oauth-mysql  -p 8888:8888 -d atomscat-oauth
# curl http://127.0.0.1:8888/actuator/health