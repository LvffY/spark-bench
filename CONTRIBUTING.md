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

### Génération de données

Il peut être important de faire des tests pour tester la rapidité d'écriture de données avec Spark.

#### CSV

##### 1 Fichier

Nous voulons tester l'écriture de données lorsque vous forçons Spark à n'écrire qu'un fichier en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/1CSVGenerator.conf
```

***N.B :*** *Dans le cas particulier de la génération de 1To de données, Spark avec 1 partition a des problèmes. Donc ce que nous faisons c'est générér 10 fichiers de 100Go. Il ne reste plus qu'à les concaténer par :*

```console
hadoop jar /usr/hdp/current/hadoop-mapreduce-client/hadoop-streaming.jar -Dmapred.reduce.tasks=1 -Dmapreduce.reduce.memomry.mb=5072 -input /tmp/spark-bench/data/default-generator/1file/big/100Go.csv -output /tmp/spark-bench/data/default-generator/1file/big/100000000_concatenate.csv -mapper cat -reducer cat
```

##### 10 Fichiers

Nous voulons tester l'écriture de données lorsque vous forçons Spark à n'écrire qu'un fichier en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/10CSVGenerator.conf
```

##### 100 Fichiers

Nous voulons tester l'écriture de données lorsque vous forçons Spark à n'écrire qu'un fichier en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/100CSVGenerator.conf
```

##### 1000 Fichiers

Nous voulons tester l'écriture de données lorsque vous forçons Spark à n'écrire qu'un fichier en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/1000CSVGenerator.conf
```
