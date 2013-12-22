#!/bin/bash

BIN_DIR="$(pwd)/$(dirname $0)"
ROOT_DIR="$BIN_DIR/.."
CP_FILE="$ROOT_DIR/classpath"

PACKAGE_PREFIX="stupaq.cloudatlas."

RMI_CODEBASE="file:/$ROOT_DIR/src/main/java/"
RMI_SERVER_HOSTNAME="localhost"


export CLASSPATH="$ROOT_DIR/target/classes:$ROOT_DIR/target/*:`cat $CP_FILE 2>/dev/null`"
echo -e "CLASSPATH:\n$CLASSPATH"
