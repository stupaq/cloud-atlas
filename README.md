CloudAtlas
==========
CloudAtlas is a system for managing large collections of distributed resources.

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
Examples configuration files with very verbose comments can be found under `skeletons/` directory.
It is strongly advised to read them before starting to play around this CloudAtlas system.

CloudAtlas local client
-----------------------
You can start local client using `bin/ca-local-client.sh <config1> <config2> ...` where `<confign>` is a path to
configuration file that local agent uses to connect to CloudAtlas agent.
Note that if the agent is not running or was killed, the client that tried or was connected to the agent will fail.
However, if you start a single client and tell it to connect to multiple agents (by specifying multiple configuration
files), failure to connect to one agent will not disturb the client in serving the other agent.

CloudAtlas agent
----------------
You can start agent by running `bin/ca-agent.sh <config>` where `<config>` is a path to configuration file.
Note that the script will check if there is any RMI registry instance running on the same machine and start own
if there is none. Started instance will be stopped on agents exit. If you wish RMI registry to stay alive after
agent is killed, you should start it yourself.

CloudAtlas queries removal tool
-------------------------------
For testing purposes you can remove all queries installed on any running CloudAtlas agent by running
`ca-queries-removal-tool.sh <address1> <address2> ...` where `<addressn>` is an address (host and optionally port)
of a RMI registry instance that agents bound to to listen for commands from local clients.

License and copyright notices
-----------------------------
Copyright (c) 2013 Mateusz Machalica
