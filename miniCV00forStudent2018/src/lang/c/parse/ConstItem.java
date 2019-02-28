package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConstItem extends CParseRule {
    //CSymbolTableEntryに必要な情報
    String name = null;
    CType type;
    int size = 1;		//定数値を入れる
    boolean constp = true;
    boolean isGlobal = true;
    int addr = 0;	//とりあえず

    public ConstItem(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_MUL || tk.getType() == CToken.TK_IDENT;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        CSymbolTable cst = pcx.getTable();

        if (tk.getType() == CToken.TK_MUL) {
            type = CType.getCType(CType.T_pint);
            tk = ct.getNextToken(pcx);
        } else {
            type = CType.getCType(CType.T_int);
        }
        if (tk.getType() == CToken.TK_IDENT) {
            name = tk.getText();
            tk = ct.getNextToken(pcx);
            if (tk.getType() == CToken.TK_ASSIGN) {
                tk = ct.getNextToken(pcx);
                if (tk.getType() == CToken.TK_AMP) {
                    if (type != CType.getCType(CType.T_pint)) {
                        pcx.fatalError(tk.toExplainString() + "両辺の型が一致していません");
                    }
                    tk = ct.getNextToken(pcx);
                } else {
                    if (type != CType.getCType(CType.T_int)) {
                        pcx.fatalError(tk.toExplainString() + "両辺の型が一致していません");
                    }
                }
                if (tk.getType() == CToken.TK_NUM) {
                    size = tk.getIntValue();
                    tk = ct.getNextToken(pcx);
                } else {
                    pcx.fatalError(tk.toExplainString() + "=の後に定数がありません");
                }
            } else {
                pcx.fatalError(tk.toExplainString() + "識別子の後に=がありません");
            }
        } else {
            pcx.fatalError(tk.toExplainString() + "*の後に識別子がありません");
        }
        if (!cst.registerGlobal(name, type, size, constp, isGlobal, addr)) {
            pcx.fatalError("識別子" + name + "が重複して定義されています");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        //	o.println(";;; ConstItem starts");
        o.println("\t"+ name + ":\t.WORD\t" + size + "\t; ConstItem: 定数の領域確保");
        //	o.println(";;; ConstItem completes");
    }
}