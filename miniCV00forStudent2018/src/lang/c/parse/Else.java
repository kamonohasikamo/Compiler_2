package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Else extends CParseRule {
	private CParseRule rule;
	public Else(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_ELSE
		if (If.isFirst(tk)) {
			rule = new If(pcx);
			rule.parse(pcx);
		} else if (BranchPart.isFirst(tk)) {
			rule = new BranchPart(pcx);
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
