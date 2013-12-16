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

License and copyright notices
-----------------------------
Copyright (c) 2013 Mateusz Machalica
