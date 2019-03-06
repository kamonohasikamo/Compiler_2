package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementElse extends CParseRule {
    private CParseRule rule;
    public StatementElse(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_ELSE;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);	//TK_ELSE
        if (StatementIf.isFirst(tk)) {
            rule = new StatementIf(pcx);
            rule.parse(pcx);
        } else if (StatementBranch.isFirst(tk)) {
            rule = new StatementBranch(pcx);
            rule.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "条件文内部に文がありません");
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
        if (rule != null) {
            rule.codeGen(pcx);
        }
    }
}