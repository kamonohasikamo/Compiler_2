package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Ident extends CParseRule {
    private CToken ident;
    private CSymbolTableEntry e;
    public Ident(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_IDENT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        CSymbolTable cst = pcx.getTable();
        ident = tk;
        e = cst.searchGlobal(ident.getText()); // searchGlobalで記号表に登録されているかどうかチェック(実験9で重要になる)
        if(e == null) {
            pcx.fatalError(tk.toExplainString() + "この識別子は宣言されていません");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(e.getType());
        this.setConstant(e.getConstp());
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ident starts");
        if (ident != null) {
            o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
                    + ident.toExplainString() + ">");
        }
        o.println(";;; ident completes");
    }
}
