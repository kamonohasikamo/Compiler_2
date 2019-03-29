package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementBranch extends CParseRule {
	private CParseRule branch;
	private CToken ident;
	public StatementBranch(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return If.isFirst(tk) || While.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (If.isFirst(tk)) {
			branch = new If(pcx, ident);
			branch.parse(pcx);
		} else if (While.isFirst(tk)) {
			branch = new While(pcx, ident);
			branch.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (branch != null) {
			branch.semanticCheck(pcx);
			setCType(branch.getCType());
			setConstant(branch.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		if (branch != null) {
			branch.codeGen(pcx);
		}
	}
}
