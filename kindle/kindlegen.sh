#!/bin/bash

PATH=$1
#KIDNLE_HOME has to be defined
KINDLE_HOME=""

if [ -z ${KINDLE_HOME} ]; then
    echo "Empty KINDLE_HOME";
    exit -2;
fi

if [ -z ${PATH} ]; then
    echo "Empty path";
    exit -2;
else 
    if [ -d ${PATH} ]; then
	echo "Scan and convert *.htm in ${PATH}";
	for file in ${PATH}/*.htm
	do
	    echo "Convert file ${file}";
	    ${KINDLE_HOME}/kindlegen ${file} -c1 -c0 -locale ru -verbose >conversion.out;
	done
    else
	echo "Convert ${PATH}";
	${KINDLE_HOME}/kindlegen ${PATH} -c1 -c0 -locale ru -verbose >conversion.out;
    fi
fi

echo "Conversion is done"


