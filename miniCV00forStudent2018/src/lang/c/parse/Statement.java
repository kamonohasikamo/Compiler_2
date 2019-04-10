package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Statement extends CParseRule{
	private CParseRule rule;
	private CToken ident;
	public Statement(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk)
				|| StatementBranch.isFirst(tk)
				|| StatementIO.isFirst(tk)
				|| StatementCall.isFirst(tk)
				|| StatementReturn.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (StatementAssign.isFirst(tk)) {
			rule = new StatementAssign(pcx);
			rule.parse(pcx);
		} else if (StatementBranch.isFirst(tk)) {
			rule = new StatementBranch(pcx, ident);
			rule.parse(pcx);
		} else if (StatementIO.isFirst(tk)) {
			rule = new StatementIO(pcx);
			rule.parse(pcx);
		} else if (StatementCall.isFirst(tk)) {
			rule = new StatementCall(pcx);
			rule.parse(pcx);
		} else if (StatementReturn.isFirst(tk)) {
			rule = new StatementReturn(pcx, ident);
			rule.parse(pcx);
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
	//	PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; statement starts");
		if (rule != null) { rule.codeGen(pcx);}
	//	o.println(";;; statement completes");
	}

	public boolean checkStRt() {
			return rule instanceof StatementReturn;
	}

	public boolean checkStBr() {
		return rule instanceof StatementBranch;
}
}