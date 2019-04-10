package lang.c;

import java.util.ArrayList;
import java.util.List;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
	private CType type; // この識別子に対して宣言された型
	private int size; // メモリ上に確保すべきワード数
	private boolean constp; // 定数宣言か？
	private boolean isGlobal; // 大域変数か？
	private int address; // 割り当て番地

	private List<CType> atlist = new ArrayList<CType>();

	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isGlobal, int addr) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isGlobal = isGlobal;
		this.address = addr;
	}
	public String toExplainString() { // このエントリに関する情報を作り出す。記号表全体を出力するときに使う。
		return type.toString() + ", " + size + ", " + (constp ? "定数" : "変数")
				+ ", " + isGlobal + ", " + address;
	}
	public CType getType() { return type; }
	public int getSize() { return size; }
	public boolean getConstp() { return constp; }
	public boolean getIsGlobal() { return isGlobal; }
	public int getAddress() { return address; }

	public List<CType> getlist() {
		return atlist;
	}

	public void setlist(List<CType> list) {
		atlist = list;
	}
}
