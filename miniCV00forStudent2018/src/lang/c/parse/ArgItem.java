package lang.c.parse;

import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ArgItem extends CParseRule {
	private List<CType> ctlist;
	private CType type;
	private CSymbolTableEntry e;
	//CSymbolTableEntryに必要な情報
	String name = null;
	int size = 0;
	boolean constp = false;

	public ArgItem(CParseContext pcx, List<CType> list) {
		this.ctlist = list;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);		//TK_INT
		CSymbolTable cst = pcx.getTable();
		type = CType.getCType(CType.T_int);
		if (tk.getType() == CToken.TK_MULT) {
			type = CType.getCType(CType.T_pint);
			tk = ct.getNextToken(pcx);
		}
		if (tk.getType() == CToken.TK_IDENT) {
			name = tk.getText();
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "識別子がありません");
		}
		if (tk.getType() == CToken.TK_LBRA) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_RBRA) {
				if (type == CType.getCType(CType.T_pint)) {
					type = CType.getCType(CType.T_pary);
				} else if (type == CType.getCType(CType.T_int)) {
					type = CType.getCType(CType.T_ary);
				}
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + " ]がありません");
			}
		}
		ctlist.add(type);
		e = cst.registerTable(name, type, size, constp);
		if (e == null) {
			pcx.fatalError("識別子" + name + "が重複して定義されています");
		}
		cst.setfd(name);		//size=1, FPからの相対値を変更する
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	}
}