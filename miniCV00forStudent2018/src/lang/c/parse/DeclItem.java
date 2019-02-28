package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.SymbolTable;
import lang.SymbolTableEntry;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class DeclItem extends CParseRule {
    //CSymbolTableEntryに必要な情報
    String name = null;
    CType type;
    int size = 1;
    boolean constp = false;
    boolean isGlobal = true;
    int addr = 0;	//とりあえず

    public DeclItem(CParseContext pcx) {
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
        } else {
            pcx.fatalError(tk.toExplainString() + "*の後に識別子がありません");
        }
        if (tk.getType() == CToken.TK_LBRA) {
            tk = ct.getNextToken(pcx);
            if (tk.getType() == CToken.TK_NUM) {
                size = tk.getIntValue();
                tk = ct.getNextToken(pcx);
                if (tk.getType() == CToken.TK_RBRA) {
                    tk = ct.getNextToken(pcx);
                } else {
                    pcx.fatalError(tk.toExplainString() + "要素数の後の]がありません");
                }
            } else {
                pcx.fatalError(tk.toExplainString() + "[の後に要素数が指定されていません");
            }
            if (type == CType.getCType(CType.T_pint)) {
                type = CType.getCType(CType.T_parray);
            } else {
                type = CType.getCType(CType.T_array);
            }
        }
        if (!cst.registerGlobal(name, type, size, constp, isGlobal, addr)) {
            pcx.fatalError("識別子" + name + "が重複して定義されています");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        //	o.println(";;; DeclItem starts");
        if (type == CType.getCType(CType.T_int) || type == CType.getCType(CType.T_pint)) {
            o.println("\t"+ name + ":\t.WORD\t0\t; DeclItem: 変数の領域確保");
        } else {
            o.println("\t"+ name + ":\t.BLKW\t" + size + "\t; DeclItem: 変数の領域確保");
        }
        //	o.println(";;; DeclItem completes");
    }
}