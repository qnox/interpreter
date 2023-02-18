grammar Lang;

program
    : stmt
    | program stmt
    ;

stmt
    : VAR variable=IDENTIFIER EQ value=expr #Assignment
    | OUT value=expr #Out
    | PRINT quotedString=STRING #Print
    ;

expr
    : number=NUMBER #NumberExpr
    | identifier=IDENTIFIER #IdentifierExpr
    | <assoc=right> v1=expr op=POW v2=expr #PowExpr
    | v1=expr op=(TIMES | DIV) v2=expr #MulExpr
    | v1=expr op=(PLUS | MINUS) v2=expr #AddExpr
    | op=(PLUS | MINUS) v=expr #UnaryExpr
    | LPAREN expression=expr RPAREN #ParenthisedExpr
    | LCURLY from=expr COMMA to=expr RCURLY #RangeExpr
    | MAP LPAREN input=expr COMMA v=IDENTIFIER ARROW op=expr RPAREN #MapExpr
    | REDUCE LPAREN input=expr COMMA init=expr COMMA acc=IDENTIFIER v=IDENTIFIER ARROW op=expr RPAREN #ReduceExpr
    ;

VAR
   : 'var'
   ;

MAP
   : 'map'
   ;

REDUCE
   : 'reduce'
   ;

OUT
   : 'out'
   ;

PRINT
   : 'print'
   ;

ARROW
   : '->'
   ;

PLUS
   : '+'
   ;

MINUS
   : '-'
   ;

TIMES
   : '*'
   ;

DIV
   : '/'
   ;

POW
   : '^'
   ;

EQ
   : '='
   ;

COMMA
   : ','
   ;

LCURLY
   : '{'
   ;

RCURLY
   : '}'
   ;

LPAREN
   : '('
   ;

RPAREN
   : ')'
   ;

NUMBER
   : ('0' .. '9') + ('.' ('0' .. '9') +)?
   ;

STRING
   : '"' ~["]* '"'
   ;

IDENTIFIER
   : VALID_IDENTIFIER_START VALID_IDENTIFIER_CHAR*
   ;


fragment VALID_IDENTIFIER_START
   : ('a' .. 'z') | ('A' .. 'Z') | '_'
   ;


fragment VALID_IDENTIFIER_CHAR
   : VALID_IDENTIFIER_START | ('0' .. '9')
   ;

WS
   : [ \r\n\t] + -> skip
   ;
