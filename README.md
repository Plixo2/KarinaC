

<div align="center">

<h1 align="center">Karina Compiler</h1>
<a href="https://karina-lang.org/">
  Karina-lang.org
</a>

</div>

<br>

![Test Status](https://github.com/Plixo2/KarinaC/actions/workflows/gradle.yml/badge.svg)


## Getting Started

You need Java 23 or higher.
You can use [SDKMAN!](https://sdkman.io/) to manage your Java versions.



```shell
 git clone https://github.com/Plixo2/KarinaC.git
 cd KarinaC
```

<details> <summary>IDE setup</summary>

The Compiler is a standard Gradle project, so you can use it with any IDE that supports Gradle.

You can run the compiler via the Gradle task `run` or run the [Main Class](src/main/java/org/karina/lang/compiler/Main.java) directly.

The `run` script in [`resources/out/`](resources/out/) can be used to run the generated jar file.

</details>

<details>

<summary>Manual setup</summary>

Make sure your `JAVA_HOME` is set to the correct version.

### Windows
```shell
 gradlew.bat run # run the compiler via gradle
 resources\out\run.bat # run the generated file
```

### Linux
```shell
 git clone https://github.com/Plixo2/KarinaC.git
 cd KarinaC
 chmod +x ./gradlew
 ./gradlew run # run the compiler via gradle
 chmod +x ./resources/out/run
 ./resources/out/run # run the generated file
 ```


</details>


The project is configured to build the demo project in [`resources/src/`](resources/src/).

The `run` script in [`resources/out/`](resources/out/) will run the generated jar file.


## Development


You can use the following System Properties for local development:

<details> <summary>karina.source</summary>

> `karina.source="<src folder>"` 

Points to your local development folder. Defaults to `resources/src/`

</details>

<details> <summary>karina.out</summary>

> `karina.out="<build file>"` 

specifies the output jar file. Defaults to `resources/out/build.jar`

</details>


<details> <summary>karina.flight</summary>

> `karina.flight="<debug file>"` 

Specifies the debug flight recorder file path. Defaults to `resources/flight.txt`

</details>

<details> <summary>karina.console</summary>

> `karina.console="<true/false>"`  

Enables/Disables the flight recorder output to the console. Defaults to `resources/flight.txt`

</details>

<details> <summary>karina.logging</summary>

> `karina.logging="<none/verbose/verbose_jvm>"`

Enables/Disables the flight recorder output to the console. Defaults to `none`.
Useful for debugging the compiler.


</details>

You can set system environment flags via [build.gradle](build.gradle) or the vm arguments in your IDE.

```groovy
application {
    mainClass.set('org.karina.lang.compiler.Main')
    applicationDefaultJvmArgs = ['-Dkarina.source="resources/local/"'] // set the source folder to your local dev folder
}
```

You also can set custom Log types in
[`Log.java`](src/main/java/org/karina/lang/compiler/logging/Log.java#L45).
This will enable logging for specific parts of the compiler.

> [!IMPORTANT]
> Please set this flag to `verbose` and upload the flight.txt file along with the source files, when you encounter a bug.



## Rebuild the standard library

You can rebuild the [standard library](src/main/java/karina/lang/) with the
Gradle task `KARINA-BASE`. 

This will create a new  `karina_base.jar` file, located in [`src/main/resources`](src/main/resources)

# Compiler architecture

<details>

<summary>Internals</summary>



- Read the source code into memory
- Load the precompiled jar files (java.core and the karina.base) into a ClassModel
- [Parser Stage](src/main/java/org/karina/lang/compiler/stages/parser/ParseProcessor.java)
  - Parse the loaded files into tokens, then into an AST via Antlr
  - Convert the Antlr AST into a ClassModel and IR
- [Import Stage](src/main/java/org/karina/lang/compiler/stages/imports/ImportProcessor.java)
  - Resolve all types
  - Validate implememented Methods and Inheritance/Composition
- [Attribution Stage](src/main/java/org/karina/lang/compiler/stages/attrib/AttributionProcessor.java)
  - Validation and type inference
- [Lower Stage](src/main/java/org/karina/lang/compiler/stages/lower/LoweringProcessor.java)
  - Construct new classes, bridge methods, rewrite loops, etc
- [Generate Stage](src/main/java/org/karina/lang/compiler/stages/generate/GenerationProcessor.java)
  - Generate the bytecode
- And then finally write the bytecode to disk

Other important classes/packages:
- [Main Class](src/main/java/org/karina/lang/compiler/Main.java)
- [Compiler Class](src/main/java/org/karina/lang/compiler/KarinaCompiler.java)
- [KExpr Class](src/main/java/org/karina/lang/compiler/utils/KExpr.java)
- [KType Class](src/main/java/org/karina/lang/compiler/utils/KType.java)
- [jvm](src/main/java/org/karina/lang/compiler/jvm_loading)
  - Responsible for loading of precompiled classes
- [model_api](src/main/java/org/karina/lang/compiler/model_api)
  - The API for the ClassModel. Represents all loaded classes and their fields, methods, etc

</details>





