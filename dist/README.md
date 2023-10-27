This folder contains an executable JAR called part2.jar.
The commands for running the executable are the following:

```bash
java -jar part2.jar sourceFile.pmp
```

```bash
java -jar part2.jar -wt sourceFile.tex sourceFile.pmp
```

Alternatively, you can use these commands provided in the Makefile:

```bash
make launch_parse_only
```

```bash
make launch_parse_and_build_tree
```