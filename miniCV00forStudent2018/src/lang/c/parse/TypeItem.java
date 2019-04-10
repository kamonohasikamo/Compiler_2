package lang.c.parse;

import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class TypeItem extends CParseRule{
	private List<CType> ctlist;
	private CType type;
	public TypeItem(CParseContext pcx, List<CType> list) {
		ctlist = list;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_INT
		type = CType.getCType(CType.T_int);
		if (tk.getType() == CToken.TK_MULT) {
			type = CType.getCType(CType.T_pint);
			tk = ct.getNextToken(pcx);
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
				pcx.fatalError(tk.toExplainString() + "]がありません");
			}
		}
		ctlist.add(type);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	}
}