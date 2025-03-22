![Test Status](https://github.com/Plixo2/KarinaC/actions/workflows/gradle.yml/badge.svg)

Language Features Documentation: [karina-lang.org](https://karina-lang.org/Intro.html)

# Karina Compiler 
 

- [Language Server](src/main/java/org/karina/lang/lsp)
- [Standard Library](src/main/java/karina/)
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