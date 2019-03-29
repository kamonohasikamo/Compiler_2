package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Factor extends CParseRule {
	private CParseRule factor;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return PlusFactor.isFirst(tk)
				|| MinusFactor.isFirst(tk)
				|| UnsignedFactor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (PlusFactor.isFirst(tk)) {
			factor = new PlusFactor(pcx);
			factor.parse(pcx);
		} else if (MinusFactor.isFirst(tk)) {
			factor = new MinusFactor(pcx);
			factor.parse(pcx);
		} else if (UnsignedFactor.isFirst(tk)) {
			factor = new UnsignedFactor(pcx);
			factor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType());
			setConstant(factor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; factor starts");
		if (factor != null) { factor.codeGen(pcx); }
	//	o.println(";;; factor completes");
	}
}

class PlusFactor extends CParseRule {
	private CParseRule unsignedfactor;
	public PlusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);
		if (UnsignedFactor.isFirst(tk)) {
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "+(sign)の後ろはunsignedfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedfactor != null) {
			unsignedfactor.semanticCheck(pcx);
			setCType(unsignedfactor.getCType());
			setConstant(unsignedfactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; plusfactor starts");
		if (unsignedfactor != null) { unsignedfactor.codeGen(pcx); }
	//	o.println(";;; plusfactor completes");
	}
}

class MinusFactor extends CParseRule {
	private CToken sign;
	private CParseRule unsignedfactor;
	public MinusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		sign = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if (UnsignedFactor.isFirst(tk)) {
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-(sign)の後ろはunsignedfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedfactor != null) {
			unsignedfactor.semanticCheck(pcx);
			if(unsignedfactor.getCType().getType() == CType.T_pint) {
				pcx.fatalError(sign.toExplainString() + "ポインタに符号は付けられません");
			}
			setCType(unsignedfactor.getCType());
			setConstant(unsignedfactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; minusfactor starts");
		if (unsignedfactor != null) { unsignedfactor.codeGen(pcx); }
		//0-num
		o.println("\tMOV\t-(R6), R0\t; MinusFactor: 符号を反転する ");
		o.println("\tMOV\t#0, R1\t; MinusFactor:");
		o.println("\tSUB\tR0, R1\t; MinusFactor:");
		o.println("\tMOV\tR1, (R6)+\t; MinusFactor:");
	//	o.println(";;; minusfactor completes");
	}
}