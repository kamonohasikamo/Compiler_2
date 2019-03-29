package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Condition extends CParseRule{
	private CParseRule condition = null;
	private boolean tf = true;
	public Condition(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Expression.isFirst(tk)
			|| tk.getType() == CToken.TK_TRUE
			|| tk.getType() == CToken.TK_FALSE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CParseRule expression = null;
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if (ConditionLT.isFirst(tk)) {
				condition = new ConditionLT(pcx, expression);
				condition.parse(pcx);
			} else if (ConditionLE.isFirst(tk)) {
				condition = new ConditionLE(pcx, expression);
				condition.parse(pcx);
			} else if (ConditionGT.isFirst(tk)) {
				condition = new ConditionGT(pcx, expression);
				condition.parse(pcx);
			} else if (ConditionGE.isFirst(tk)) {
				condition = new ConditionGE(pcx, expression);
				condition.parse(pcx);
			} else if (ConditionEQ.isFirst(tk)) {
				condition = new ConditionEQ(pcx, expression);
				condition.parse(pcx);
			} else if (ConditionNE.isFirst(tk)) {
				condition = new ConditionNE(pcx, expression);
				condition.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "条件部の式が不適切です");
			}
		} else if (tk.getType() == CToken.TK_TRUE) {
			tf = true;
			ct.getNextToken(pcx);
		} else if (tk.getType() == CToken.TK_FALSE) {
			tf = false;
			ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
			setCType(condition.getCType());
			setConstant(condition.isConstant());
		} else {
			setCType(CType.getCType(CType.T_bool));
			setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (condition != null) {
			condition.codeGen(pcx);
		} else {
			if (tf == true) {
				o.println("\tMOV\t#0x0001, (R6)+\t; Condition:");
			} else if (tf == false) {
				o.println("\tMOV\t#0x0000, (R6)+\t; Condition:");
			}
		}

	}
}
