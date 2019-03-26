package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Array extends CParseRule{
	private CParseRule expression;
	public Array(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);	// '['
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if (tk.getType() == CToken.TK_RBRA) {
				ct.getNextToken(pcx);
			} else {
				expression = null;
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			if(expression.getCType() != CType.getCType(CType.T_int)) {
				pcx.fatalError("配列の添え字はint型でないといけません");
			}
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; array starts");
		if (expression != null) { expression.codeGen(pcx);}
		o.println("\tMOV\t-(R6), R0\t; Array:添え字を取り出し、変数番地に変えてスタックに積む");
		o.println("\tMOV\t-(R6), R1\t; Array:変数番地の取り出し");
		o.println("\tADD\tR1, R0\t; Array:");
		o.println("\tMOV\tR0, (R6)+\t; Array:");
		o.println(";;; array completes");
	}
}
