#!/bin/bash

PATH=$1
KINDLE_HOME="."

if [ -z ${PATH} ]; then
    echo "Empty path">>logs/kindle.log;
    exit -2;
else 
    #check if $PATH is a directory
    if [ -d ${PATH} ]; then
	echo "Scan and convert *.htm in the directory ${PATH}">>logs/kindle.log ;
	for file in ${PATH}/*.htm
	do
	    echo "Convert file ${file}">>logs/kindle.log;
	    ${KINDLE_HOME}/kindlegen ${file} -c1 -c0 -locale ru -verbose >>logs/conversion.out;
	done
    else
	echo "Convert file ${PATH}" >>logs/kindle.log;
	${KINDLE_HOME}/kindlegen ${PATH} -c1 -c0 -locale ru -verbose >>logs/conversion.out;
    fi
fi

echo "Conversion is done" >>logs/kindle.log


