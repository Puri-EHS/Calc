class Lexer {
    private String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public Token<?> getToken() {
        char c = peekChar();
        while (" \t\n".indexOf(c) != -1) {
            getChar();
            c = peekChar();
        }
        if (c == '\0') {
            return new EndToken();
        }
        else if (Character.isDigit(c)) {
            return new NumberToken(readNumber());
        }
        else if (Character.isLetter(c)) {
            return new IdToken(readId());
        }
        else if ("+-*/^()".indexOf(c) != -1) {
            return new OpToken(getChar());
        }
        else return new ErrorToken();
    }

    public int checkpoint() {
        return pos;
    }

    public void restore(int checkpoint) {
        pos = checkpoint;
    }

    private Number readNumber() {
        StringBuilder chars = new StringBuilder();
        boolean sawPeriod = false;
        chars.append(getChar());
        char c = peekChar();
        while (Character.isDigit(c) || (c == '.' && !sawPeriod)) {
            chars.append(getChar());
            sawPeriod |= (c == '.');
            c = peekChar();
        }
        return Double.valueOf(chars.toString());
    }

    private String readId() {
        StringBuilder chars = new StringBuilder();
        chars.append(getChar());
        char c = peekChar();
        while (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
            chars.append(getChar());
            c = peekChar();
        }
        return chars.toString();
    }

    private char peekChar() {
        if (pos >= input.length()) {
            return '\0';
        }
        return input.charAt(pos);
    }

    private char getChar() {
        char result = peekChar();
        if (result != '\0') {
            pos++;
        }
        return result;
    }
}

abstract class Token<T> {
    public T value;
    public Number getNumber() { return null; }
    public String getId() { return null; }
    public Character getOp() { return null; }

    public boolean isOp(String ops) {
        return getOp() != null && ops.indexOf(getOp()) != -1;
    }
}

class NumberToken extends Token<Number> {
    NumberToken(Number value) {
        this.value = value;
    }
    public Number getNumber() { return value; }
}

class IdToken extends Token<String> {
    IdToken(String value) {
        this.value = value;
    }
    public String getId() { return value; }
}

class OpToken extends Token<Character> {
    OpToken(char value) {
        this.value = value;
    }
    public Character getOp() { return value; }
}

class EndToken extends Token<Void> {}
class ErrorToken extends Token<Void> {}