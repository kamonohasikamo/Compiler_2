package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Branchblock extends CParseRule {
	private List<CParseRule> statements;
	private CToken ident;
	public Branchblock(CParseContext pcx, CToken ident) {
		this.ident = ident;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCUR;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CParseRule st = null;
		statements = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);	//TK_LBRC
		while(true) {
			if (Statement.isFirst(tk)) {
				st = new Statement(pcx, ident);
				st.parse(pcx);
				statements.add(st);
				tk = ct.getCurrentToken(pcx);
			} else {
				break;
			}
		}
		if (tk.getType() == CToken.TK_RCUR) {
			ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "Branch: } がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		setCType(null);
		for (CParseRule st : statements) {
			if (st != null) {
				st.semanticCheck(pcx);
				if (((Statement)st).isStatementReturn()) {
					setCType(st.getCType());
				}
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		for (CParseRule st : statements) {
			st.codeGen(pcx);
		}
	}
}
