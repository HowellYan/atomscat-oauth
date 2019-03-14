#!/usr/bin/env bash
cp ./target/root.jar ./bin/dockerfile/java-app
docker build -t atomscat-oauth ./bin/dockerfile/java-app/;

#