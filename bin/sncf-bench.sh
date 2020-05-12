#!/bin/bash
set -eu

execdate=$(date +'%Y%m%d%H%M%S')

for file in $(find ./examples/SNCF -type f -name *.conf | sort); do
  exec=$(find . -name spark-bench.sh)
  chmod +x $exec
  echo "$exec $file"
  $exec $file 2>"spark-bench-$(basename $file .conf)-$execdate-spark.log" | tee "spark-bench-$(basename $file .conf)-$execdate.log"
done

hdfs dfs -mkdir -p /tmp/spark-bench/logs/$execdate
hdfs dfs -put -f *.log /tmp/spark-bench/logs/$execdate/
rm -rf *.log
