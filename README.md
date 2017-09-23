# TEB project analyser

This application analyser [TEB projects](https://github.com/TechEmpower/FrameworkBenchmarks/) and generate detail comparison between frameworks

## Prerequisite

Make sure you have the following system command ready for use:

1. git - download from https://git-scm.com/downloads
2. loc - download from https://github.com/cgag/loc

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

This above command will fetch the TEB repository from github for the first time

## Do analysis

Enter the CLI mode and type `analyse`

The above command will run analysis on all TEB projects 

## View report

TBD