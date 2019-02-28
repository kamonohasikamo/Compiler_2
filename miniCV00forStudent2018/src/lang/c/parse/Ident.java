package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Ident extends CParseRule {
    private CToken ident;
    public Ident(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_IDENT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        ident = tk;
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(CType.getCType(CType.T_int));
        this.setConstant(false);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Ident starts");
        if (ident != null) {
            o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
                    + ident.toExplainString() + ">");
        }
        o.println(";;; Ident completes");
    }
}
