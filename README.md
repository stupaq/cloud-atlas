CloudAtlas
==========
CloudAtlas is a system for managing large collections of distributed resources.

[![Build Status](https://travis-ci.org/stupaq/cloud-atlas.svg?branch=master)](https://travis-ci.org/stupaq/cloud-atlas)

Compilation and installation
----------------------------
There is not much to do at this point. To compile project run `mvn compile`, to build binaries `mvn package`.
All unit and integration tests can be executed with `mvn test`.

CloudAtlas query language
-------------------------
To enter interpreter shell with example zone hierarchy run `bin/ca-interpreter-shell.sh`, the shell accepts multiple
queries, installs them in the hierarchy and prints resulting hierarchy. Note that a query which cannot be parsed will
stop all of the queries from being installed.
Query language grammar is defined in BNF format in `src/QueryLanguage.cf`.
If a query fails to execute in a certain context (a zone in which it was installed) an appropriate error message will be
written to the logger and its results will be discarded, note that query execution for other contexts will not be
affected in any way.

CloudAtlas configuration
------------------------
Example configuration files with very verbose comments can be found under `skeletons/` directory.
It is strongly advised to read them before starting to play around this CloudAtlas system.

CloudAtlas local client
-----------------------
You can start local client using `bin/ca-local-client.sh <config1> <config2> ...` where `<confign>` is a path to
configuration file that local agent uses to connect to CloudAtlas agent.
Note that if the agent is not running or was killed, the client that tried or was connected to the agent will fail.
However, if you start a single client and tell it to connect to multiple agents (by specifying multiple configuration
files), failure to connect to one agent will not disturb the client in serving the other agent.

### Attributes collector ###
A service running within local client that is responsible for periodic collecting attributes about machine that it runs
and pushing them to an associated agent.
Attributes collector is in fact a thin wrapper around a bash script `libexec/collector-script.sh`.
This approach enables easy attributes injection (which script to use can be configured) and usage of UNIX tools that
are just perfect for discovering various system related statistics.
You can find more detailed description in `skeletons/CALocalClientProcess.sh`.

### Attributes scribe ###
A service running within local client that is responsible for periodic fetching of values of specified attributes from
an associated agent and writing them to appropriate file in time series format..
You can find more detailed description in `skeletons/CALocalClientProcess.sh`.

### Queries installer ###
A service running within local client that is responsible for installing/removing/updating queries on associated agent.
You can find more detailed description in `skeletons/CAQueriesInstaller.sh`.

CloudAtlas agent
----------------
You can start agent by running `bin/ca-agent.sh <config>` where `<config>` is a path to configuration file.
Note that the script will check if there is any RMI registry instance running on the same machine and start own if there
is none. Started instance will be stopped on agents exit. If you wish RMI registry to stay alive after agent is killed,
you should start it yourself.
You can find detailed description of configuration properties in `skeletons/CAAgentProcess.sh`.

CloudAtlas queries removal tool
-------------------------------
For testing purposes you can remove all queries installed on any running CloudAtlas agent by running
`bin/ca-queries-removal-tool.sh <address1> <address2> ...` where `<addressn>` is an address (host and optionally port)
of a RMI registry instance that agents bound to to listen for commands from local clients.

Quick start guide to five nodes CloudAtlas instance (also on your Mac)
----------------------------------------------------------------------
Configuration provided under `configs/` directory is sufficient to run five nodes cluster on a local machine.
In case you wish to change the configuration, it is much more convenient to alter scripts under `skeletons/` directory
and run `bin/ca-gen-configs.sh` in order to regenerate configuration.
Provided configuration tells each agent instance to periodically dump zones hierarchy as seen by the agent to
appropriate file under `dumps/` directory.
Additionally, a client will fetch values of `num_processes` attribute in `/`, `/uw`, and `/pjwstk` zones from each node
and record in time series format in appropriate files under `scribe/` directory.
To run the cluster using provided configuration run the following commands (you might want to run each in separate
terminal instead on running them all in a single one using `&`):

    bin/ca-agent.sh configs/uw/khaki13
    bin/ca-agent.sh configs/uw/khaki31
    bin/ca-agent.sh configs/uw/violet07
    bin/ca-agent.sh configs/pjwstk/whatever01
    bin/ca-agent.sh configs/pjwstk/whatever02
    bin/ca-local-client.sh configs/{uw/khaki13,uw/khaki31,uw/violet07,pjwstk/whatever01,pjwstk/whatever02}

To monitor how each agent discovers new zones (or removes them when you kill one of the agents) you can use special
script `bin/cat-hierarchy-dumps.sh dumps/*` that prints all found dumps files side by side.

ENJOY!

License and copyright notices
-----------------------------
Copyright (c) 2013-2014 Mateusz Machalica
