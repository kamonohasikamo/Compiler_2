package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Primary extends CParseRule {
    private CParseRule rule;
    public Primary(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに書かれる
        return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if(PrimaryMult.isFirst(tk)) {
            rule = new PrimaryMult(pcx);
            rule.parse(pcx);
        } else if(Variable.isFirst(tk)) {
            rule = new Variable(pcx);
            rule.parse(pcx);;
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
        o.println(";;; Primary starts");
        if (rule != null) { rule.codeGen(pcx); }
        o.println(";;; Primary completes");
    }

    public boolean PMcheck() {
        return (rule instanceof PrimaryMult);
    }
}

class PrimaryMult extends CParseRule {
    private CToken point;
    private CParseRule variable;
    public PrimaryMult(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_MUL; // MULT variable なので
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        point = ct.getCurrentToken(pcx);	//「*」
        CToken tk = ct.getNextToken(pcx);
        if (Variable.isFirst(tk)) {
            variable = new Variable(pcx);
            variable.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        variable.semanticCheck(pcx);
        if (variable.getCType() != CType.getCType(CType.T_pint)) {
            pcx.fatalError(point.toExplainString() + variable.getCType().toString() + "型にポインタ参照はできません");
        }
        setConstant(variable.isConstant());
        if (variable.getCType() == CType.getCType(CType.T_pint)) { //絶対通る
            setCType(CType.getCType(CType.T_int));
        }

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (variable != null) {
            variable.codeGen(pcx);
            o.println("\tMOV\t-(R6),   R0\t; PrimaryMult: アドレスを取り出して、内容を参照して、積む<"
                    + point.toExplainString() + ">");
            o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
        }
    }
}