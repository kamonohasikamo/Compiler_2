 package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class CallFunc extends CParseRule {
	private CParseRule ident, call;
	private CToken idtk;
	public CallFunc(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//@
		if (Ident.isFirst(tk)) {
			idtk = tk;
			ident = new Ident(pcx);
			ident.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "識別子がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (Call.isFirst(tk)) {
			call = new Call(pcx, idtk);
			call.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "()がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			setCType(ident.getCType());
			setConstant(ident.isConstant());
		}
		if (call != null) {
			call.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (call != null) {
			call.codeGen(pcx);
		}
		o.println("\tMOV\tR0, (R6)+\t; CallFunc: 返り値(R0)をスタックに積む");
	}
}