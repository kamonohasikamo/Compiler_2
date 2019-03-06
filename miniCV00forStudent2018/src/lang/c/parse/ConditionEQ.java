package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

import java.io.PrintStream;

public class ConditionEQ extends CParseRule {
    private CToken operand;
    private CParseRule left, right;
    public ConditionEQ(CParseContext pcx, CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_EQ;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        operand = tk;
        tk = ct.getNextToken(pcx);
        if (Expression.isFirst(tk)) {
            right = new Expression(pcx);
            right.parse(pcx);
        } else {
            pcx.fatalError(operand.toExplainString() + "右辺が存在しませんね＾＾＾；；；");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            if (!left.getCType().equals(right.getCType())) {
                pcx.fatalError(operand.toExplainString() +  "左辺の型 [" + left.getCType().toString() + "] と右辺の型 ["
                        + right.getCType().toString() + "] が一致しないので比較できません");
            } else {
                this.setCType(CType.getCType(CType.T_bool));
                this.setConstant(true);
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Condition == (compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            int seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6),   R0\t; ConditionEQ: ２数を取り出して、比べる");
            o.println("\tMOV\t-(R6),   R1\t; ConditionEQ:");
            o.println("\tMOV\t#0x0001, R2\t; ConditionEQ: set true");
            o.println("\tCMP\tR1,      R0\t; ConditionEQ: R1<R0 = R1-R0<0");
            o.println("\tBRZ\tEQ" + seq + " ; ConditionEQ:");
            o.println("\tCLR\tR2\t\t; ConditionEQ: set false");
            o.println("EQ" + seq + ":\tMOV\tR2, (R6)+\t; ConditionEQ:");
        }
        o.println(";;; Condition == (compare) completes");
    }
}