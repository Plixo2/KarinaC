![Test Status](https://github.com/Plixo2/KarinaC/actions/workflows/gradle.yml/badge.svg)

Language Features Documentation: [karina-lang.org](https://karina-lang.org/Intro.html)

# Karina Compiler 

Use at least Java 23 with `--enable-preview` flag.



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



- [Language Server](src/main/java/org/karina/lang/lsp)
- [Standard Library](src/main/java/karina/lang/)
- [Compiler](src/main/java/org/karina/lang/compiler)


## Compiler architecture (roughly)

- Start with reading the source code into a file tree structure.
- Check cached libraries, and rebuild them if needed (with asm)
- Convert the standard java library to a custom Class, Field, Method and type abstractions 
- Convert the rest of .jar libraries 
- Flat the file tree into a list of files
- Parse the loaded files into tokens, then into an AST via Antlr
- Convert and Link the Antlr AST into a ClassModel and custom Expressions
- Merge all loaded classes in a common data structure
- Classes loaded from source code do now traverse the the following steps
  - Resolve the types in the Import stage using imports
  - Validation and Infer Types in the Attribution stage
  - Construct new classes, bridge methods / rewrite loops, etc in the Lower stage
  - Generate the bytecode in the Emit stage
- And then finally write the bytecode to disk