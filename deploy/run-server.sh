#!/bin/bash

mkdir -p /app/conf
echo $BASE64_ENC_SECRET_PROPS_FILE | base64 -d > /app/conf/secret.properties
$1
