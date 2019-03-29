package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class StatementReturn extends CParseRule{
	private CParseRule expression = null;
	private CToken ident;
	public StatementReturn(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_RETURN;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//return
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		setCType(CType.getCType(CType.T_void));
		if(expression != null) {
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (expression != null) {
			expression.codeGen(pcx);
		}
		if (this.getCType() != CType.getCType(CType.T_void) ) {
			o.println("\tMOV\t-(R6), R0\t; StatementReturn: 返り値をR0に残す");
		}
		o.println("\tJMP\tR_" + ident.getText() + "\t\t\t; StatementReturn: 関数の最後にジャンプする");
	}
}
