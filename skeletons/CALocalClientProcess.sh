#!/bin/sh

if [[ $# -ne 2 ]]; then
    echo "Missing arguments: zone_name fallback_contacts"
    exit 1
fi

zone_name="$1"
fallback_contacts="$2"
config_file="${CONFIGS_DIR:?Variable CONFIGS_DIR must be set!}/$zone_name/`basename ${0%.*}.ini`"

mkdir -p `dirname ${config_file}`
egrep '^[^;].*$' > ${config_file} <<EOF
;;; Introduction.
; This configuration file controls all services running on a local client.
; Single config file specifies single agent that client connects to.
; Client can read arbitrary number of config files and, as a consequence,
; connect to arbitrary number of agents.
; All properties describing periods, intervals or other time-related values must be specified in
; milliseconds unless otherwise noted.

;;; Obligatory configuration section for RMI client.
[rmi]
; A host running RMI registry, that agent's RMI server bound to, defaults to 127.0.0.1
    host = ${RMI_SERVER_HOSTNAME}
; A port of the RMI registry.
    port = ${RMI_SERVER_PORT}
; A handler that agent's RMI server bound to. Defaults to the value of zone_name property
; in collector section.
;   handle = ${zone_name}

;;; Optional configuration section for attributes collector service.
; If this section is not present then local client wil not start the service (that means no
; attributes will be collected and pushed to the agent).
[collector]
; An obligatory global name of the zone on behalf of which the attributes are collected.
    zone_name = ${zone_name}
; A set of fallback contacts, defaults to empty set.
    fallback_contacts = ${fallback_contacts}
; Interval between attributes collection, defaults to 5s.
;   push_interval = 5000
; Path to collector script, defaults to a location under libexec/ directory in project's tree.
;   script = libexec/collector-script.sh

;;; Optional configuration section for attributes collector service.
; If this section is not present then local client wil not start the service (that means no
; queries will be installed/removed at the agent).
[queries_installer]
; Obligatory path to the file that contains configuration for queries queries_installer service.
    queries_file = `dirname ${config_file}`/CAQueriesInstaller.ini

;;; Optional configuration section for attributes collector service.
; Scribe service collects values of selected attributes in selected zones and writes them as
; a time sequence to local files. If this section is not present then local client wil not
; start the service (that means no attributes will be collected from the agent).
[scribe]
; Obligatory root directory for time sequence files.
    records_directory = scribe/`basename ${zone_name}`
; Interval between collecting attributes from the agent, defaults to 10s.
;   fetch_interval = 10000
; Specification of an attribute in a zone (collectively - an entity) to collect.
; Entity specification consists of an attribute name (the part after last slash) and a zone's
; global name (the part before last slash).
    entities = /num_processes
    entities = /uw/num_processes
    entities = /pjwstk/num_processes
; By specifying entities multiple times, you can tell the service to collect multiple attributes
; at the same time.
EOF
