package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.*;

public class Declblock extends CParseRule {
	private int addrsize;
	private List<CParseRule> declarations;
	private List<CParseRule> statements;
	private CType rtrn = null;
	private CToken ident;
	public Declblock(CParseContext pcx, CToken ct) {
		ident = ct;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCUR;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CParseRule rule = null;
		declarations = new ArrayList<CParseRule>();
		statements = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_LCUR
		CSymbolTable cst = pcx.getTable();
		cst.setupLocalSymbolTable();		//local作成
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
		addrsize = cst.getAddrsize();		//局所変数の必要領域数
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
		cst.showTable();
		if (tk.getType() == CToken.TK_RCUR) {
			cst.deleteLocalSymbolTable();	//local削除
			ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "}がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (CParseRule decl : declarations) {
			decl.semanticCheck(pcx);
		}
		for (CParseRule stmt : statements) {
			stmt.semanticCheck(pcx);
			if (((Statement)stmt).checkStRt()) {
				if (rtrn != null) {
					if (stmt.getCType() != rtrn) {	//2回目以降のリターン文とのチェック
						pcx.fatalError("返り値の型が一部一致していません");
					}
				} else {
					rtrn = stmt.getCType();
					setCType(rtrn);
				}
			} else if (((Statement)stmt).checkStBr()) {
				if (rtrn != null) {
					if ((stmt.getCType() != null) && stmt.getCType() != rtrn) {	//2回目以降のリターン文とのチェック
						pcx.fatalError("返り値の型が一部一致していません");
					}
				} else {
					rtrn = stmt.getCType();
					setCType(rtrn);
				}
			}
		}
		if (rtrn == null ) {
			setCType(CType.getCType(CType.T_void));
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println("\tMOV\tR4, (R6)+\t; Declblock: 前のフレームポインタの値を退避");
		o.println("\tMOV\tR6, R4\t	; Declblock: フレームポインタを取り出す");
		for (CParseRule decl : declarations) {
			decl.codeGen(pcx);
		}
		o.println("\tADD\t#" + addrsize + ", R6\t	; Declblock: 局所変数の領域確保");
		for (CParseRule stmt : statements) {
			stmt.codeGen(pcx);
		}
	}
}