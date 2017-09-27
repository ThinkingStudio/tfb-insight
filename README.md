# TFB Insight

This application read and analyse [TechEmpower Benchmark project](https://github.com/TechEmpower/FrameworkBenchmarks/) data and provides visual insight into them 

## Prerequisite

### System command

Make sure you have the following programs installed and reachable from your `$PATH` environment variable: 

1. git - download from https://git-scm.com/downloads
2. loc - download from https://github.com/cgag/loc

### Packages

Make sure you have the following software packages installed

1. [MongoDB](https://docs.mongodb.com/manual/administration/install-community/) 
2. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
3. [Maven](http://maven.apache.org/download.cgi)

## Start the application

Start the application in dev mode

```
mvn clean compile exec:exec
```

Start the application in prod mode

```
mvn clean package
cd target/dist
unzip *
./start
```

## Prepare workspace for analysis

Enter the CLI mode:

```bash
nc localhost 5461
``` 

Type `workspace.load`

This above command will fetch the TFB repository from github for the first time

## Do analysis

Enter the CLI mode and type `analyse`

The above command will run analysis on all TFB projects 

## View report

Open browser and navigate to `http://localhost:5460`