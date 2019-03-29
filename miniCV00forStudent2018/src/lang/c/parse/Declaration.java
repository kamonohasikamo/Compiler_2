package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Declaration extends CParseRule {
	private CParseRule rule;
	public Declaration(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk) || VoidDecl.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (IntDecl.isFirst(tk)) {
			rule = new IntDecl(pcx);
			rule.parse(pcx);
		} else if(ConstDecl.isFirst(tk)) {
			rule = new ConstDecl(pcx);
			rule.parse(pcx);
		} else if(VoidDecl.isFirst(tk)) {
			rule = new VoidDecl(pcx);
			rule.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (rule != null) {
			rule.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		if (rule != null) { rule.codeGen(pcx); }
	}
}