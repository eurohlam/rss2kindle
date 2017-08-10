#!/bin/bash

FILE_PATH=$1
KINDLE_HOME=$2
CURRENT_DIR=$(pwd)
echo "INFO: Running kindlegen for ${FILE_PATH}. Current directory ${CURRENT_DIR}">>logs/kindle.log

if [ -z ${KINDLE_HOME} ]; then
    KINDLE_HOME="."
fi

if [ ! -f ${KINDLE_HOME}/kindlegen ]; then
    echo "ERROR: kindlegen does not exist in folder ${KINDLE_HOME}"
    exit -2;
fi

if [ -z ${FILE_PATH} ]; then
    echo "ERROR: Empty path">>logs/kindle.log;
    exit -2;
else 
    #check if $PATH is a directory
    if [ -d ${FILE_PATH} ]; then
        echo "INFO: Scan and convert *.opf in the directory ${FILE_PATH}">>logs/kindle.log ;
        for file in ${FILE_PATH}/*.opf
        do
            echo "INFO: Convert file ${file}">>logs/kindle.log;
            ${KINDLE_HOME}/kindlegen ${file} -c1 -c0 -locale ru -verbose >>logs/conversion.out;
        done
    else
        echo "INFO: Convert file ${CURRENT_DIR}/${FILE_PATH}" >>logs/kindle.log;
        ${KINDLE_HOME}/kindlegen ${CURRENT_DIR}/${FILE_PATH} -c1 -c0 -locale ru -verbose >>logs/conversion.out;
    fi
fi

echo "INFO: Conversion is done" >>logs/kindle.log


