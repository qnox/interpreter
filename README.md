# Simple language interpreter based on Antlr4

This interpreter supports a grammar represented as pseudo BNF:
```
expr ::= expr op expr | (expr) | identifier | { expr, expr } | number | map(expr, identifier -> expr) | reduce(expr, expr, identifier identifier -> expr)
op ::= + | - | * | / | ^
stmt ::= var identifier = expr | out expr | print "string"
program ::= stmt | program stmt
```

## How to build
```shell
mvn clean package
```

## How to run
```shell
cat src/test/resources/input.txt | java -jar target/interpreter-1.0-SNAPSHOT.jar
```

```shell
echo out 1 + 1 | java -jar target/interpreter-1.0-SNAPSHOT.jar
```