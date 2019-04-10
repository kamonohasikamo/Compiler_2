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

public class StatementCall extends CParseRule{
	private List<CParseRule> exlist;
	private CParseRule ident, expression;
	private CSymbolTableEntry e;
	private CToken idtk;
	public StatementCall(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//call
		CSymbolTable cst = pcx.getTable();
		exlist = new ArrayList<CParseRule>();
		if (Ident.isFirst(tk)) {
			idtk = tk;
			ident = new Ident(pcx);
			ident.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "識別子がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(がありません");
		}
		e = cst.searchFunc(idtk.getText());		//プロトタイプ宣言の引数とのチェックを行う
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			exlist.add(expression);
			tk = ct.getCurrentToken(pcx);
			while (tk.getType() == CToken.TK_COMMA) {
				tk = ct.getNextToken(pcx);
				if (Expression.isFirst(tk)) {
					expression = new Expression(pcx);
					expression.parse(pcx);
					exlist.add(expression);
				} else {
					pcx.fatalError(tk.toExplainString() + "引数がありません");
				}
				tk = ct.getCurrentToken(pcx);
			}
		}
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ")がありません");
		}
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
		}
		if (exlist.size() != e.getlist().size()) {
			pcx.fatalError("プロトタイプ宣言と関数呼び出し時の引数の個数が一致しません");
		}
		int count = 0;
		for (CParseRule ex : exlist) {
			ex.semanticCheck(pcx);
			if (ex.getCType() != e.getlist().get(count)) {
				pcx.fatalError("プロトタイプ宣言と関数呼び出し時の引数の型が一致しません");
			}
			count++;
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		for (int i = exlist.size() - 1; i >= 0; i--) {
			exlist.get(i).codeGen(pcx);
		}
		o.println("\tJSR " + idtk.getText() + "\t\t\t; StatementCall: 関数呼び出し");
		o.println("\tSUB\t#" + exlist.size() + ", R6\t\t; StatementCall: 引数をスタックから降ろす");
	}
}
