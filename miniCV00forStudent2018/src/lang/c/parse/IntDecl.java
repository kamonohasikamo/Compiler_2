package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class IntDecl extends CParseRule {
	private List<DeclItem> declItems;
	public IntDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		DeclItem di;
		declItems = new ArrayList<DeclItem>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			if(DeclItem.isFirst(tk)) {
				di = new DeclItem(pcx);
				di.parse(pcx);
				declItems.add(di);
				tk = ct.getCurrentToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "宣言する識別子がありません");
			}
			while(true) {
				if (tk.getType() == CToken.TK_COMMA) {
					tk = ct.getNextToken(pcx);
					if (DeclItem.isFirst(tk)) {
						di = new DeclItem(pcx);
						di.parse(pcx);
						declItems.add(di);
						tk = ct.getCurrentToken(pcx);
					} else {
						pcx.fatalError(tk.toExplainString() + "連続して宣言する識別子がありません");
					}
				} else {
					break;
				}
			}
			if (tk.getType() == CToken.TK_SEMI) {
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "\";\"がありません");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		for (CParseRule di : declItems) {
			di.codeGen(pcx);
		}
	}
}