#!/bin/sh
gen_agent=`dirname $0`/CAAgentProcess.sh
gen_client=`dirname $0`/CALocalClientProcess.sh
gen_queries=`dirname $0`/CAQueriesInstaller.sh

port_seed=11
host_seed=localhost:${port_seed}

# /pjwstk/whatever01
$gen_agent /pjwstk/whatever01 ${port_seed}01
$gen_client /pjwstk/whatever01 ${host_seed}02
$gen_queries /pjwstk/whatever01

# /pjwstk/whatever02
$gen_agent /pjwstk/whatever02 ${port_seed}02
$gen_client /pjwstk/whatever02 ${host_seed}07
$gen_queries /pjwstk/whatever02

# /uw/khaki13
$gen_agent /uw/khaki13 ${port_seed}13
$gen_client /uw/khaki13 ${host_seed}31
$gen_queries /uw/khaki13

# /uw/khaki31
$gen_agent /uw/khaki31 ${port_seed}31
$gen_client /uw/khaki31 ${host_seed}07
$gen_queries /uw/khaki31

# /uw/violet07
$gen_agent /uw/violet07 ${port_seed}07
$gen_client /uw/violet07 ${host_seed}02
$gen_queries /uw/violet07
