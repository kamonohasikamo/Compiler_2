package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS			= 2;	// +
	public static final int TK_SUB			= 3;	// -
	public static final int TK_AMP			= 4;	// &(アドレス)

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
