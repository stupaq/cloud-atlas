#!/bin/bash

source "$(dirname $0)/ca-commons.sh"

RMI_POLICY_SERVER="$(mktemp /tmp/server.policy.XXXXXX)"
cat > "$RMI_POLICY_SERVER" << EOF
grant {
    permission java.security.AllPermission;
};
EOF
trap "rm -f $RMI_POLICY_SERVER" EXIT

ps cax | grep rmiregistry &>/dev/null
if [[ $? -ne 0 ]]; then
    rmiregistry &
    RMI_REGISTRY_PID=$!
    trap "kill $RMI_REGISTRY_PID" EXIT
fi

java \
    -ea \
    -Djava.security.policy="$RMI_POLICY_SERVER" \
    -Djava.rmi.server.codebase="$RMI_CODEBASE" \
    -Djava.rmi.server.hostname="$RMI_SERVER_HOSTNAME" \
    "${PACKAGE_PREFIX}runnable.GenericServiceRunner" \
    "${PACKAGE_PREFIX}runnable.agent.CAAgentProcess" \
    "$@"
