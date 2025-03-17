package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>(); 
    // points to first character in the lexeme
    private int start = 0;
    // points at character currently being considered
    private int current = 0;
    // tracks what source line current is on
    private int line = 1;

    // Scanner constructor for source code
    Scanner(String source) {
        this.source = source;
    }

    // Calls scan token to scan each lexemme and add it as a token in the list 
    // Adds EOF token at the end to represent the end of the file
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line))
        return tokens;
    }
    
    // Designates each lexemme into a token type
    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': 
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
            if (match('/')) {
              while (peek() != '\n' && !isAtEnd()) advance();
            } else {
              addToken(SLASH);
            }
            break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
            if (isDigit(c)) {
                number();
            } else{
                Lox.error(line, "Unexpecter character.");
            }
            break;
        }
        
    }
    
    // function to add string tokens 
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    // checks next character to see if it matches current character
    // used for two character lexemmes
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current ++;
        return true; 
    }

    // checks if it is a double-slash (comment) 
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // checks to ensure that value after the decimal point is still a number
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    // checks if character is a digit
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // consume the rest of the literal for the number
    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    // checks if the source code is over
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // advances to the next character in the code
    private char advance() {
        return source.charAt(current++);
    }

    // add token function definition for empty types
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // add token function definition for non-empty types
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}