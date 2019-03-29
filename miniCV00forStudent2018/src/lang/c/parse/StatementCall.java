package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementCall extends CParseRule{
	private CParseRule ident;
	private CToken idtk;
	public StatementCall(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//call
		if (Ident.isFirst(tk)) {
			idtk = tk;
			ident = new Ident(pcx);
			ident.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "識別子がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(がありません");
		}
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ")がありません");
		}
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println("\tJSR " + idtk.getText() + "\t\t\t; StatementCall: 関数呼び出し");
	}
}
