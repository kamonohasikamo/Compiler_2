package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Program extends CParseRule {
	// program ::= expression EOF
	private List<CParseRule> declarations;
	private List<CParseRule> function;
	public Program(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return  Declaration.isFirst(tk) || Function.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule rule = null;
		declarations = new ArrayList<CParseRule>();
		function = new ArrayList<CParseRule>();
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
			if (Function.isFirst(tk)) {
				rule = new Function(pcx);
				rule.parse(pcx);
				function.add(rule);
				tk = ct.getCurrentToken(pcx);
			} else {
				break;
			}
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (CParseRule declrton : declarations) {
			declrton.semanticCheck(pcx);
		}
		for (CParseRule stmt : function) {
			stmt.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
	//	o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
		for (CParseRule declrton : declarations) {
			declrton.codeGen(pcx);
		}
		for (CParseRule stmt : function) {
			stmt.codeGen(pcx);
		}
		//o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		o.println("\tHLT\t\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
	//	o.println(";;; program completes");
	}
}
