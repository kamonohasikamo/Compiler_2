package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

import java.util.ArrayList;
import java.util.List;

public class StatementBranch extends CParseRule {
    private List<CParseRule> statements;

    public StatementBranch(CParseContext pcx) {

    }

    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_LCUR;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CParseRule st = null;
        statements = new ArrayList< CParseRule >();
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        while(true) { // { を読み、Statement.isFirst()を満たす文字列を常に受け取る。
            if (Statement.isFirst(tk)) {
                st = new Statement(pcx);
                st.parse(pcx);
                statements.add(st);
                tk = ct.getCurrentToken(pcx);
            } else { // Statement.isFirst()を満たさなくなったとき、ループを抜ける
                break;
            }
        }
        if (tk.getType() == CToken.TK_RCUR) { // } が来たら、受理
            ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + " } がありません");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for (CParseRule st : statements) {
            if (st != null) {
                st.semanticCheck(pcx);
                setCType(st.getCType());
                setConstant(st.isConstant());
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        for (CParseRule st : statements) {
            st.codeGen(pcx);
        }
    }
}
