#!/bin/bash

aws ecr get-login-password --region us-west-2 | sudo docker login --username AWS --password-stdin 563057296362.dkr.ecr.us-west-2.amazonaws.com
sudo docker pull 563057296362.dkr.ecr.us-west-2.amazonaws.com/our-map-server:latest
sudo docker run -p 80:8080 563057296362.dkr.ecr.us-west-2.amazonaws.com/our-map-server
