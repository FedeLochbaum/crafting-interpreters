package jlox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LoxLang {
  static boolean hadError = false;
  public static void main(String[] args) throws Exception {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws Exception {
    if (hadError) System.exit(65);

    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, StandardCharsets.UTF_8));
  }

  private static void runPrompt() throws Exception {
    hadError = false;

    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    while (true) {
      System.out.print("> ");
      run(reader.readLine());
      System.out.println();
    }
  }

  private static void run(String source) {
    Lexer lexer = new Lexer(source);
    List<Token> tokens = lexer.consumeTokens();

    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  private static void report(int line, String where, String message) {
    System.err.println("Error in [line " + line + " ] " + where + ": " + message);
    hadError = true;
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

}