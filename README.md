# Compiler - Part 2: Project on Language Theory and Compiling (INFO-F403)

This project aims to build a compiler for PASCALMAISPRESQUE, a simple imperative language.
In this second part, we implemented the parser using JFlex.


# Compilation
To compile the project into a .jar file, you can use the following command in your terminal:

```bash
make jar
```

# Usage
You can run the project in two different ways:

## Using Project Files:
You can execute the Main class by running the following commands:

```bash
make parse_only
```

or 

```bash
make parse_and_build_tree
```

These methods allow you to directly run the project using its source files.

## Using Compiled JAR:

If you prefer, you can use the precompiled JAR file by executing:

```bash
make launch_parse_only
```

or

```bash
make launch_parse_and_build_tree
```

These commands use the part2.jar file generated during compilation. If you want to process a different input file
or output the parsing tree to a different file, you can make the necessary changes in the Makefile
(INPUT_FILE and OUTPUT_FILE).