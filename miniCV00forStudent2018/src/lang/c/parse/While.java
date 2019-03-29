package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class While extends CParseRule {
	private CParseRule stcd, stbr;
	private CToken ident;
	public While(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_WHILE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_WHILE
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
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (stcd != null && stbr != null) {
			stcd.semanticCheck(pcx);
			stbr.semanticCheck(pcx);
			setCType(stbr.getCType());
			setConstant(stbr.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (stcd != null && stbr != null) {
			int brc = pcx.getBrcId();
			o.println("UC" + brc + ":\t\t\t\t; While: 再度条件式を評価するための分岐先");
			stcd.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; While: 条件式の結果を取り出す");
			o.println("\tBRZ\tF" + brc + "\t\t\t; While: 条件が偽の場合の分岐命令");
			stbr.codeGen(pcx);
			o.println("\tJMP\tUC" + brc + "\t\t\t; While: 再度条件式を評価するための無条件分岐命令");
			o.println("F" + brc + ":\t\t\t\t\t; While: 条件文終了");
		}
	}
}
