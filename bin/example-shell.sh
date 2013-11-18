#!/bin/sh

PACKAGE_PREFIX="stupaq.cloudatlas."

java -cp "target/*" "${PACKAGE_PREFIX}interpreter.shell.ExampleShell" $@
