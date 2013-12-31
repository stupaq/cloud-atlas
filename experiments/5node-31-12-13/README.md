Experiment 2013/12/31
=====================

Initial setup
-------------
The experiment was conducted using provided configuration for five nodes and a single local client serving all agent
processes. We were interested in `num_processes` attribute value in `/`, `/uw` and `/pjwstk` zones. Scribing interval
was set to 10s with little to no other activities on the machines.

The experiment
--------------
Nodes were started sequentially with little to no lag.
After couple minutes of operation (exact time can be restored from time series records) the agent serving `/uw/khaki31`
was killed and restarted after approximately two minutes.
After another few minutes the agent serving `/uw/violet07`, and shortly after, the one serving `/pjwstk/whatever01` were
killed for approximately three minutes.
Once system came back to the balance nodes were killed one after another in the following order:
    +   `/uw/khaki13`
    +   `/uw/khaki31`
    +   `/uw/violet07`
    +   `/pjwstk/whatever01`
    +   `/pjwstk/whatever02`
Through continuous observation of zone hierarchy snapshots during the course of the experiment it was determined that
approximate time from restarting a node to the moment when all interested nodes interchange updates with restarted node
is equal to 2-3 gossiping rounds (that means around 12s).
Note that nodes were initially started with a single fallback contact, in some cases pointing to itself (thus completely
unusable). Extremely simple contacts election "algorithm" (that means "take five random contacts from subzones") served
well.

Experiment results
------------------
Time series files can be found under appropriate subdirectory (one for each agent instance serving different leaf zone)
of `scribe/`. Additionally four full zone hierarchy snapshots were taken for each of the agents during the course of the
experiment, you can find them under appropriate `dump_hh-mm/` subdirectories, where `hh` and `mm` denote hour and minute
time, the snapshot has been taken.
For viewing time series file please use provided `ts_plot.sh` script (it requires `gnuplot` to be installed).
For example to show time series for agent serving `/uw/pjwstk/khaki31`, attribute `num_processes` in a zone `/` run
`ts_plot.sh khaki31 / num_processes`. Note the lack of data points around 0130 hours, the agent was dead at this time
as described previously.

