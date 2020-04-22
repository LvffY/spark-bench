# Utilisation SNCF

Pour les tests SNCF, nous avons écrits plusieurs Workload permettant d'exécuter des tests. 

## Compilation

Pour compiler votre code vous pouvez faire 
```console
sbt assembly
```

## Fichiers et architecture 

Une fois [votre code compilé](#compilation) afin d'exécuter les workloads souhaités il faut sur votre environnement de tests : 

- Les jars générés par la compilation présents sous **target/assembly** _en local_ de votre code.
- Les fichiers de confs présents sous **[examples](examples)** _en local_
- Les fichiers présents sous


Sur votre environnement de tests, vous devez donc avoir (*au moins*) une architecture similaire à :
```console
├── bin
│   ├── spark-bench-env.sh.template
│   └── spark-bench.sh
├── examples
│   ├── csv-vs-parquet.conf
│   ├── CSVGenerator.conf
│   ├── livy-example.conf
│   ├── local-livy-example.conf
│   ├── minimal-example.conf
│   ├── no-output-example.conf
│   └── sparkpi.conf
├── target
│   └── assembly
│       ├── spark-bench-2.3.0_0.4.0-RELEASE.jar
│       └── spark-bench-launch-2.3.0_0.4.0-RELEASE.jar

```

## Exécution des tests

### SparkPi

Un des premiers tests a effectué est l'exécution de la fonction [SparkPi](https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/SparkPi.scala). Cette méthode peut être assez gourmande en CPU. 

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/sparkpi.conf
```

### Ecriture de données

Il peut être important de faire des tests pour tester la rapidité d'écriture de données avec Spark.  

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/CSVGenerator.conf
```

Pour concaténer les fichiers générés vous pouvez utiliser une fonction analogue à celle-ci : 

```console
hadoop jar /usr/hdp/current/hadoop-mapreduce-client/hadoop-streaming.jar -Dmapred.reduce.tasks=1 -input /tmp/spark-bench/data/default-generator/custom/100000000.csv -output /tmp/spark-bench/data/default-generator/custom/100000000_concatenate.csv -mapper cat -reducer cat
```
