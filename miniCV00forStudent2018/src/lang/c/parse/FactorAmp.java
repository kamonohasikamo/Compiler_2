package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	private CParseRule rule;
	private CToken amp;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		amp = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Number.isFirst(tk)) {
			rule = new Number(pcx);		//Number
			rule.parse(pcx);
		} else if(Primary.isFirst(tk)) {
			rule = new Primary(pcx);
			rule.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (rule != null) {
			rule.semanticCheck(pcx);
			if (rule instanceof Primary && ((Primary)rule).PMcheck()) {
				pcx.fatalError(amp.toExplainString() + "&の後ろに*がきてはいけません");
			}
			if (rule.getCType() == CType.getCType(CType.T_int)) {
				setCType(CType.getCType(CType.T_pint));
			} else if (rule.getCType() == CType.getCType(CType.T_ary)) {
				setCType(CType.getCType(CType.T_ary));
			}
			setConstant(rule.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; factoramp starts");
		if (rule != null) { rule.codeGen(pcx); }
	//	o.println(";;; factoramp completes");
	}
}