#!/bin/bash
set -eu

execdate=$(date +'%Y%m%d%H%M%S')

for file in $(find ./examples/SNCF -type f -name *Generator.conf -o -name *Separate.conf | sort); do
  exec=$(find . -name spark-bench.sh)
  chmod +x $exec
  echo "Execute $exec $file"
  $exec $file 2>"spark-bench-$(basename $file .conf)-$execdate-spark.log"
done

hdfs dfs -mkdir -p /tmp/spark-bench/logs/$execdate
hdfs dfs -put -f spark-bench-*-$execdate-spark.log /tmp/spark-bench/logs/$execdate/
rm -rf spark-bench-*-spark.log
