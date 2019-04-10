package lang.c.parse;

import java.io.PrintStream;
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

public class Function extends CParseRule{
	private List<CType> argtypes;
	private CParseRule arglist, declblock;
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
		argtypes = new ArrayList<CType>();
		cst.setupLocalSymbolTable();		//local作成
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
		if (Arglist.isFirst(tk)) {
			arglist = new Arglist(pcx, argtypes);
			arglist.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ")がありません");
		}
		e = cst.searchFunc(ident.getText());
		if(e == null) {
			pcx.fatalError(tk.toExplainString() + "この関数は宣言されていません");
		}
		if (Declblock.isFirst(tk)) {
			declblock = new Declblock(pcx,ident);
			declblock.parse(pcx);
			cst.showTable();
			cst.deleteLocalSymbolTable();	//local削除
		} else {
			pcx.fatalError(tk.toExplainString() + "関数の内部がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ftype != e.getType()) {		//宣言と定義の型チェック
			pcx.fatalError("プロトタイプ宣言と関数定義での型が一致していません");
		}
		if  ((argtypes.size() != e.getlist().size())) {
			pcx.fatalError("プロトタイプ宣言と関数定義の引数の個数が一致しません");
		}
		int count = 0;
		for (CType at : argtypes) {
			if (at != e.getlist().get(count)) {
				pcx.fatalError("プロトタイプ宣言と関数定義の引数の型が一致しません");
			}
			count++;
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
