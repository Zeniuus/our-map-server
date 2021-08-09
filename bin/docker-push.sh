#!/bin/bash

aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 563057296362.dkr.ecr.us-west-2.amazonaws.com
./gradlew installDist # FIXME: 분명 dockerPushOurMap이 installDist에 의존하는데... 이 줄이 없으면 build/install/our-map-server 아래 내용이 아무것도 복사가 안 된다.
./gradlew dockerPushOurMap
