#!/bin/bash

BIN_DIR="$(dirname $0)"
ROOT_DIR="$BIN_DIR/.."
SKELETONS_DIR="$ROOT_DIR/skeletons"
CONFIGS_DIR="$ROOT_DIR/configs"

CP_FILE="$ROOT_DIR/classpath"

PACKAGE_PREFIX="stupaq.cloudatlas."

RMI_CODEBASE="file:/$ROOT_DIR/src/main/java/"
RMI_SERVER_HOSTNAME="localhost"

export CLASSPATH="$ROOT_DIR/target/classes:$ROOT_DIR/target/*:`cat $CP_FILE 2>/dev/null`"
