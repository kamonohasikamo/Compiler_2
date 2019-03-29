package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class If extends CParseRule {
	private CParseRule stcd, stbr, stel;
	private CToken ident;
	public If(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IF;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_IF
		if (Condblock.isFirst(tk)) {
			stcd = new Condblock(pcx);
			stcd.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "条件式がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (Branchblock.isFirst(tk)) {
			stbr = new Branchblock(pcx, ident);
			stbr.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "条件文内部に文がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (Else.isFirst(tk)) {
			stel = new Else(pcx, ident);
			stel.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (stcd != null && stbr != null) {
			stcd.semanticCheck(pcx);
			stbr.semanticCheck(pcx);
		}
		if (stel != null) {
			stel.semanticCheck(pcx);
			if (stbr.getCType() == null) {
				setCType(stel.getCType());
			} else if (stel.getCType() == null) {
				setCType(stbr.getCType());
			} else if (stbr.getCType() == stel.getCType()) {
				setCType(stbr.getCType());
			} else {
				pcx.fatalError("IF文とELSE文での返り値の型が一致していません");
			}
		} else {
			setCType(stbr.getCType());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (stcd != null && stbr != null) {
			int brc = pcx.getBrcId();
			stcd.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; If: 条件式の結果を取り出す");
			o.println("\tBRZ\tF" + brc + "\t\t\t; If: 条件が偽の場合の分岐命令");
			stbr.codeGen(pcx);
			o.println("\tJMP\tUC" + brc + "\t\t\t; If: 条件文を抜けるための無条件分岐命令");
			o.println("F" + brc + ":\t\t\t\t\t; If: else文");
			if (stel != null) {
				stel.codeGen(pcx);
			}
			o.println("UC" + brc + ":\t\t\t\t; If: 条件文終了");
		}
	}
}
