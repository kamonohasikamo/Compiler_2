package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP number
	private CParseRule pnumber;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);
		if(Number.isFirst(tk)) {
			pnumber = new Number(pcx);		//Number
			pnumber.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (pnumber != null) {
			pnumber.semanticCheck(pcx);
			setCType(CType.getCType(CType.T_pint));	// Typeをポインタにする
			setConstant(pnumber.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factoramp starts");
		if (pnumber != null) { pnumber.codeGen(pcx); }
		o.println(";;; factoramp completes");
	}
}