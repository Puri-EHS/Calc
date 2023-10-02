// Language definition:
//
// Top          := Assignment
//              |  Expression
// Assignment   := <id> = Expression
// Expression   := Expression <addop> MulExpr
//              |  MulExpr
// MulExpr      := MulExpr <mulop> PowExpr
//              |  PowExpr
// PowExpr      := PowExpr <powop> RawExpr
//              |  RawExpr
// RawExpr      := <id>
//              |  <number>
//              |  ( Expression )
//              |  <string>
//              |  <boolean>

class Parser {
    private Lexer lexer;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public Ast parse() {
        Ast result = null;
        if (result == null) result = parseAssignment();
        if (result == null) result = parseExpression(levels.length);
        return result;
    }

    private Assignment parseAssignment() {
        int checkpoint = lexer.checkpoint();
        String id = lexer.getToken().getId();
        if (id != null && lexer.getToken().isOp("=")) {
            Expression expr = parseExpression(levels.length);
            if (expr != null) {
                return new Assignment(id, expr);
            }
        }
        lexer.restore(checkpoint);
        return null;
    }

    private final String[] levels = { "^", "*/", "+-" };

    private Expression parseExpression(int level) {
        if (level == 0) {
            return parseRawExpression();
        }

        int checkpoint = lexer.checkpoint();
        Expression expr = parseExpression(level - 1);
        if (expr != null) {
            Expression newExpr = parseNextBinop(expr, level);
            while (newExpr != null) {
                expr = newExpr;
                newExpr = parseNextBinop(expr, level);
            }
            return expr;
        }
        lexer.restore(checkpoint);
        return null;
    }

    private Expression parseNextBinop(Expression expr, int level) {
        int checkpoint = lexer.checkpoint();
        Token<?> token = lexer.getToken();
        if (token.isOp(levels[level - 1])) {
            Expression next = parseExpression(level - 1);
            if (next != null) {
                return new BinopExpression(token.getOp(), expr, next);
            }
        }
        lexer.restore(checkpoint);
        return null;
    }

    private Expression parseRawExpression() {
        int checkpoint = lexer.checkpoint();
        String id = lexer.getToken().getId();
        if (id != null) {
            return new IdExpression(id);
        }
        lexer.restore(checkpoint);

        Number number = lexer.getToken().getNumber();
        if (number != null) {
            return new NumberExpression(number);
        }
        lexer.restore(checkpoint);

        if (lexer.getToken().getOp() == '(') {
            Expression expr = parseExpression(levels.length);
            if (expr != null && lexer.getToken().getOp() == ')') {
                return expr;
            }
        }
        lexer.restore(checkpoint);
        return null;
    }
}

abstract class Ast {}
abstract class Expression extends Ast {}

class Assignment extends Ast {
    public String id;
    public Expression expr;

    Assignment(String id, Expression expr) {
        this.id = id;
        this.expr = expr;
    }

    public String toString() {
        return id + " = " + expr.toString();
    }
}

class BinopExpression extends Expression {
    public char op;
    public Expression expr1, expr2;

    BinopExpression(char op, Expression expr1, Expression expr2) {
        this.op = op;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public String toString() {
        return "(" + expr1.toString() + " " + op + " " + expr2.toString() + ")";
    }
}

class IdExpression extends Expression {
    public String id;

    IdExpression(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }
}

class NumberExpression extends Expression {
    public Number number;

    NumberExpression(Number number) {
        this.number = number;
    }

    public String toString() {
        return number.toString();
    }
}