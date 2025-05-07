![Test Status](https://github.com/Plixo2/KarinaC/actions/workflows/gradle.yml/badge.svg)

Language Features/Documentation: [karina-lang.org](https://karina-lang.org/Intro.html)

# Karina Compiler 

Use at least Java 23 with `--enable-preview` flag.



- [Standard Library](src/main/java/karina/lang/)
- [Compiler](src/main/java/org/karina/lang/compiler)
- [Language Server](src/main/java/org/karina/lang/lsp)

## Compiler architecture

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
- [Main Class](src/main/java/org/karina/lang/compiler/boot/Main.java)
- [Compiler Class](src/main/java/org/karina/lang/compiler/api/KarinaDefaultCompiler.java)
- [KExpr Class](src/main/java/org/karina/lang/compiler/objects/KExpr.java)
- [KType Class](src/main/java/org/karina/lang/compiler/objects/KType.java)
- [jvm](src/main/java/org/karina/lang/compiler/jvm_loading)
  - Responsible for loading of precompiled classes
- [model_api](src/main/java/org/karina/lang/compiler/model_api)
  - The API for the ClassModel. Represents all loaded classes and their fields, methods, etc

I would recommend to use the following java flags for development:

```shell
-ea 
-Dkarina.out="resources/out/build.jar"  
-Dkarina.source="resources/src/" 
-Dkarina.console=true 
-Dkarina.logging=none
```

> `karina.source` can point to your local development folder

> `karina.out` points to the output jar file

> `karina.console` will print the flight recorder output to the console.

> `karina.logging` will set the logging level (`none`, `verbose` or `verbose_jvm`)

> [!IMPORTANT]
> Please set the `karina.logging` flag to `verbose` and upload the flight.txt file when you encounter a bug along with the source files


