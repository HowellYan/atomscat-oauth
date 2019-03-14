#!/usr/bin/env bash
docker build -t atomscat-oauth-mysql .;
#运行： docker run --name atomscat-oauth-mysql -d -p 3306:3306 -v /data/docker_db/atomscat-oauth-mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Aa123456  atomscat-oauth-mysql