# TFB Insight

TFB Insight 是一个 [TechEmpower Benchmark project](https://github.com/TechEmpower/FrameworkBenchmarks/) 性能测试数据展示平台.

## 应用官网地址:

[tfb-insight.thinking.studio](http://tfb-insight.thinking.studio)

## 系统要求

### 系统命令

需要在您系统的 `$PATH` 环境变量中能够访问到一下两条系统命令:

1. git - 下载地址: https://git-scm.com/downloads
2. loc - 下载地址: https://github.com/cgag/loc

### 其他依赖软件

您必须确保系统上已经安装运行了一下软件

1. [MongoDB](https://docs.mongodb.com/manual/administration/install-community/) 
2. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
3. [Maven](http://maven.apache.org/download.cgi)

## 启动应用

开发模式启动应用

```
mvn clean compile exec:exec
```

产品模式启动应用

```
mvn clean package
cd target/dist
unzip *
./start
```

## 准备数据分析工作区

进入 ActFramework 的 CLI 模式:

```bash
nc localhost 5461 #nc 也可以替换为 telnet
``` 

键入 `workspace.load`

上面的命令会从 [TechEmpower Github 库](https://github.com/TechEmpower/FrameworkBenchmarks/) 中获取所有测试项目源代码

## 进行数据分析

还是继续保持在 CLI 模式中,然后键入 `analyse`

这条命令会从 [TechEmpower Benchmark 网站](https://www.techempower.com/benchmarks/previews/round15/) 获取测试性能数据并进行分析处理

## 查看数据

打开浏览器并访问 `http://localhost:5460`