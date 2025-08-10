

<div align="center">

<h1 align="center">Karina Compiler</h1>
<a href="https://karina-lang.org/">
  karina-lang.org
</a>

</div>

<br>
<br>
<br>

![Test Status](https://github.com/Plixo2/KarinaC/actions/workflows/gradle.yml/badge.svg)
![Java Version](https://img.shields.io/badge/Java-23+-orange)
![Karina Version](https://img.shields.io/badge/Karina-v0.6-8A2BE2)
![Visitors](https://visitor-badge.laobi.icu/badge?page_id=plixo.karinac)
[![License: MIT/Apache-2.0](https://img.shields.io/badge/License-Apache--2.0%20%7C%20MIT-blue)](https://opensource.org/licenses/MIT)
![Windows](https://img.shields.io/badge/Windows-0078D6?style=flat)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=flat&logo=linux&logoColor=black)
![macOS](https://img.shields.io/badge/macOS-000000?style=flat&logo=apple&logoColor=white)
<br>

**Karina is a statically typed, general-purpose, high-level programming language that emphasizes simplicity, 
interoperability, and concise notation. Karina is fully compatible with Java, allowing you to use existing libraries and 
frameworks seamlessly while enjoying a modern programming experience.**

<br>

> 📢 **Heads up!** If you run into any issues, bugs, unexpected behavior, or have any questions while using this project, you’re encouraged to [open an issue](https://github.com/Plixo2/KarinaC/issues/new).
>
> Your feedback and inquiries are greatly appreciated!

<br>

- [**Documentation and Features**](#Documentation)
- [**Installer and CLI**](#Installer-and-CLI)
- [**Local development**](#Local-development)
- [**Compiler architecture**](#Compiler-architecture)
- [**Licenses**](#Licenses)


## Documentation

The official documentation is available at
[karina-lang.org](https://karina-lang.org/guide/hello.html).


## Installer and CLI

You need Java 23 or higher.
You can use [SDKMAN!](https://sdkman.io/) to manage your Java versions.

To install the Karina compiler, download and run the installer.
After the installation, you can run the compiler from the command line:

```shell
karina -v
```
> Karina: v0.6 \
> Java: OpenJDK 64-Bit Server VM 23.0.2


#### Hello World

```shell
karina new hello-world
cd hello-world
karina run
```

> Hello, World!


<br>

## Local development

- [Demo Karina Project](resources/src/)
- [Compiler Code](compiler/)
- [Standard Library Code](stdlib/)
- [LSP Code](lsp/)

You need Java 23 or higher.
You can use [SDKMAN!](https://sdkman.io/) to manage your Java versions.


```shell
 git clone https://github.com/Plixo2/KarinaC.git
 cd KarinaC
```

The compiler is a standard Gradle project, so you can use it with any IDE that supports Gradle.

You can run the compiler via the Gradle task `compiler:run`:

```shell
 gradlew compiler:run
```

The project is configured to build the demo project in [`resources/src/`](resources/src) by default.


> [!NOTE]
> This project consists of multiple Gradle subprojects. Make sure to run the Gradle `jar` task's when updating the standard library or the compiler.


<details> <summary>Custom Environment</summary>


You can set System environment flags via [build.gradle](build.gradle) or the vm arguments in your IDE.

```groovy
application {
  // ...
  applicationDefaultJvmArgs = ['-Dkarina.source="resources/local/"'] // set the source folder to your local dev folder
}
```

### Flags:

#### karina.source
> `karina.source="<src folder>"`

Points to your local development folder. Defaults to `resources/src/`

#### karina.out
> `karina.out="<build file>"`

Specifies the output JAR file. Defaults to `resources/out/build.jar`

#### karina.classes
> `karina.classes="<true/false>"`

Enables/Disables the generation of .class files. Defaults to `true`

#### karina.flight
> `karina.flight="<debug file>"`

Specifies the debug flight recorder file path. Defaults to `resources/flight.txt`

#### karina.console
> `karina.console="<true/false>"`

Enables/Disables the flight recorder output to the console. Defaults to `true`

#### karina.binary
> `karina.binary="<true/false>"`

Enables/Disables the usage of a binary format for faster reading of precompiled classes.
Can improve startup performance by over 20 times, but untested and may cause issues.
Defaults to `false`



#### karina.logging

> `karina.logging="<none/basic/verbose/verbose_jvm>"`

Enables/Disables the flight recorder output to the console. Defaults to `none`.
Useful for debugging the compiler.


</details>

### Customize Logging
You can set custom log types in
[here](compiler/src/main/java/org/karina/lang/compiler/logging/Log.java#L53).

Adding log types will enable logging for specific parts of the compiler.
You can gain detailed insights into the inner workings of the compiler
by setting the correct logging types.


E.g. `LogTypes.CHECK_TYPE` will get you a **very** detailed view of the type checking process.

> [!NOTE]
> This is the primary way to debug the compiler.
> Be aware that this will generate a lot of output, when too many log types are enabled.

### javap

Another helpful tool is [`javap`](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javap.html). Use it to inspect the generated bytecode in detail.

```shell
javap -c -v -p main.class > main.txt
```
This will write the bytecode of the `main.class` file to `main.txt`, where it can be inspected.


### Rebuild the standard library

You can rebuild the [standard library](stdlib/src/main/java/karina/lang/) with the
Gradle task `stdlib:buildForCompiler`:

```shell
gradlew stdlib:buildForCompiler
```

This will create a new  `karina_base.jar` file, located in [`src/main/resources`](compiler/src/main/resources)

After that run the task `compiler:buildCache`:
```shell
gradlew compiler:buildCache
```

This will rebuild the cache for the compiler, so it can use the new standard library.

## Compiler architecture

<details open>

<summary>Internals</summary>


- Read the source code into memory
- Load the precompiled JAR files (java.core and the karina.base) into a ClassModel 
- [Parser Stage](compiler/src/main/java/org/karina/lang/compiler/stages/parser/ParseProcessor.java)
  - Parse the loaded files into tokens, then into an AST via Antlr
  - Convert the Antlr AST into a ClassModel and IR
- [Import Stage](compiler/src/main/java/org/karina/lang/compiler/stages/imports/ImportProcessor.java)
  - Resolve all types via imports
- [Attribution Stage](compiler/src/main/java/org/karina/lang/compiler/stages/attrib/AttributionProcessor.java)
  - Expression validation and type inference
- [Lower Stage](compiler/src/main/java/org/karina/lang/compiler/stages/lower/LoweringProcessor.java)
  - Construct new classes, bridge methods, rewrite loops, etc
- [Generate Stage](compiler/src/main/java/org/karina/lang/compiler/stages/generate/GenerationProcessor.java)
  - Generate bytecode
- And then finally write the bytecode to disk

#### Important classes:
- [Main Class](compiler/src/main/java/org/karina/lang/compiler/Main.java)
- [Compiler Class](compiler/src/main/java/org/karina/lang/compiler/KarinaCompiler.java)
- [KExpr Class](compiler/src/main/java/org/karina/lang/compiler/utils/KExpr.java)
- [KType Class](compiler/src/main/java/org/karina/lang/compiler/utils/KType.java)

#### Important Packages
- [jvm_loading](compiler/src/main/java/org/karina/lang/compiler/jvm_loading)
  - Responsible for loading precompiled classes
- [model_api](compiler/src/main/java/org/karina/lang/compiler/model_api)
  - The API for the ClassModel. Represents all loaded classes and their fields, methods, etc
- [stages](compiler/src/main/java/org/karina/lang/compiler/stages)
  - All stages of the compiler.

</details>


## Licenses

Karina is published under two licenses:
- [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)
- [MIT License](https://opensource.org/license/mit/)

SPDX-License-Identifier: `Apache-2.0` or `MIT`

You may use this software under the terms of any license you choose.
