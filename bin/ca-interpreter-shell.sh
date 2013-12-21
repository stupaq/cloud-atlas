#!/bin/sh

source "$(dirname $0)/ca-commons.sh"

java \
    -ea \
    "${PACKAGE_PREFIX}module.zonemanager.shell.ExampleShellTest" \
    "$@"
