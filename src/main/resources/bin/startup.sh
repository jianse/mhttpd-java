#!/bin/sh
log4jpath="./../conf/log4j2.xml"
if test -e $log4jpath;then
	args="$args -Dlog4j.configurationFile=$log4jpath"
	echo $args
fi
	java $args -jar mhttpd* 
