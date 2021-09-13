#!/bin/bash
#aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com
#
#sudo docker pull 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server:latest
#sudo docker pull 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-server-admin:latest
#sudo docker pull 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-frontend-admin:latest

sudo docker ps -a -q | xargs sudo docker rm -f # 기존에 떠 있던 서버를 종료한다.

function get_publishing_port {
  # $1: cluster, $2: container-name
  # e.g. get_publishing_port prod server -> 80
  if [ $1 = "prod" ]
  then
    case $2 in
      "server")
        echo 80
        ;;
      "server-admin")
        echo 8081
        ;;
      "frontend-admin")
        echo 3001
        ;;
      *)
        echo "invalid input: $1 $2"
        exit 1
        ;;
    esac
  elif [ $1 == "test" ]
  then
    case $2 in
      "server")
        echo 90
        ;;
      "server-admin")
        echo 9081
        ;;
      "frontend-admin")
        echo 4001
        ;;
      *)
        echo "invalid input: $1 $2"
        exit 1
        ;;
    esac
  fi
}

function get_container_port {
  # $1: container-name
  # e.g. get_container_port server -> 8080
  case $1 in
    "server")
      echo 8080
      ;;
    "server-admin")
      echo 8081
      ;;
    "frontend-admin")
      echo 80
      ;;
    *)
      echo "invalid input: $1 $2"
      exit 1
      ;;
  esac
}

function deploy_server {
  # $1: cluster, $2: server-name
  # e.g. deploy_server prod server
  echo $(get_publishing_port $1 $2)
  echo $(get_container_port $2)
  let PUBLISHING_PORT=$(get_publishing_port $1 $2)
  let CONTAINER_PORT=$(get_container_port $2)
  sudo rm "nohup-$1-$2.out"
  sudo nohup docker run -p "$PUBLISHING_PORT:$CONTAINER_PORT" \
    -v "$(pwd)/deploy/$1":/app/conf \
    -e OUR_MAP_OVERRIDING_PROPERTIES_FILENAME=/app/conf/application.properties \
    "563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-$2" &> "nohup-$1-$2.out" &
}

function deploy_frontend {
  # $1: cluster, $2: frontend-name
  # e.g. deploy_frontend prod frontend-admin
  let PUBLISHING_PORT=$(get_publishing_port $1 $2)
  let CONTAINER_PORT=$(get_container_port $2)
  sudo rm "nohup-$1-$2.out"
  sudo nohup docker run -p "$PUBLISHING_PORT:$CONTAINER_PORT" \
    -v "$(pwd)/deploy/$1/config.js":/app/build/config.js \
    "563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-$2" &> "nohup-$1-$2.out" &
}

deploy_server prod server
deploy_server prod server-admin
deploy_frontend prod frontend-admin

deploy_server test server
deploy_server test server-admin
deploy_frontend test frontend-admin
