package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Arglist extends CParseRule {
	private List<CType> ctlist;
	private List<CParseRule> items;
	private CParseRule argitem;
	public Arglist(CParseContext pcx, List<CType> list) {
		ctlist = list;
	}
	public static boolean isFirst(CToken tk) {
		return ArgItem.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		items = new ArrayList<CParseRule>();
		if (ArgItem.isFirst(tk)) {		//必要ない条件
			argitem = new ArgItem(pcx, ctlist);
			argitem.parse(pcx);
			items.add(argitem);
		}
		tk = ct.getCurrentToken(pcx);
		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (ArgItem.isFirst(tk)) {
				argitem = new ArgItem(pcx, ctlist);
				argitem.parse(pcx);
				items.add(argitem);
			} else {
				pcx.fatalError(tk.toExplainString() + "引数がありません");
			}
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	}
}