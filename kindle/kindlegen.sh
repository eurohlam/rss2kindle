#!/bin/bash

FILE_PATH=$1
KINDLE_HOME=$2
CURRENT_DIR=$(pwd)
KINDLE_LOG=logs/rss-2-kindle-kindlegen.log
CONVERSION_LOG=logs/rss-2-kindle-kindlegen-conversion.log

echo "INFO: Running kindlegen for ${FILE_PATH}. Current directory ${CURRENT_DIR}">>${KINDLE_LOG}

if [ -z ${KINDLE_HOME} ]; then
    KINDLE_HOME="."
fi

if [ ! -f ${KINDLE_HOME}/kindlegen ]; then
    echo "ERROR: kindlegen does not exist in folder ${KINDLE_HOME}"
    exit -2;
fi

if [ -z ${FILE_PATH} ]; then
    echo "ERROR: Empty path">>${KINDLE_LOG};
    exit -2;
else 
    #check if $PATH is a directory
    if [ -d ${FILE_PATH} ]; then
        echo "INFO: Scan and convert *.opf in the directory ${FILE_PATH}">>${KINDLE_LOG};
        for file in ${FILE_PATH}/*.opf
        do
            echo "INFO: Convert file ${file}">>${KINDLE_LOG};
            ${KINDLE_HOME}/kindlegen ${file} -c1 -c0 -locale ru -verbose >>${CONVERSION_LOG};
        done
    else
        echo "INFO: Convert file ${CURRENT_DIR}/${FILE_PATH}" >>${KINDLE_LOG};
        ${KINDLE_HOME}/kindlegen ${CURRENT_DIR}/${FILE_PATH} -c1 -c0 -locale ru -verbose >>${CONVERSION_LOG};
    fi
fi

echo "INFO: Conversion is done" >>${KINDLE_LOG}


