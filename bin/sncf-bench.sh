#!/bin/bash
set -eu

##
#
# Nom : executeSparkBench
# Fonctionnalité(s) :
#	- Execution d'un lot de Spark-bench
#
#
# Paramètres :
#   - Pattern des fichiers de confs à exécuter e.g *Generator.conf
# 	- Date d'exécution courante
#   - Dossier dans lequel chercher les confs. Comme on utilise la fonction find, ce dossier peut être à haut niveau la recherche sera récursive. Par défaut, vaut ./exemples/SNCF.
#
#
# Retour(s) :
#	- Aucun
##
function executeSparkBench() {
  local filePattern=$1
  local execDate=$2
  local confDirectory=${3:-'./examples/SNCF'}

  for file in $(find $confDirectory -type f -name $filePattern | sort); do
    exec=$(find . -name spark-bench.sh)
    chmod +x $exec
    echo "Execute $exec $file"
    $exec $file 2>"spark-bench-$(basename $file .conf)-$execDate-spark.log"
  done
}

execdate=$(date +'%Y%m%d%H%M%S')

## Execute SparkPi
executeSparkBench sparkpi.conf $execdate

## Execute Generator bench
executeSparkBench *Generator.conf $execdate

## Execute Reader bench
executeSparkBench *Reader.conf $execdate

hdfs dfs -mkdir -p /tmp/spark-bench/logs/$execdate
hdfs dfs -put -f spark-bench-*-$execdate-spark.log /tmp/spark-bench/logs/$execdate/
rm -rf spark-bench-*-spark.log
