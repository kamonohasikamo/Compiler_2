package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Call extends CParseRule{
	public Call(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);	// '('
		tk = ct.getNextToken(pcx);
		if (tk.getType() == CToken.TK_RPAR) {
			ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ")がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	}
}
