package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConstDecl extends CParseRule {
	private List<ConstItem> constItems;
	public ConstDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		ConstItem ci;
		constItems = new ArrayList<ConstItem>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_CONST) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_INT) {
				tk = ct.getNextToken(pcx);
				if(ConstItem.isFirst(tk)) {
					ci = new ConstItem(pcx);
					ci.parse(pcx);
					constItems.add(ci);
					tk = ct.getCurrentToken(pcx);
				} else {
					pcx.fatalError(tk.toExplainString() + "宣言する識別子がありません");
				}
				while(true) {
					if (tk.getType() == CToken.TK_COMMA) {
						tk = ct.getNextToken(pcx);
						if (ConstItem.isFirst(tk)) {
							ci = new ConstItem(pcx);
							ci.parse(pcx);
							constItems.add(ci);
							tk = ct.getCurrentToken(pcx);
						} else {
							pcx.fatalError(tk.toExplainString() + "連続して宣言する識別子がありません");
						}
					} else {
						break;
					}
				}
				tk = ct.getCurrentToken(pcx);
				if (tk.getType() == CToken.TK_SEMI) {
					tk = ct.getNextToken(pcx);
				} else {
					pcx.fatalError(tk.toExplainString() + "\";\"がありません");
				}
			} else {
				pcx.fatalError(tk.toExplainString() + "constの後にintが必要です");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		for (CParseRule ci : constItems) {
			ci.codeGen(pcx);
		}
	}
}