package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS			= 2;	// +
	public static final int TK_SUB			= 3;	// -
	public static final int TK_AMP			= 4;	// &(アドレス)
	public static final int TK_DIV			= 5;	// /(DIV)
	public static final int TK_MUL			= 6;	// *
	public static final int TK_LPAR			= 7;	// (
	public static final int TK_RPAR			= 8;	// )
	public static final int TK_LBRA			= 9;	// [ (配列用)
	public static final int TK_RBRA			= 10;	// ] (配列用)
	public static final int TK_ASSIGN		= 11;	// =
	public static final int TK_SEMI			= 12;	// ;

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
