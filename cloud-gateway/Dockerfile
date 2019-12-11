FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER 2929783435@qq.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /cloud-gateway

WORKDIR /cloud-gateway

EXPOSE 9999

ADD ./cloud-gateway/cloud-gateway.jar ./

CMD sleep 120;java -Xms128m -Xmx256m -jar cloud-gateway.jar
