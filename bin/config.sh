#!/bin/bash

BIN_DIR="$(pwd)/$(dirname $0)"
ROOT_DIR="$BIN_DIR/.."

PACKAGE_PREFIX="stupaq.cloudatlas."

RMI_CODEBASE="file:/$ROOT_DIR/src/main/java/"
RMI_SERVER_HOSTNAME="localhost"

export CLASSPATH="$ROOT_DIR/target/*"
