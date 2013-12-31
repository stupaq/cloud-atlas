#!/usr/bin/gnuplot

unset key
set grid

set style data fsteps

set xlabel "Time"
set xdata time
set timefmt "%s"
set format x "%m/%d/%Y %H:%M:%S"

set ylabel "`echo ${attribute}`"

plot "`echo ${file_path}`" using ($1/1000):2 with linespoints
