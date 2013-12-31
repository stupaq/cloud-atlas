#!/bin/sh

source "$(dirname $0)/ca-commons.sh"

export CONFIGS_DIR
export RMI_SERVER_HOSTNAME
export RMI_SERVER_PORT

gen_agent="$SKELETONS_DIR/CAAgentProcess.sh"
gen_client="$SKELETONS_DIR/CALocalClientProcess.sh"
gen_queries="$SKELETONS_DIR/CAQueriesInstaller.sh"

port_seed="$PORT_RANGE_PREFIX"

# /pjwstk/whatever01
$gen_agent /pjwstk/whatever01 "localhost" "${port_seed}01"
$gen_client /pjwstk/whatever01 "localhost:${port_seed}02"
$gen_queries /pjwstk/whatever01

# /pjwstk/whatever02
$gen_agent /pjwstk/whatever02 "localhost" "${port_seed}02"
$gen_client /pjwstk/whatever02 "localhost:${port_seed}07"
$gen_queries /pjwstk/whatever02

# /uw/khaki13
$gen_agent /uw/khaki13 "localhost" "${port_seed}13"
$gen_client /uw/khaki13 "localhost:${port_seed}31"
$gen_queries /uw/khaki13

# /uw/khaki31
$gen_agent /uw/khaki31 "localhost" "${port_seed}31"
$gen_client /uw/khaki31 "localhost:${port_seed}07"
$gen_queries /uw/khaki31

# /uw/violet07
$gen_agent /uw/violet07 "localhost" "${port_seed}07"
$gen_client /uw/violet07 "localhost:${port_seed}02"
$gen_queries /uw/violet07
