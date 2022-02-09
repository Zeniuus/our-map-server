FROM openjdk:8-jdk

WORKDIR /app
ADD . /app
COPY run-server.sh /app/run-server.sh
RUN chmod +x run-server.sh

CMD ["./run-server.sh", "./bin/our-map-server"]
