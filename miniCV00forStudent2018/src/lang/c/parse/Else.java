package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Else extends CParseRule {
	private CParseRule rule;
	private CToken ident;
	public Else(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_ELSE
		if (If.isFirst(tk)) {
			rule = new If(pcx, ident);
			rule.parse(pcx);
		} else if (Branchblock.isFirst(tk)) {
			rule = new Branchblock(pcx, ident);
			rule.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "条件文内部に文がありません");
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
		if (rule != null) {
			rule.codeGen(pcx);
		}
	}
}
