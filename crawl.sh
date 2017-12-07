#!/bin/sh
cmdArgs=${1}
if test "$#" -lt 1 ; then
	echo "Missing seed URL"
	echo "Usage: crawl.sh <seedURL> <Output File>"
	echo "Example: crawl.sh \"http://google.com\""
	echo "Example: crawl.sh \"http://google.com myOutput.log\""
else 
	mvn exec:java -Dexec.mainClass=com.fhd.webcrawler.Main  -Dexec.args=$cmdArgs
fi

