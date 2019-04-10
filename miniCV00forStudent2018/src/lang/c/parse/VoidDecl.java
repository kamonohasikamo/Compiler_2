package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class VoidDecl extends CParseRule{
	private List<CParseRule> tlists;
	private CParseRule typelist;
	private CSymbolTableEntry e;
	//CSymbolTableEntryに必要な情報
	String name = null;
	CType type = CType.getCType(CType.T_void);
	int size = 0;
	boolean constp = true;

	public VoidDecl(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_VOID;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//void
		tlists = new ArrayList<CParseRule>();
		CSymbolTable cst = pcx.getTable();
		if (tk.getType() == CToken.TK_IDENT) {
			name = tk.getText();
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "識別子がありません");
		}
		if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(がありません");
		}
		if (Typelist.isFirst(tk)) {
			typelist = new Typelist(pcx);
			typelist.parse(pcx);
			tlists.add(typelist);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ")がありません");
		}
		e = cst.registerTable(name, type, size, constp);
		if (e == null) {
			pcx.fatalError("識別子" + name + "が重複して定義されています");
		}
		if (typelist != null) {
			e.setlist(((Typelist)typelist).getList());
			typelist = null;
		}
		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_IDENT) {
				name = tk.getText();
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "識別子がありません");
			}
			if (tk.getType() == CToken.TK_LPAR) {
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "(がありません");
			}
			if (Typelist.isFirst(tk)) {
				typelist = new Typelist(pcx);
				typelist.parse(pcx);
				tlists.add(typelist);
			}
			tk = ct.getCurrentToken(pcx);
			if (tk.getType() == CToken.TK_RPAR) {
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + ")がありません");
			}
			e = cst.registerTable(name, type, size, constp);
			if (e == null) {
				pcx.fatalError("識別子" + name + "が重複して定義されています");
			}
			if (typelist != null) {
				e.setlist(((Typelist)typelist).getList());
				typelist = null;
			}
		}
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	//	PrintStream o = pcx.getIOContext().getOutStream();
	}
}
