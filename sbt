#!/bin/bash
args=$@
SBT_OPTS="-Xms2G -Xmx4G -Xss4M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=4G -Dsbt.override.build.repos=true"
echo 'You can use "df" for debug from first, "dm" to debug in the middle, "pr" to use proxy by SOCKSSERVER'
for var in "$@"
do
    if [[ $var == "df" ]]; then
	SBT_OPTS=$SBT_OPTS" -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 "
	args=${args#$var}
    fi
    if [[ $var == "dm" ]]; then
	SBT_OPTS=$SBT_OPTS" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 "
	args=${args#$var}
    fi
    if [[ $var == "pr" ]]; then
	SBT_OPTS=$SBT_OPTS" -DsocksProxyHost=SOCKSSERVER -DsocksProxyPort=1080 " 
	args=${args#$var}
    fi
    if [[ $var == testCoverage ]]; then
    args="clean coverage test coverageReport coverageAggregate"
    fi

done
if [[ $var == testCoverage ]]; then
    java $SBT_OPTS -jar `dirname $0`/sbt-launch.jar $args
else
    java $SBT_OPTS -jar `dirname $0`/sbt-launch.jar "$args"
fi
