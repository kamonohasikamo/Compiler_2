package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;

public class Program extends CParseRule {
	// program ::= expression EOF
	private CParseRule program;

	public Program(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
		return Expression.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		program = new Expression(pcx);
		program.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "入力プログラム間違えてませんか？");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (program != null) { program.semanticCheck(pcx); }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t\t; ProgramNode: プログラムはっじまるぜぇ～～～！！初期実行文へGo！！！");
		// ここには将来、宣言に対するコード生成が必要
		if (program != null) {
			o.println("__START:");
			o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化初期化初期化初期化ぁ！！！");
			program.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		}
		o.println("\tHLT\t\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode: プログラム終了だぁ！！！！！！！！！！！！");
		o.println(";;; Program completes");
	}
}
