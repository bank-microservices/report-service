#!/bin/sh

echo "The application will start in ${START_TIME_SLEEP}s..." && sleep ${START_TIME_SLEEP}
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.nttdata.microservices.report.ReportServiceApplication"  "$@"
