FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER 2929783435@qq.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /cloud-auth

WORKDIR /cloud-auth

EXPOSE 9100

ADD ./cloud-auth/cloud-auth.jar ./

CMD sleep 120;java -Xms128m -Xmx256m -jar cloud-auth.jar
