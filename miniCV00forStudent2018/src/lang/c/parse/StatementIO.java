package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementIO extends CParseRule {
	private CParseRule io;
	public StatementIO(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Input.isFirst(tk) || Output.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Input.isFirst(tk)) {
			io = new Input(pcx);
			io.parse(pcx);
		} else if (Output.isFirst(tk)) {
			io = new Output(pcx);
			io.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (io != null) {
			io.semanticCheck(pcx);
			setCType(io.getCType());
			setConstant(io.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		if (io != null) {

			io.codeGen(pcx);
		}
	}
}

class Input extends CParseRule {
	private CParseRule ident;
	public Input(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_INPUT
		if (Ident.isFirst(tk)) {
			ident = new Ident(pcx);
			ident.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "入力する識別子がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			setCType(ident.getCType());
			setConstant(ident.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (ident != null) {
			ident.codeGen(pcx);
		}
		o.println("\tMOV\t#0xFFE0, R0; Input: メモリマップドI/O用アドレスを設定");
		o.println("\tMOV\t-(R6), R1; Input: 変数アドレスを取り出す");
		o.println("\tMOV\t(R0), (R1); Input: 入力値を変数のアドレス先に書き込む");
	}
}

class Output extends CParseRule {
	private CParseRule expression;
	public Output(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_OUTPUT
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "出力する値がありません");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t#0xFFE0, R0; Output: メモリマップドI/O用アドレスを設定");
		o.println("\tMOV\t-(R6), R1; Output: 出力する値を取り出す");
		o.println("\tMOV\tR1, (R0); Output: 入力値を変数のアドレス先に書き込む");
	}
}