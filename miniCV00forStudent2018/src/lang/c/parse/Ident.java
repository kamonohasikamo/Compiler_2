package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Ident extends CParseRule {
	private CToken ident;
	private CSymbolTableEntry e;
	public Ident(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CSymbolTable cst = pcx.getTable();
		ident = tk;
		e = cst.searchTable(ident.getText());
		if(e == null) {
			pcx.fatalError(tk.toExplainString() + "この識別子は宣言されていません");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(e.getType());
		this.setConstant(e.getConstp());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; ident starts");
		if (ident != null) {
			if (e.getIsGlobal()) {
				o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: （大域）変数アドレスを積む<"
						+ ident.toExplainString() + ">");
			} else {
				o.println("\tMOV\t#" + e.getAddress() + ", R0\t\t; Ident: フレームポインタからの相対値");
				o.println("\tADD\tR4, R0\t\t; Ident: 相対アドレスを求める");
				o.println("\tMOV\tR0, (R6)+\t; Ident: （局所）変数アドレスを積む<"
						+ ident.toExplainString() + ">");
			}
		}
	//	o.println(";;; ident completes");
	}
}
