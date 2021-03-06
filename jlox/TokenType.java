package jlox;

enum TokenType {
  
  // punctuations
  LEFT_PARENT, RIGHT_PARENT, LEFT_BRACE, RIGHT_BRACE,
  COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, BANG, BANG_EQUAL,
  
  // operators
  EQUAL, EQUAL_EQUAL, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL,

  // literals
  IDENTIFIER, STRING, NUMBER,

  // keywords
  AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL,
  OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

  // eof
  EOF
}
