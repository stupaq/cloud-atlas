#!/bin/sh

source "$(dirname $0)/config.sh"

java "${PACKAGE_PREFIX}module.zonemanager.shell.ExampleShellTest" "$@"
