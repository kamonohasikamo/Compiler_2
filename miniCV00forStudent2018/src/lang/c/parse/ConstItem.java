package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConstItem extends CParseRule {
	private CSymbolTableEntry e;
	//CSymbolTableEntryに必要な情報
	String name = null;
	CType type;
	int size = 1;		//定数値を入れる
	boolean constp = true;

	int addr = 0;

	public ConstItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CSymbolTable cst = pcx.getTable();

		if (tk.getType() == CToken.TK_MULT) {
			type = CType.getCType(CType.T_pint);
			tk = ct.getNextToken(pcx);
		} else {
			type = CType.getCType(CType.T_int);
		}
		if (tk.getType() == CToken.TK_IDENT) {
			name = tk.getText();
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_ASSIGN) {
				tk = ct.getNextToken(pcx);
				if (tk.getType() == CToken.TK_AMP) {
					if (type != CType.getCType(CType.T_pint)) {
						pcx.fatalError(tk.toExplainString() + "両辺の型が一致していません");
					}
					tk = ct.getNextToken(pcx);			//↑↓意味解析！？
				} else {
					if (type != CType.getCType(CType.T_int)) {
						pcx.fatalError(tk.toExplainString() + "両辺の型が一致していません");
					}
				}
				if (tk.getType() == CToken.TK_NUM) {
					size = tk.getIntValue();
					tk = ct.getNextToken(pcx);
				} else {
					pcx.fatalError(tk.toExplainString() + "=の後に定数がありません");
				}
			} else {
				pcx.fatalError(tk.toExplainString() + "識別子の後に=がありません");
			}
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後に識別子がありません");
		}
		addr = cst.getAddrsize();
		e = cst.registerTable(name, type, size, constp);
		if (e == null) {
			pcx.fatalError("識別子" + name + "が重複して定義されています");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; ConstItem starts");
		if (e.getIsGlobal()) {
			o.println(name + ":\t.WORD\t" + size + "\t; ConstItem: 定数の領域確保");
		} else {
			o.println("\tMOV\tR4, R0\t; ConstItem: （局所）定数の初期化");
			o.println("\tADD\t#" + addr +", R0\t; ConstItem:");
			o.println("\tMOV\t#" + size + ", (R0)\t; ConstItem:");
		}
	//	o.println(";;; ConstItem completes");
	}
}