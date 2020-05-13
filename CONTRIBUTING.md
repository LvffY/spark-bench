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
- Les fichiers de confs présents sous **[examples/SNCF](examples/SNCF)** _en local_
- Les fichiers présents sous **[bin](bin)** sont tous les shells permettant d'exécuter les différents tests

Sur votre environnement de tests, vous devez donc avoir (*au moins*) une architecture similaire à :
```console
bin
├── sncf-bench.sh
├── spark-bench-env.sh.template
└── spark-bench.sh
target
└── assembly
    ├── spark-bench-2.3.0_0.4.0-RELEASE.jar
    └── spark-bench-launch-2.3.0_0.4.0-RELEASE.jar
examples
└── SNCF
    ├── ADL
    │   ├── 1000CSVGenerator.conf
    │   ├── 1000CSVReader.conf
    │   ├── 100CSVGenerator.conf
    │   ├── 100CSVReader.conf
    │   ├── 10CSVGenerator.conf
    │   ├── 10CSVReader.conf
    │   ├── 1CSVGenerator.conf
    │   └── 1CSVReader.conf
    ├── sparkpi.conf
    └── WASB
        ├── 1000CSVGenerator.conf
        ├── 1000CSVReader.conf
        ├── 100CSVGenerator.conf
        ├── 100CSVReader.conf
        ├── 10CSVGenerator.conf
        ├── 10CSVReader.conf
        ├── 1CSVGenerator.conf
        └── 1CSVReader.conf
```

## Exécution des tests

### Releases

Toutes les releases sont disponibles sous le dossier [dist](dist). Pour utiliser les commandes ci-dessous vous devez d'abord dézipper la release souhaitée : 

En fonction de votre environnement, il faut changer les infos suivantes : 

- Le blob storage à utiliser pour les résultats ainsi que les tests de lecture/écriture : 
```console
sed -i 's/spark-bench@allstoragesv2.blob.core.windows.net/blobName@storageAccountName.blob.core.windows.net/' examples/SNCF/*.conf examples/SNCF/*/*.conf
```


- Le datalake à utiliser pour les test de lecture/écriture : 
```console
sed -i 's/cdcbigdataall.azuredatalakestore.net/dataLakeName.azuredatalakestore.net/' examples/SNCF/*.conf examples/SNCF/*/*.conf
```

### Tous les tests

Tous les tests décrits ci-dessous peuvent être exécutés via la commande suivante : 

```console
./bin/sncf-bench.sh
```

***N.B :*** Chacune des lignes de commandes décrites ci-dessous peuvent être suivies de la ligne `2>file.txt` pour ne garder dans la console que la sortie standard i.e les logs générées par l'exécution en cours. 

### SparkPi

Un des premiers tests a effectué est l'exécution de la fonction [SparkPi](https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/SparkPi.scala). Cette méthode peut être assez gourmande en CPU. 

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/sparkpi.conf
```

### Génération de données

Il peut être important de faire des tests pour tester la rapidité d'écriture de données avec Spark.

#### CSV

##### Ecriture sur un blob storage 

###### 1 Fichier

Nous voulons tester l'écriture de données lorsque nous forçons Spark à n'écrire qu'un fichier en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/WASB/1CSVGenerator.conf
```

###### 10 Fichiers

Nous voulons tester l'écriture de données lorsque nous forçons Spark à écrire 10 fichiers en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/WASB/10CSVGenerator.conf
```

###### 100 Fichiers

Nous voulons tester l'écriture de données lorsque nous forçons Spark à écrire 100 fichiers en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/WASB/100CSVGenerator.conf
```

###### 1000 Fichiers

Nous voulons tester l'écriture de données lorsque nous forçons Spark à écrire 1000 fichiers en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/WASB/1000CSVGenerator.conf
```

##### Ecriture sur un data lake

###### 1 Fichier

Nous voulons tester l'écriture de données lorsque nous forçons Spark à n'écrire qu'un fichier en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/ADL/1CSVGenerator.conf
```

###### 10 Fichiers

Nous voulons tester l'écriture de données lorsque nous forçons Spark à écrire 10 fichiers en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/ADL/10CSVGenerator.conf
```

###### 100 Fichiers

Nous voulons tester l'écriture de données lorsque nous forçons Spark à écrire 100 fichiers en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/ADL/100CSVGenerator.conf
```

###### 1000 Fichiers

Nous voulons tester l'écriture de données lorsque nous forçons Spark à écrire 1000 fichiers en sortie

Pour exécuter ce code : 
```console
./bin/spark-bench.sh examples/SNCF/ADL/1000CSVGenerator.conf
```

### Lecture de fichier

Il peut être important de faire des tests pour tester la rapidité de lecture de données avec Spark sur des données non caché, chaché sur disque ou caché sur ram.

#### CSV

Dans un premier temps, nous effectuons des tests uniquement sur des jeux de données CSV.

##### Vérification des entrants

Il est important de vérifier avant de lancer les tests de lecture que les tests d'écriture se sont bien passés. Pour éviter, certains biais nous nous basons uniquement sur les fichiers générés avec les plus grosses configurations mémoire : "big".

###### 1 fichier

```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/data/default-generator/1file/big
```

###### 10 fichiers

```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/data/default-generator/10files/big
```

###### 100 fichiers

```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/data/default-generator/100files/big
```

###### 1000 fichiers

```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/data/default-generator/1000files/big
```

##### Lecture depuis un blob storage

Voici les commandes permettant de lancer les workload des readers avec une spark session par niveau de configuration.

###### 1 fichier

Nous voulons tester la lecture de données sur un unique fichier.

###### Lancement

Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/1CSVReader.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement.
```console
./bin/spark-bench.sh examples/SNCF/1CSVReader.conf 2>1cvsreader.txt
```
###### Vérification des sorties
Vérification des sorties de lecture des 10ko, 1Mo sur 1 fichier avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1file/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 1 fichier avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1file/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go sur 1 fichier avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1file/csv/big/data.csv
```

###### 10 fichiers

Nous voulons tester la lecture de données sur 10 fichiers uniquement.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/10CSVReader.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement.
```console
./bin/spark-bench.sh examples/SNCF/10CSVReader.conf 2>10cvsreader.txt
```
###### Vérification des sorties
Vérification des sorties de lecture des 10ko, 1Mo sur 10 fichiers avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/10files/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 10 fichiers avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/10files/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go, 1To sur 10 fichiers avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/10files/csv/big/data.csv
```

###### 100 fichiers

Nous voulons tester la lecture de données sur 100 fichiers uniquement.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/100CSVReader.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement.
```console
./bin/spark-bench.sh examples/SNCF/100CSVReader.conf 2>100cvsreader.txt
```
###### Vérification des sorties

Vérification des sorties de lecture des 10ko, 1Mo sur 100 fichiers avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/100files/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 100 fichiers avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/100files/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go, 1To sur 100 fichiers avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/100files/csv/big/data.csv
```

###### 1000 fichiers

Nous voulons tester la lecture de données sur 1000 fichiers uniquement.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/1000CSVReader.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement.
```console
./bin/spark-bench.sh examples/SNCF/1000CSVReader.conf 2>1000cvsreader.txt
```
###### Vérification des sorties

Vérification des sorties de lecture des 10ko, 1Mo sur 100 fichiers avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1000files/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 100 fichiers avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1000files/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go, 1To sur 100 fichiers avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1000files/csv/big/data.csv
```

# Lancement des readers modifiés

Voici les commandes permettant de lancer les workload des readers avec une spark session par taille de fichier et type de cache utilisé.

###### 1 fichier

Nous voulons tester la lecture de données sur un unique fichier.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/1CSVReaderSeparate.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement
```console
./bin/spark-bench.sh examples/SNCF/1CSVReaderSeparate.conf 2>1cvsreaderseparate.txt
```
###### Vérification des sorties

Vérification des sorties de lecture des 10ko, 1Mo sur 1 fichier avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1file/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 1 fichier avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1file/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go sur 1 fichier avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1file/csv/big/data.csv
```

###### 10 fichiers

Nous voulons tester la lecture de données sur 10 fichiers uniquement.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/10CSVReaderSeparate.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement
```console
./bin/spark-bench.sh examples/SNCF/10CSVReaderSeparate.conf 2>10cvsreaderseparate.txt
```
###### Vérification des sorties

Vérification des sorties de lecture des 10ko, 1Mo sur 10 fichiers avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/10files/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 10 fichiers avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/10files/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go, 1To sur 10 fichiers avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/10files/csv/big/data.csv
```

###### 100 fichiers

Nous voulons tester la lecture de données sur 100 fichiers uniquement.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/100CSVReaderSeparate.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement
```console
./bin/spark-bench.sh examples/SNCF/100CSVReaderSeparate.conf 2>100cvsreaderseparate.txt
```
###### Vérification des sorties

Vérification des sorties de lecture des 10ko, 1Mo sur 100 fichiers avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/100files/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 100 fichiers avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/100files/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go, 1To sur 100 fichiers avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/100files/csv/big/data.csv
```

###### 1000 fichiers

Nous voulons tester la lecture de données sur 1000 fichiers uniquement.

###### Lancement
Lancement avec logs spark affichés sur la console
```console
./bin/spark-bench.sh examples/SNCF/1000CSVReaderSeparate.conf
```
Lancement avec les logs générés par l'exécution en cours uniquement
```console
./bin/spark-bench.sh examples/SNCF/1000CSVReaderSeparate.conf 2>1000cvsreaderseparate.txt
```
###### Vérification des sorties

Vérification des sorties de lecture des 10ko, 1Mo sur 100 fichiers avec les configurations par defaut.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1000files/csv/default/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go sur 100 fichiers avec les configurations moyennes.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1000files/csv/custom/data.csv
```
Vérification des sorties de lecture des 10ko, 1Mo, 1Go, 100Go, 1To sur 100 fichiers avec les configurations big.
```console
hdfs dfs -ls wasbs://spark-bench@allstoragesv2.blob.core.windows.net/spark-bench/v1.1/output/default-reader/1000files/csv/big/data.csv
```

######