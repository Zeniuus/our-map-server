#!/bin/bash

aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com

cd frontend-admin/admin
npm install
REACT_APP_API_BASE_URL=https://admin.staircrusher.club/api npm run build
docker build -t 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-frontend-admin:latest .
docker push 563057296362.dkr.ecr.ap-northeast-2.amazonaws.com/our-map-frontend-admin:latest
