package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Statement extends CParseRule{
    private CParseRule rule;
    public Statement(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (StatementAssign.isFirst(tk)) {
            rule = new StatementAssign(pcx);
            rule.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (rule != null) {
            rule.semanticCheck(pcx);
            setCType(rule.getCType());
            setConstant(rule.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Statement starts");
        if (rule != null) { rule.codeGen(pcx);}
        o.println(";;; Statement completes");
    }
}
