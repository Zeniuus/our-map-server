#!/bin/bash

aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com
sudo docker ps -a -q | xargs sudo docker rm -f # 기존에 떠 있던 서버를 종료한다.

sudo rm nohup-server.out
sudo docker pull 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server:latest
sudo nohup docker run -p 80:8080 \
  -v $(pwd)/deploy/prod:/app/conf \
  -e OUR_MAP_OVERRIDING_PROPERTIES_FILENAME=/app/conf/application-prod.properties \
  563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server &> nohup-server.out &

sudo rm nohup-server-admin.out
sudo docker pull 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server-admin:latest
sudo nohup docker run -p 8081:8081 \
  -v $(pwd)/deploy/prod:/app/conf \
  -e OUR_MAP_OVERRIDING_PROPERTIES_FILENAME=/app/conf/application-prod.properties \
  563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server-admin &> nohup-server-admin.out &

sudo rm nohup-frontend-admin.out
sudo docker pull 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-frontend-admin:latest
sudo nohup docker run -p 3001:80 \
  563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-frontend-admin &> nohup-server-admin.out &
