#!/bin/sh

if [[ $# -ne 1 ]]; then
    echo "Missing argument: zone_name"
    exit 1
fi

zone_name="$1"
config_file="${CONFIGS_DIR:?Variable CONFIGS_DIR must be set!}/$zone_name/`basename ${0%.*}.ini`"

mkdir -p `dirname ${config_file}`
egrep '^[^;].*$' > ${config_file} <<EOF
;;; Introduction.
; This configuration file controls local client's queries installer service.
; The service periodically reads this file and installs/removes/updates queries on the agent.
; It is important to understand that if a query is not mentioned in this file, it will not be
; altered in any way, unless you specify replace_all (described later).
; That being said, if you want to remove the query - set enabled to false.

;;; Config file format explanation.
; Determines whether all existing queries are removed when starting session with the agent.
; Defaults to false. It is advised to set it to true - you never know which queries were
; previously installed on the agent.
;   replace_all = true

; Each query is represented by a section, whose name denotes a name of the query.
;   [&contacts_election]

; Attribute query denotes the query string.
;   query = "SELECT to_set(random(5, unfold(contacts))) AS contacts"

; You can remove the query by setting enabled attribute to false (it defaults to true).
;   enabled = false

; Specifying zones attribute will install the query in a zone determined by its value.
; If you do not specify zones attribute the query will be installed in all zones but the leaf one.
;   zones = /
;   zones = /uw
;   zones = /uw/violet
; You can specify zones attribute multiple times, the query will be installed in each
; specified zone. You can change zones specification for any query (not only the new ones),
; queries installer will install/remove queries accordingly.

;;; Skeleton config.

[&contacts_election]
    query = "SELECT to_set(random(5, unfold(contacts))) AS contacts"

[&num_processes]
    query = "SELECT sum(num_processes) AS num_processes"
EOF
