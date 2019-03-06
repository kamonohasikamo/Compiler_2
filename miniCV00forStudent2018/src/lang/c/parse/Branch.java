package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Branch extends CParseRule {
    private CParseRule branch;

    public Branch(CParseContext pcx) {

    }
    public static boolean isFirst(CToken tk) {  // 構文定義の右辺がここに来る
        return StatementIf.isFirst(tk) || StatementWhile.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (StatementIf.isFirst(tk)) {
            branch = new StatementIf(pcx);
            branch.parse(pcx);
        } else if(StatementWhile.isFirst(tk)) {
            branch = new StatementWhile(pcx);
            branch.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (branch != null) {
            branch.semanticCheck(pcx);
            setCType(branch.getCType());
            setConstant(branch.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        if (branch != null) {
            branch.codeGen(pcx);
        }
    }
}
