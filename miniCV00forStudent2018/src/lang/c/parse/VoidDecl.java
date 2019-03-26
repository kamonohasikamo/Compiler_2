package lang.c.parse;

import lang.FatalErrorException;
import lang.c.*;

public class VoidDecl extends CParseRule {
    private CSymbolTableEntry entry;
    String name = null;
    CType type = CType.getCType(CType.T_void);
    int address_size = 0; // アドレスサイズ
    boolean constp = true;

    public VoidDecl(CParseContext pcx) {

    }

    public static boolean isFirst(CToken tk) { // VOID型が来た
        return tk.getType() == CToken.TK_VOID;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx);
        CSymbolTable cst = pcx.getTable();

        if (tk.getType() == CToken.TK_IDENT) { // void型でIDENTがきたら、名前の登録
            name = tk.getText();
            tk = ct.getNextToken(pcx);
        } else { // 識別子のないvoidはアウト
            pcx.fatalError(tk.toExplainString() + "識別子がありませんケド…(笑)");
        }

        if (tk.getType() == CToken.TK_LPAR) { // 関数なので()が必要
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "(がありませんョｗｗｗ");
        }

        if (tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + ")がありませんねぇ～～～～～～～");
        }

        entry = cst.registerTable(name, type, address_size, constp);
        if (entry == null) {
            pcx.fatalError("識別子である" + name + "が重複して定義されています。書き直そうね＾＾;");
        }
        while (tk.getType() == CToken.TK_COMMA) { // コンマがあったら、無くなるまで定義が可能(ex: int a, b, c; みたいな定義の仕方)
            if (tk.getType() == CToken.TK_IDENT) { // void型でIDENTがきたら、名前の登録
                name = tk.getText();
                tk = ct.getNextToken(pcx);
            } else { // 識別子のないvoidはアウト
                pcx.fatalError(tk.toExplainString() + "識別子がありませんケド…(笑)");
            }

            if (tk.getType() == CToken.TK_LPAR) { // 関数なので()が必要
                tk = ct.getNextToken(pcx);
            } else {
                pcx.fatalError(tk.toExplainString() + "(がありませんョｗｗｗ");
            }

            if (tk.getType() == CToken.TK_RPAR) {
                tk = ct.getNextToken(pcx);
            } else {
                pcx.fatalError(tk.toExplainString() + ")がありませんねぇ～～～～～～～");
            }

            entry = cst.registerTable(name, type, address_size, constp);
            if (entry == null) {
                pcx.fatalError("識別子である" + name + "が重複して定義されています。書き直そうね＾＾;");
            }
        }
        if (tk.getType() == CToken.TK_SEMI) { // ; が来たらそこで宣言終了
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "；がないってやばくね？Pythonじゃないんだから…");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        //	PrintStream o = pcx.getIOContext().getOutStream();
    }
}
