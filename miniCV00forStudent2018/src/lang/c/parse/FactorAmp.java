package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP (number | primary )
	private CParseRule rule;
	private CToken amp;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
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
			setCType(CType.getCType(CType.T_pint));	// Typeをポインタにする
			setConstant(rule.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factoramp starts");
		if (rule != null) { rule.codeGen(pcx); }
		o.println(";;; factoramp completes");
	}
}