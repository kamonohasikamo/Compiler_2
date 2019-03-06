package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementCond extends CParseRule {
    private CParseRule condition;
    public StatementCond(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_LPAR;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);	//TK_LPAR
        if (Condition.isFirst(tk)) { // Condition.isFirstを満たす -> 条件式が「ある」ということ
            condition = new Condition(pcx);
            condition.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "条件式がありません");
        }
        tk = ct.getCurrentToken(pcx);
        if (tk.getType() == CToken.TK_RPAR) { // (から読み始め、)が来るまで読み込む
            ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + ")がありません");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condition != null) {
            condition.semanticCheck(pcx);
            setCType(condition.getCType());
            setConstant(condition.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        if (condition != null) {
            condition.codeGen(pcx);
        }
    }
}
