#!/bin/bash

source "$(dirname $0)/ca-commons.sh"

RMI_POLICY_CLIENT="$(mktemp /tmp/client.policy.XXXXXX)"
cat > "$RMI_POLICY_CLIENT" << EOF
grant {
    permission java.security.AllPermission;
};
EOF
trap "rm -f $RMI_POLICY_CLIENT" EXIT

java \
    -ea \
    -Djava.net.preferIPv4Stack=true \
    -Djava.security.policy="$RMI_POLICY_CLIENT" \
    -Djava.rmi.server.codebase="$RMI_CODEBASE" \
    "${PACKAGE_PREFIX}runnable.GenericServiceRunner" \
    "${PACKAGE_PREFIX}runnable.tools.CAQueriesRemovalTool" \
    "$@"
