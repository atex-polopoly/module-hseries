module-hseries
==============

modules to integrate Hermes via content api

This is an example demonstrating how to connect Hermes11 and Polopoly via content API. 
This project is needed to connect both ACT and dm.desk to Hermes11 sharing contents between systems.


Use the RELEASE branch corresponding to your combination of Polopoly version and dm.desk version.

**Please note that this example can only be used in combination with the GONG, and has only been verified to work with the corresponding release versions (of both GONG and Polopoly).**

## Installation

### 1. Clone the repositories

Clone the repository into your project (say your project is named gong):

```
~/gong $ git clone git@github.com:atex-polopoly/module-hseries.git

```

### 2. Check out the correct branch

Check out a release branch matching your setup:
```
~/gong $ git checkout RELEASE-POLOPOLY-10.11.0-SNAPSHOT
```

**Currently you should checkout the master branch in order to work with dm.desk and ACT**
**If no branch matches your combination of Polopoly version and dm.desk version, then that combination is not officially supported.**

### 3. Modify the project top pom

Add the newly cloned repository as a module in the top pom.xml file of your project:

```xml
...
<modules>
  ...
    <module>module-hseries</module>
  ...
</modules>
...
```

### 4. Change version in module-hseries

Modify the parent version in the *module-hseries/pom.xml* file in order to match the version of the Greenfield Online project being used:

```xml
...
<parent>
  <groupId>com.atex.gong</groupId>
  <artifactId>top</artifactId>
  <version>1.0-SNAPSHOT</version>
  <relativePath>../pom.xml</relativePath>
</parent>
...
```

### 5. Add artifact to the server-onecms

Add a dependency to the example artifact in the pom.xml file of the Data API Server (server-data-api/pom.xml):

```xml
...
<dependencies>
  ...
    <dependency>
      <artifactId>module-hseries</artifactId>
      <groupId>com.atex</groupId>
      <version>${project.version}</version>
    </dependency>
  ...
</dependencies>
...
```

### 8. (Re) start GONG

When everything above is completed, start your installation using **mvn p:run**.


## Code Status
The code in this repository is provided with the following status: **EXAMPLE**

Under the open source initiative, Atex provides source code for plugins with different levels of support. There are three different levels of support used. These are:

- EXAMPLE
The code is provided as an illustration of a pattern or blueprint for how to use a specific feature. Code provided as is.

- PROJECT
The code has been identified in an implementation project to be generic enough to be useful also in other projects. This means that it has actually been used in production somewhere, but it comes "as is", with no support attached. The idea is to promote code reuse and to provide a convenient starting point for customization if needed.

- PRODUCT
The code is provided with full product support, just as the core Polopoly product itself.
If you modify the code (outside of configuraton files), the support is voided.

## License
Atex Polopoly Source Code License
Version 1.0 February 2012

See file **LICENSE** for details
