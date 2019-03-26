package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class StatementAssign extends CParseRule{
	private CParseRule primary;
	private CParseRule expression;
	public StatementAssign(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if (tk.getType() == CToken.TK_ASSIGN) {
				tk = ct.getNextToken(pcx);
			} else {
				primary = null;
			}
			if (Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if (tk.getType() == CToken.TK_SEMI) {
					ct.getNextToken(pcx);
				} else {
					expression = null;
				}
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null && expression != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			if (primary.getCType() != expression.getCType()) {
				pcx.fatalError("両辺の型が一致していません");
			}
			if (primary.getCType() == CType.getCType(CType.T_ary)
					|| primary.getCType() == CType.getCType(CType.T_pary)) {
					pcx.fatalError("配列に代入することはできません");
				}
			if (primary.isConstant() == true) {
				pcx.fatalError("左辺の識別子が変数ではありません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementAssign starts");
		if (primary != null) { primary.codeGen(pcx);}
		if (expression != null) { expression.codeGen(pcx);}
		o.println("\tMOV\t-(R6), R0\t; StatementAssign: 変数アドレスと代入値を取り出す");
		o.println("\tMOV\t-(R6), R1\t; StatementAssign:");
		o.println("\tMOV\tR0, (R1)\t; StatementAssign: アドレス先に代入値を書き込む");
		o.println(";;; statementAssign completes");
	}
}
