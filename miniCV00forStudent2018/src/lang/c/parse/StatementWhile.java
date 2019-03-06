package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementWhile extends CParseRule {
    private CParseRule stcd, stbr;
    public StatementWhile(CParseContext pcx) {
    }
    public static boolean isFirst(CToken tk) { // 構文定義の右辺がここに来る
        return tk.getType() == CToken.TK_WHILE;
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);	//TK_WHILE
        if (StatementCond.isFirst(tk)) { // StatementCondで条件式の有無を判定
            stcd = new StatementCond(pcx);
            stcd.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "条件式がありません");
        }
        // 以下は条件式が有るという前提で処理が書ける
        tk = ct.getCurrentToken(pcx);
        if (StatementBranch.isFirst(tk)) { // StatementBranchで { が読み込まれたということ
            stbr = new StatementBranch(pcx);
            stbr.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "WHILE文の条件文内部に文がありません");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (stcd != null && stbr != null) {
            stcd.semanticCheck(pcx);
            stbr.semanticCheck(pcx);
            setCType(stcd.getCType());
            setConstant(stcd.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (stcd != null && stbr != null) {
            int brc = pcx.getBrcId();
            o.println("UC" + brc + ":\t; StatementWhile: 再度条件式を評価するための分岐先");
            stcd.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0\t; StatementWhile: 条件式の結果を取り出す");
            o.println("\tBRZ\tF" + brc + "; StatementWhile: 条件が偽の場合の分岐命令");
            stbr.codeGen(pcx);
            o.println("\tJMP\tUC" + brc + "; StatementWhile: 再度条件式を評価するための無条件分岐命令");
            o.println("F" + brc + ":\t; StatementWhile: 条件文終了");
        }
    }
}
