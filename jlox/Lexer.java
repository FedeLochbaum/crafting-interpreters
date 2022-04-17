package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jlox.TokenType.*;

public class Lexer {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  private static final Map<String, TokenType> KEYWORDS;

  static {
    KEYWORDS = new HashMap<>();
    KEYWORDS.put("if", IF);
    KEYWORDS.put("else", ELSE);
    KEYWORDS.put("for", FOR);
    KEYWORDS.put("while", WHILE);

    KEYWORDS.put("var", VAR);
    KEYWORDS.put("false", FALSE);
    KEYWORDS.put("true", TRUE);
    KEYWORDS.put("nil", NIL);
    
    KEYWORDS.put("and", AND);
    KEYWORDS.put("or", OR);
    
    KEYWORDS.put("fun", FUN);
    KEYWORDS.put("print", PRINT);
    KEYWORDS.put("return", RETURN);

    KEYWORDS.put("this", THIS);
    KEYWORDS.put("super", SUPER);
    KEYWORDS.put("class", CLASS);
  }

  String EOF_LEXEME = "";

  char JUMP = '\n';

  int start = 0;
  int current = 0;
  int line = 1;

  Lexer(String source) {
    this.source = source;
  }

  private Boolean isAtEnd() {
    return current >= source.length();
  }

  private void addToken(TokenType type, Object literalValue) {
    String lexeme = source.substring(start, current);
    tokens.add(new Token(type, lexeme, literalValue, line));
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private char advance() {
    return source.charAt(current++);
  }

  private boolean matchNext(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) return JUMP;
    return source.charAt(current);
  }

  private void consumeSlash() {
    if (matchNext('/')) {
      while (peek() != JUMP && !isAtEnd()) advance();
    } else { addToken(SLASH); };
  }

  private void consumeString() {
    while (peek() != '"' && isAtEnd()) {
      if (peek() == '\n') line++; advance();
    }

    if (isAtEnd()) {
      LoxLang.error(line, "Unterminated string.");
      return;
    }

    // case where the peek is end delimiter "
    advance();

    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean isDigit(char current) {
    return current >= '0' && current <= '9';
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private void consumeNumber() {
    while (isDigit(peek())) advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // consume the '.'
      advance();
      while (isDigit(peek())) advance();
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private boolean isAlpha(char current) {
    return 
      (current >= 'a' && current <= 'z') ||
      (current >= 'A' && current <= 'Z') ||
      current == '_';
  }

  private boolean isAlphaNumeric(char current) {
    return isAlpha(current) || isDigit(current);
  }

  private void consumeIdentifier() {
    while (isAlphaNumeric(peek())) advance();

    String value = source.substring(start, current);
    TokenType type = KEYWORDS.get(value);

    if (type != null) type = IDENTIFIER;

    addToken(type);
  }

  private void consumeToken() {
    char current = advance();

    switch (current) {

      // cases with a character
      case '(': addToken(LEFT_PARENT); break;
      case ')': addToken(RIGHT_PARENT); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;

      // cases with two characters
      case '!': addToken(matchNext('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(matchNext('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': addToken(matchNext('=') ? LESS_OR_EQUAL : LESS); break;
      case '>': addToken(matchNext('=') ? GREATER_OR_EQUAL : GREATER); break;
      case '/': consumeSlash(); break;

      // logic operators
      case 'o': if (matchNext('r')) addToken(OR); break;

      // spaces
      case ' ':
      case '\r':
      case '\t': break;
      case '\n': line++; break;

      // literals
      case '"': consumeString(); break;

      default:
        if (isDigit(current)) consumeNumber();
        else if (isAlpha(current)) consumeIdentifier();
        else {
          // unknown character
          LoxLang.error(line, "Unexpected character " + current);
        }
        break;
    }
  }

  List<Token> consumeTokens() {
    while (!isAtEnd()) {
      start = current;
      consumeToken();
    }
    
    tokens.add(new Token(EOF, EOF_LEXEME, null, line));
    return tokens;
  }

}
