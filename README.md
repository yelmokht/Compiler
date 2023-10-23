# Compiler - Part 1: Project on Language Theory and Compiling (INFO-F403)

This goal of this project is to build a compiler designed for PASCALMAISPRESQUE, a simple imperative language.
In this first part, we implemented the lexical analyzer for the compiler using JFlex.

# Compilation
To compile the project into a .jar file, you can use the following command in your terminal:

```bash
make jar
```

# Usage
You can run the project in two different ways:

## Using Project Files:
You can execute the Main class by running the following command:

```bash
make run
```
This method allows you to directly run the project using its source files.

## Using Compiled JAR:

If you prefer, you can use the precompiled JAR file by executing:

```bash
make launch
```

This command utilizes the part1.jar file generated during the compilation process. If you want to read a different file,
you have to modify the filename in the Makefile (change "FILE_NAME = euclid.pmp").