#!/bin/bash

source "$(dirname $0)/config.sh"

RMI_POLICY_SERVER="$(mktemp /tmp/server.policy.XXXXXX)"
cat > "$RMI_POLICY_SERVER" << EOF
grant {
    permission java.security.AllPermission;
};
EOF
trap "rm -f $RMI_POLICY_SERVER" EXIT

rmiregistry &
RMI_REGISTRY_PID=$!
trap "kill $RMI_REGISTRY_PID" EXIT

java \
    -Djava.security.policy="$RMI_POLICY_SERVER" \
    -Djava.rmi.server.codebase="$RMI_CODEBASE" \
    -Djava.rmi.server.hostname="$RMI_SERVER_HOSTNAME" \
    "${PACKAGE_PREFIX}runnable.agent.CAAgentProcess" "$@"
