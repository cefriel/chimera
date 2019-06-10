#!/bin/bash

# Replaces the netex version in the bindings file.

NETEX_VERSION=$1
: ${BINDINGS_FILE:="./bindings.xjb"}

if [ -z $NETEX_VERSION ]; then
    echo "USAGE: $0 <netex_version>"
    exit 1
fi

echo "Updating bindings file with version ${NETEX_VERSION}"

sed -i -E "s/resources\/xsd\/[0-9]\.[0-9]+(\.[0-9])*\//resources\/xsd\/${NETEX_VERSION}\//" ${BINDINGS_FILE}