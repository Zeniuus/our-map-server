#!/bin/bash

aws ecr get-login-password --region us-west-2 | sudo docker login --username AWS --password-stdin 563057296362.dkr.ecr.us-west-2.amazonaws.com
sudo docker pull 563057296362.dkr.ecr.us-west-2.amazonaws.com/our-map-server:latest
sudo docker ps -a -q | xargs sudo docker rm -f # 기존에 떠 있던 서버를 종료한다.
sudo rm nohup.out
sudo nohup docker run -p 80:8080 \
  -v $(pwd)/deploy/prod:/app/conf \
  -e OUR_MAP_OVERRIDING_PROPERTIES_FILENAME=/app/conf/application-prod.properties \
  563057296362.dkr.ecr.us-west-2.amazonaws.com/our-map-server &
