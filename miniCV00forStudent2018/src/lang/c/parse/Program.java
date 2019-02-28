package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import lang.*;
import lang.c.*;

public class Program extends CParseRule {
	// program ::= { declaration } { expression }EOF
	private List<CParseRule> statements;
	private List<CParseRule> declarations;
	private CParseRule statement;

	public Program(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
		return Declaration.isFirst(tk) ||Statement.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule rule = null;
		declarations	= new ArrayList<CParseRule>();
		statements		= new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while(true) {
			if (Declaration.isFirst(tk)) {
				rule = new Declaration(pcx);
				rule.parse(pcx);
				declarations.add(rule);
				tk = ct.getCurrentToken(pcx);
			} else {
				break;
			}
		}
		while(true) {
			if (Statement.isFirst(tk)) {
				rule = new Statement(pcx);
				rule.parse(pcx);
				statements.add(rule);
				tk = ct.getCurrentToken(pcx);
			} else {
				break;
			}
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "入力プログラム間違えてませんか？");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (CParseRule program : declarations) {
			if (program != null) { program.semanticCheck(pcx); }
		}
		for (CParseRule program : statements) {
			if (program != null) { program.semanticCheck(pcx); }
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t\t; ProgramNode: プログラムはっじまるぜぇ～～～！！初期実行文へGo！！！");
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化初期化初期化初期化ぁ！！！");
		for (CParseRule program : declarations) {
			program.codeGen(pcx);
		}
		for (CParseRule program : statements) {
			program.codeGen(pcx);
		}
		// o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		o.println("\tHLT\t\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode: プログラム終了だぁ！！！！！！！！！！！！");
		o.println(";;; Program completes");
	}
}
