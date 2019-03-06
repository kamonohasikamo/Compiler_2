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
	public static final int TK_INT			= 13;	// int
	public static final int TK_CONST		= 14;	// const
	public static final int TK_CANMA		= 15;	// ,
	public static final int TK_LT			= 16;	// <
	public static final int TK_LE			= 17;	// <=
	public static final int TK_GT			= 18;	// >
	public static final int TK_GE			= 19;	// >=
	public static final int TK_EQ			= 20;	// ==
	public static final int TK_NE			= 21;	// !=
	public static final int TK_TRUE			= 22;	// true
	public static final int TK_FALSE		= 23;	// false
	public static final int TK_LCUR			= 24;	// {
	public static final int TK_RCUR			= 25;	// }
	public static final int TK_IF			= 26;	// if
	public static final int TK_ELSE			= 27;	// else
	public static final int TK_WHILE		= 28;	// while
	public static final int TK_INPUT		= 29;	// input
	public static final int TK_OUTPUT		= 30;	// output

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
