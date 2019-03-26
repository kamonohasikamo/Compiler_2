package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Variable extends CParseRule{
	private CParseRule ident;
	private CParseRule array;
	public Variable(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Ident.isFirst(tk)) {
			ident = new Ident(pcx);
			ident.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (Array.isFirst(tk)) {	//配列の場合
			array = new Array(pcx);
			array.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			if (array != null && !(ident.getCType() == CType.getCType(CType.T_ary)
					|| ident.getCType() == CType.getCType(CType.T_pary))) {
				pcx.fatalError("配列は" + ident.getCType().toString() + "型ではありません");
			}
			setCType(ident.getCType());
			if (array != null) {
				array.semanticCheck(pcx);
				if (ident.getCType() == CType.getCType(CType.T_ary)) {		//変換しちゃう
					setCType(CType.getCType(CType.T_int));		// x = a[0] できるからよい？
				} else if (ident.getCType() == CType.getCType(CType.T_pary)) {
					setCType(CType.getCType(CType.T_pint));
				}
			}
			setConstant(ident.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if (ident != null) { ident.codeGen(pcx);}
		if (array != null) { array.codeGen(pcx);}
		o.println(";;; variable completes");
	}
}
