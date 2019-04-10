package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Typelist extends CParseRule {
	private List<CType> ctlist;
	private List<CParseRule> items;
	private CParseRule typeitem;
	public Typelist(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return TypeItem.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ctlist = new ArrayList<CType>();
		items = new ArrayList<CParseRule>();
		if (TypeItem.isFirst(tk)) {		//必要ない条件
			typeitem = new TypeItem(pcx, ctlist);
			typeitem.parse(pcx);
			items.add(typeitem);
		}
		tk = ct.getCurrentToken(pcx);
		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (TypeItem.isFirst(tk)) {
				typeitem = new TypeItem(pcx, ctlist);
				typeitem.parse(pcx);
				items.add(typeitem);
			} else {
				pcx.fatalError(tk.toExplainString() + "引数の型指定がありません");
			}
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	}

	public List<CType> getList() {
		return ctlist;
	}
}