package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConditionEQ extends CParseRule{
	private CToken op;
	private CParseRule left, right;
	public ConditionEQ(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_EQ;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = tk;
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(op.toExplainString() + "右辺が存在しません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError(op.toExplainString() +  "左辺の型 [" + left.getCType().toString() + "] と右辺の型 ["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition == (compare) starts");
		if (left != null && right != null) {
		left.codeGen(pcx);
		right.codeGen(pcx);
		int seq = pcx.getSeqId();
		o.println("\tMOV\t-(R6), R0\t; ConditionEQ: ２数を取り出して、比べる");
		o.println("\tMOV\t-(R6), R1\t; ConditionEQ:");
		o.println("\tMOV\t#0x0001, R2\t; ConditionEQ: set true");
		o.println("\tCMP\tR1, R0\t; ConditionEQ: R1<R0 = R1-R0<0");
		o.println("\tBRZ\tEQ" + seq + " ; ConditionEQ:");
		o.println("\tCLR\tR2\t\t; ConditionEQ: set false");
		o.println("EQ" + seq + ":\tMOV\tR2, (R6)+\t; ConditionEQ:");
		}
		o.println(";;;condition == (compare) completes");
	}
}
