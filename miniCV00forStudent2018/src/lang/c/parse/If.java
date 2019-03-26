package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class If extends CParseRule {
	private CParseRule stcd, stbr, stel;
	public If(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IF;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_IF
		if (CondPart.isFirst(tk)) {
			stcd = new CondPart(pcx);
			stcd.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "条件式がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (BranchPart.isFirst(tk)) {
			stbr = new BranchPart(pcx);
			stbr.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "条件文内部に文がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (Else.isFirst(tk)) {
			stel = new Else(pcx);
			stel.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (stcd != null && stbr != null && stel != null) {
			stcd.semanticCheck(pcx);
			stbr.semanticCheck(pcx);
			stel.semanticCheck(pcx);
			setCType(stcd.getCType());
			setConstant(stcd.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (stcd != null && stbr != null) {
			int brc = pcx.getBrcId();
			stcd.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0; StatementIf: 条件式の結果を取り出す");
			o.println("\tBRZ\tF" + brc + "; StatementIf: 条件が偽の場合の分岐命令");
			stbr.codeGen(pcx);
			o.println("\tJMP\tUC" + brc + "; StatementIf: 条件文を抜けるための無条件分岐命令");
			o.println("F" + brc + ":\t; StatementIf: else文");
			if (stel != null) {
				stel.codeGen(pcx);
			}
			o.println("UC" + brc + ":\t; StatementIf: 条件文終了");
		}
	}
}
