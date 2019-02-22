package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Term extends CParseRule {
	// term ::= term
	private CParseRule term;
	public Term(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = null, list = null;
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Factor.isFirst(tk)) {
			factor = new Factor(pcx);
			factor.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		while(true) {
			if(TermMult.isFirst(tk)) {
				list = new TermMult(pcx,factor);
				list.parse(pcx);
				factor = list;
				tk = ct.getCurrentToken(pcx);
			} else if(TermDiv.isFirst(tk)) {
				list = new TermDiv(pcx,factor);
				list.parse(pcx);
				factor = list;
				tk = ct.getCurrentToken(pcx);
			} else {
				break;
			}
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType());		// term の型をそのままコピー
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (term != null) { term.codeGen(pcx); }
		o.println(";;; term completes");
	}
}

class TermMult extends CParseRule {
	private CToken operand;
	private CParseRule left, right;
	public TermMult(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		operand = ct.getCurrentToken(pcx);	//「*」
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後ろはfactorでござるw");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 掛け算の型計算規則
		final int s[][] = {
		//		T_err			T_int			T_pint
			{	CType.T_err,	CType.T_err, 	CType.T_err},	// T_err
			{	CType.T_err,	CType.T_int, 	CType.T_err},	// T_int
			{	CType.T_err,	CType.T_err,	CType.T_err},	// T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType();		// *の左辺の型
			int rt = right.getCType().getType();	// *の右辺の型
			int nt = s[lt][rt];						// 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(operand.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は掛けられねーんですわ(笑)");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant());	// *の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);		// 左部分木のコード生成を頼む
			right.codeGen(pcx);		// 右部分木のコード生成を頼む
			o.println("\tJSR MULT     \t; TermMult: サブルーチンの呼び出し");
			o.println("\tSUB #2,  R6  \t; TermMult: スタックに積んである引数を降ろす");
			o.println("\tMOV R0, (R6)+\t; TermMult: R0レジスタにMUL結果があるとする");
		}
	}
}

class TermDiv extends CParseRule {
	private CToken operand;
	private CParseRule left, right;
	public TermDiv(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		operand = ct.getCurrentToken(pcx);	//「*」
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "/の後ろはfactorですョ(笑)");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
		//		T_err			T_int			T_pint
			{	CType.T_err,	CType.T_err, 	CType.T_err},	// T_err
			{	CType.T_err,	CType.T_int, 	CType.T_err},	// T_int
			{	CType.T_err,	CType.T_err,	CType.T_err},	// T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType();		// /の左辺の型
			int rt = right.getCType().getType();	// /の右辺の型
			int nt = s[lt][rt];						// 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(operand.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は割れませ～～～～～～んwwwwwwwww");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant());	// /の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);		// 左部分木のコード生成を頼む
			right.codeGen(pcx);		// 右部分木のコード生成を頼む
			o.println("\tJSR DIV      \t; TermDiv: サブルーチンの呼び出し");
			o.println("\tSUB #2,  R6  \t; TermDiv: スタックに積んである引数を降ろす");
			o.println("\tMOV R0, (R6)+\t; TermDiv: R0レジスタにDIV結果があるとする");
		}
	}
}