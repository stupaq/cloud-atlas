#!/bin/bash

if [[ $# -ne 3 ]]; then
  echo "Missing arguments: <agent_leaf_name> <zone_global_name> <attribute>"
  exit 1
fi

source="scribe/$1/$2/$3".txt
file_path="$(mktemp /tmp/ts_plot.XXXXXX)"
trap "rm -f $file_path" EXIT

sed -e 's/ : integer$//g' ${source} > $file_path

export attribute="$3"
export file_path
gnuplot -p ts_plot.gp
