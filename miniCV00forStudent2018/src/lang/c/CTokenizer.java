package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.Tokenizer;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule	rule;
	private int			lineNo, colNo;
	private char		backCh;
	private boolean		backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1; colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n')  { colNo = 1; ++lineNo; }
//		System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}
	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') { --lineNo; }
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;
	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}
	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
//		System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}

	private boolean isAlpha(char ch) {
		if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
			return true;
		}
		return false;
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		StringBuffer text = new StringBuffer();

		int numcount = 0;		//8進数,16進数で利用

		int state = 0;
		boolean accept = false;
		while (!accept) {
			switch (state) {
				case 0:					// 初期状態
					ch = readChar();
					if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) {	// EOF
						startCol = colNo - 1;
						state = 1;
					} else if (ch >= '1' && ch <= '6') {
						startCol = colNo - 1;
						text.append(ch);
						state = 3;
					} else if (ch >= '7' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = 3;
						numcount++;
					} else if (ch == '0') {
						startCol = colNo - 1;
						text.append(ch);
						state = 11;
					} else if (ch == '/') {		//comment or DIV
						startCol = colNo - 1;
						text.append(ch);
						state = 4;
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = 8;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = 9;
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						state = 10;
					} else if (ch == '*') {		// 掛け算? 将来ポインタとしても活躍しそう
						startCol = colNo - 1;
						text.append(ch);
						state = 19;
					} else if (ch == '(') {		// (
						startCol = colNo - 1;
						text.append(ch);
						state = 20;
					} else if (ch == ')') {		// )
						startCol = colNo - 1;
						text.append(ch);
						state = 21;
					} else if (ch == '[') {		// [
						startCol = colNo - 1;
						text.append(ch);
						state = 22;
					} else if (ch == ']') {		// ]
						startCol = colNo - 1;
						text.append(ch);
						state = 23;
					} else if (isAlpha(ch)) {	//ident(識別子)
						startCol = colNo - 1;
						text.append(ch);
						state = 24;
					} else if (ch == '=') {		// '='
						startCol = colNo - 1;
						text.append(ch);
						state = 25;
					} else if (ch == ';') {		// ';'
						startCol = colNo - 1;
						text.append(ch);
						state = 26;
					} else if (ch == ',') {		// ','
						startCol = colNo - 1;
						text.append(ch);
						state = 27;
					} else {			// ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = 2;
					}
					break;
				case 1:					// EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case 2:					// ヘンな文字を読んだ。ゴミ処理
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 3:					// 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) { //isDigitは数字かどうか判断
						text.append(ch);
					} else if(isAlpha(ch)) {
						text.append(ch);
						state = 17;
					} else {
						// 数の終わり
						backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
						if (numcount < 5 && Integer.parseInt(text.toString()) < 65536) {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						} else {			//65536以上
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case 4:
					ch = readChar();
					if(ch == '/') {
						text.append(ch);
						state = 5;
					} else if(ch == '*') {
						text.append(ch);
						state = 6;
					} else {
						backChar(ch);
						state = 18;
					}
					break;
				case 5:
					ch = readChar();
					if (ch != '\r' && ch != (char) -1) {	//改行しない限りコメント行
						text.append(ch);
					} else {
						backChar(ch);
						text.setLength(0);
						state = 0;
					}
					break;
				case 6:
					ch = readChar();
					if (ch == (char) -1) {
						backChar(ch);
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						accept = true;
					} else if (ch != '*') {
						text.append(ch);
					} else {
						text.append(ch);
						state = 7;
					}
					break;
				case 7:
					ch = readChar();
					if (ch == (char) -1) {
						backChar(ch);
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						accept = true;
					} else if (ch == '*') {
						text.append(ch);
					} else 	if (ch != '/' ) {
						state = 6;
					} else {
						text.setLength(0);
						state = 0;
					}
					break;
				case 8:					// +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case 9:					// -を読んだ
					tk = new CToken(CToken.TK_SUB, lineNo, startCol, "-");
					accept = true;
					break;
				case 10:				// &を読んだ
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
					accept = true;
					break;
				case 11:				// 0を読んだ（8進数、16進数）
					ch = readChar();
					if(ch == '0') {
						text.append(ch);
						state = 12;
					} else if (ch == '1') {
						text.append(ch);
						state = 13;
					} else if (ch >= '2' && ch <= '7') {
						text.append(ch);
						state = 13;
						numcount++;		//最上位が1以外の時は0~77777のため
					} else if(ch == 'x') {		//16進数
						text.append(ch);
						state = 14;
					} else if ((ch == '8' || ch == '9') || isAlpha(ch)) {	//8進数なのでエラー
						text.append(ch);
						state = 17;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 12:				//8進数で上位に0が連続する場合、または0
					ch = readChar();
					if(ch == '0') {
						text.append(ch);
					} else if (ch == '1') {
						text.append(ch);
						state = 13;
					} else if (ch >= '2' && ch <= '7') {
						text.append(ch);
						state = 13;
						numcount++;		//最上位が1以外の時は0~77777のため桁数上限調整
					} else if ((ch == '8' || ch == '9') || isAlpha(ch)) {
						text.append(ch);
						state = 17;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 13:				//8進数
					ch = readChar();
					if(ch >= '0' && ch <= '7') {
						text.append(ch);
						numcount++;
					} else if ((ch == '8' || ch == '9') || isAlpha(ch)) {
						text.append(ch);
						state = 17;
					} else {
						backChar(ch);
						if (numcount < 6) {		// 1 ～ 177777
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						} else {				// 200000 ～
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case 14:				//16進数
					ch = readChar();
					if(ch == '0') {
						text.append(ch);
						state = 15;
					} else if((ch >= '1' && ch <= '9')
							|| (ch >= 'A' && ch <= 'F')
							|| (ch >= 'a' && ch <= 'f')) {
						text.append(ch);
						state = 16;
					} else if((ch >= 'G' && ch <= 'Z') || (ch >= 'g' && ch <= 'z')) {
						text.append(ch);
						state = 17;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 15:				//16進数で上位に0が連続する場合、または0
					ch = readChar();
					if(ch == '0') {
						text.append(ch);
					} else if((ch >= '1' && ch <= '9')
							|| (ch >= 'A' && ch <= 'F')
							|| (ch >= 'a' && ch <= 'f')) {
						text.append(ch);
						state = 16;
					} else if((ch >= 'G' && ch <= 'Z') || (ch >= 'g' && ch <= 'z')) {
						text.append(ch);
						state = 17;
					} else {			//0x0…0 = 0の場合
						backChar(ch);
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 16:				//16進数
					ch = readChar();
					if((ch >= '0' && ch <= '9')
							|| (ch >= 'A' && ch <= 'F')
							|| (ch >= 'a' && ch <= 'f')) {
						text.append(ch);
						numcount++;
					} else if((ch >= 'G' && ch <= 'Z') || (ch >= 'g' && ch <= 'z')) {
						text.append(ch);
						state = 17;
					} else {
						backChar(ch);
						if (numcount < 4) {		// 1 ～ FFFF
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						} else {				// 10000 ～
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case 17:				//n進数を超える文字が出たとき
					ch = readChar();
					if(Character.isDigit(ch) || isAlpha(ch)) {
						text.append(ch);
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 18:				// DIVのとき
					tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
					accept = true;
					break;
				case 19:				// MULのとき
					tk = new CToken(CToken.TK_MUL, lineNo, startCol, "*");
					accept = true;
					break;
				case 20:				// (を読んだ
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case 21:				// )を読んだ
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case 22:				// [を読んだ
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case 23:				// ]を読んだ
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case 24:				// identを読んだとき
					ch = readChar();
					if (Character.isDigit(ch) || isAlpha(ch)) {	//まだ英文字が続くのであれば文字列を積んでいく
						text.append(ch);
					} else {
						backChar(ch);
						String s = text.toString();
						Integer i = (Integer)rule.get(s);
						tk = new CToken(((i == null) ? CToken.TK_IDENT : i.intValue()), lineNo, startCol, s);
						accept = true;
					}
					break;
				case 25:				// = を読んだとき
					tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					accept = true;
					break;
				case 26:				// ; を読んだとき
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
				case 27:				// , を読んだとき
					tk = new CToken(CToken.TK_CANMA, lineNo, startCol, ",");
					accept = true;
					break;
			}
		}
		return tk;
	}
}
