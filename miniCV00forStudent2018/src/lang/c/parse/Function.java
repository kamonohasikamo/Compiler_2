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

public class Function extends CParseRule{
	private CParseRule declblock;
	private CSymbolTableEntry e;
	private CType ftype;
	private CToken ident;
	public Function(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_FUNC;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//func
		CSymbolTable cst = pcx.getTable();

		if (tk.getType() == CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			ftype = CType.getCType(CType.T_int);
			if (tk.getType() == CToken.TK_MULT) {
				tk = ct.getNextToken(pcx);
				ftype = CType.getCType(CType.T_pint);
			}
		} else if (tk.getType() == CToken.TK_VOID) {
			tk = ct.getNextToken(pcx);
			ftype = CType.getCType(CType.T_void);
		} else {
			pcx.fatalError(tk.toExplainString() + "型指定がされていません");
		}
		if (tk.getType() == CToken.TK_IDENT) {
			ident = tk;
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "識別子がありません");
		}
		if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(がありません");
		}
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ")がありません");
		}
		e = cst.searchTable(ident.getText());
		if(e == null) {
			pcx.fatalError(tk.toExplainString() + "この識別子は宣言されていません");
		}

		if (Declblock.isFirst(tk)) {
			declblock = new Declblock(pcx,ident);
			declblock.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "関数の内部がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ftype != e.getType()) {		//宣言と定義の型チェック
			pcx.fatalError("プロトタイプ宣言と関数定義での型が一致していません");
		}
		if (declblock != null) {
			declblock.semanticCheck(pcx);
			if (ftype != declblock.getCType()) {	//定義と返り値の型チェック
				pcx.fatalError("関数定義と返り値の型が一致していません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.print(ident.getText() + ":");		//ラベル付け
		if (declblock != null) { declblock.codeGen(pcx);}
		o.println("R_" + ident.getText() + ":MOV\tR4, R6\t\t; Function: 前のフレームポインタに戻す");
		o.println("\tMOV\t-(R6), R4\t; Function:");
		o.println("\tRET\t\t\t\t; Function: 関数終了");
	}
}
