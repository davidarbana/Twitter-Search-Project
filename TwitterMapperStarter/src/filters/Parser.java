package filters;

/**
 * Parse a string in the filter language and return the filter.
 * Throws a SyntaxError exception on failure.
 *
 * This is a top-down recursive descent parser (a.k.a., LL(1))
 *
 * The really short explanation is that an LL(1) grammar can be parsed by a collection
 * of mutually recursive methods, each of which is able to recognize a single grammar symbol.
 *
 * The grammar (EBNF) for our filter language is:
 *
 * goal    ::= expr
 * expr    ::= orExpr
 * orExpr  ::= andExpr ( "or" andExpr )*
 * andExpr ::= notExpr ( "and" notExpr )*
 * notExpr ::= prim | "not" notExpr
 * prim    ::= word | "(" expr ")"
 *
 * The reason for writing it this way is that it respects the "natural" precedence of boolean
 * expressions, where the precedence order (decreasing) is:
 *      parens
 *      not
 *      and
 *      or
 * This allows an expression like:
 *      blue or green and not red or yellow and purple
 * To be parsed like:
 *      blue or (green and (not red)) or (yellow and purple)
 */
public class Parser {
    private final Scanner scanner;
    private static final String LPAREN = "(";
    private static final String RPAREN = ")";
    private static final String OR = "or";
    private static final String AND = "and";
    private static final String NOT = "not";

    public Parser(String input) {
        scanner = new Scanner(input);
    }

    public Filter parse() throws SyntaxError {
        Filter answer = expr();
        if (scanner.peek() != null) {
            throw new SyntaxError("Extra stuff at end of input");
        }
        return answer;
    }

    private Filter expr() throws SyntaxError {
        return orExpr();
    }

    private Filter orExpr() throws SyntaxError {
        Filter sub = andExpr();
        String token = scanner.peek();
        while (token != null && token.equals(OR)) {
            scanner.advance();
            Filter right = andExpr();
            // At this point we have two subexpressions ("sub" on the left and "right" on the right)
            // that are to be connected by "or"
            // TODO: Construct the appropriate new Filter object
            // The new filter object should be assigned to the variable "sub"
            sub = new OrFilter(sub, right);
            token = scanner.peek();
        }
        return sub;
    }

    private Filter andExpr() throws SyntaxError {
        Filter sub = notExpr();
        String token = scanner.peek();
        while (token != null && token.equals(AND)) {
            scanner.advance();
            Filter right = notExpr();
            // At this point we have two subexpressions ("sub" on the left and "right" on the right)
            // that are to be connected by "and"
            // TODO: Construct the appropriate new Filter object
            // The new filter object should be assigned to the variable "sub"
            sub = new AndFilter(sub, right);
            token = scanner.peek();
        }
        return sub;
    }

    private Filter notExpr() throws SyntaxError {
        String token = scanner.peek();
        if (token.equals(NOT)) {
            scanner.advance();
            Filter sub = notExpr();
            return new NotFilter(sub);
        } else {
            Filter sub = prim();
            return sub;
        }
    }

    private Filter prim() throws SyntaxError {
        String token = scanner.peek();
        if (token.equals(LPAREN)) {
            scanner.advance();
            Filter sub = expr();
            if (!scanner.peek().equals(RPAREN)) {
                throw new SyntaxError("Expected ')'");
            }
            scanner.advance();
            return sub;
        } else {
            Filter sub = new BasicFilter(token);
            scanner.advance();
            return sub;
        }
    }
}
