#!/bin/bash

java -version

mkdir -p /tmp/as/local

export PROJECT_ROOT=$(cd ../../../;pwd)
cd $PROJECT_ROOT
 SPRING_PROFILES_ACTIVE=local \
 SPRING_SECURITY_DEBUG=true \
 DATAVAULT_HOME="$PROJECT_ROOT/dv5/local-byodb/props/broker" \
 SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
 SPRING_SQL_INIT_MODE=never \
 SPRING_DATASOURCE_USERNAME=user \
 SPRING_DATASOURCE_PASSWORD=userpass \
 SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC' \
 ./mvnw spring-boot:run  \
 -Dspring-boot.run.jvmArguments="-Xdebug \
 -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" \
 --projects datavault-broker
