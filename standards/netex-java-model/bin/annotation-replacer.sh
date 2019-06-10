#!/bin/bash

# Replaces annotations in NetEX xmls
# See https://java.net/jira/browse/JAXB-420

if ! type xmlstarlet > /dev/null;
    then echo "you need xmlstarlet for this to run";
    exit 1;
fi

NETEX_VERSION=$1
if [ -z $NETEX_VERSION ]; then
    echo "USAGE: $0 <netex_version>"
    exit 1
fi

XSD_FOLDER="./src/main/resources/xsd/${NETEX_VERSION}/"
find $XSD_FOLDER -name "*.xsd" -exec xmlstarlet  ed --inplace  -d "//xsd:annotation" {} \;
