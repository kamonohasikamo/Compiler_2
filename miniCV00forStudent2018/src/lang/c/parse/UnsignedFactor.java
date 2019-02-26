 package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class UnsignedFactor extends CParseRule {
	// factor ::= number
	private CParseRule rule;
	public UnsignedFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) { // 構文定義の右辺を返す
		return Number.isFirst(tk) || FactorAmp.isFirst(tk) || tk.getType() == CToken.TK_LPAR || AddressToValue.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Number.isFirst(tk)) {
			rule = new Number(pcx);			// Number
			rule.parse(pcx);
		} else if (FactorAmp.isFirst(tk)) {	// FacterAmp
			rule = new FactorAmp(pcx);
			rule.parse(pcx);
		} else if (tk.getType() == CToken.TK_LPAR) { // [ expresstion ]を表現
			tk = ct.getNextToken(pcx);
			if (Expression.isFirst(tk)) {
				rule = new Expression(pcx);
				rule.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if (tk.getType() == CToken.TK_RPAR) {
					ct.getNextToken(pcx);
				} else {
					rule = null;
				}
			} else {
				rule = null;
			}
		} else if (AddressToValue.isFirst(tk)) { // AddressToValue
			rule = new AddressToValue(pcx);
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
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; UnsignedFactor starts");
		if (rule != null) { rule.codeGen(pcx);}
		o.println(";;; UnsignedFactor completes");
	}
}