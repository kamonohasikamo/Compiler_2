package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class Variable extends CParseRule {
    private CParseRule ident;
    private CParseRule array;
    public Variable(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return Ident.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (Ident.isFirst(tk)) {    // 通常の変数のとき
            ident = new Ident(pcx);
            ident.parse(pcx);
        }
        tk = ct.getCurrentToken(pcx);
        if (Array.isFirst(tk)) {	// 配列のとき
            array = new Array(pcx);
            array.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null) {
            ident.semanticCheck(pcx);
            if (array != null && !(ident.getCType() == CType.getCType(CType.T_array)
                    || ident.getCType() == CType.getCType(CType.T_parray))) {
                pcx.fatalError("配列は" + ident.getCType().toString() + "型ではありません");
            }
            setCType(ident.getCType());
            if (array != null) {
                array.semanticCheck(pcx);
                if (ident.getCType() == CType.getCType(CType.T_array)) {		//変換する
                    setCType(CType.getCType(CType.T_int));		// x = a[0] できるからよい？
                } else if (ident.getCType() == CType.getCType(CType.T_parray)) {
                    setCType(CType.getCType(CType.T_pint));
                }
            }
            setConstant(ident.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Variable starts");
        if (ident != null) { ident.codeGen(pcx);}
        if (array != null) { array.codeGen(pcx);}
        o.println(";;; Variable completes");
    }
}
