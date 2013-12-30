#!/bin/sh

if [[ $# -ne 2 ]]; then
    echo "Missing arguments: zone_name bind_port"
    exit 1
fi

zone_name="$1"
bind_port="$2"
config_file="${CONFIGS_DIR:?Variable CONFIGS_DIR must be set!}/$zone_name/`basename ${0%.*}.ini`"

mkdir -p `dirname ${config_file}`
egrep '^[^;].*$' > ${config_file} <<EOF
;;; Introduction.
; This configuration file controls all services running on a single agent.
; All properties describing periods, intervals or other time-related values must be specified in
; milliseconds unless otherwise noted.

;;; Obligatory configuration section for zone manager service.
[zones]
; Obligatory leaf zone global name that the agent refers to.
    zone_name = ${zone_name}
; Determines how often queries values should be recomputed, defaults to 5s.
;   reevaluation_interval = 5000
; Interval after which zone will be removed if not contact gossiping for its behalf is contacted.
; Defaults to 60s.
;   purge_interval = 60000
; Optional path to a file which will be periodically updated to reflect current zone hierarchy as
; seen by the agent. If one specifies a directory instead of a file, the agent will create a
; file under this directory using the value of zone_name as a relative path.
    hierarchy_dump_file = dumps/`basename ${zone_name}`
; Mentioned file is actually a memory mapped file, the following property controls its size in
; bytes, zone hierarchy dump will be truncated to specified size. Defaults to 16kB.
;   hierarchy_dump_size = 16384

;;; Optional configuration section for RMI server.
; If this section is not present then the agent wil not start the service (that means no
; client can connect to this agent).
[rmi]
; A host running RMI registry, that the server should export RMI handlers to.
    host = localhost
; A port of the RMI registry.
;   port = 1099
; A handler that agent's RMI server bound to. Defaults to the value of zone_name property
; in zones section.
;   handle = ${zone_name}

;;; Optional configuration section for gossiping module server.
; If this section is not present then the agent wil not start the service (that means no
; gossiping can be made between this agent and the world).
[gossiping]
; Obligatory port that this agent listens on for gossiping requests.
    bind_port = ${bind_port}
; Determines how often gossiping should be initiated by the agent, defaults to 5s.
    period = 5000
; Determines how long to wait before retransmitting a single gossip if remote contact does not
; respond, defaults to 300ms.
    retry_delay = 500
; Determines how many times a single gossip should be retransmitted if remote contact does not
; respond, defaults to 3.
    retry_count = 3

;;; Optional configuration section for gossiping protocol internals.
; Inexperienced user should rely on defaults.
[gossiping_internals]
; TODO

;;; Optional configuration section for contact selection strategy
[contact_selection]
; Determines which plugin should be used as level selection strategy, defaults to UniformLevel.
;   level_selection = UniformLevel
; Determines which plugin should be used as zone selection strategy, defaults to RandomZone.
;   zone_selection = RoundRobinZones

;;; Optional configuration section for plugins-specific configuration.
[plugin]
;; ExponentialLevel
; Mean value of exponential distribution, defaults to 0.5.
;   exponential_level_mean = 0.5
EOF
