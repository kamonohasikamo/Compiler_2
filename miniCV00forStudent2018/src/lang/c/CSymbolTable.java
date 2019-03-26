package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
	private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
		@Override
		public CSymbolTableEntry register(String name, CSymbolTableEntry e) { return put(name, e); }
		@Override
		public CSymbolTableEntry search(String name) { return get(name); }

		private int addrNo = 0;
		public int getAddrNo() { return addrNo; }
		public void setAddrNo(int num) { addrNo = num; }
	}
	private OneSymbolTable global = new OneSymbolTable();	// 大域変数用
	private OneSymbolTable local; 							// 局所変数用
	// private SymbolTable<CSymbolTableEntry> global; // こう書いても、もちろん OK
	// private SymbolTable<CSymbolTableEntry> local; // （同上）

	public CSymbolTableEntry registerTable(String name, CType type, int size, boolean constp) {
		if (local != null) {
			if (local.search(name) != null) {
				return null;
			} else {
				int num = local.getAddrNo();
				if (constp) {
					local.setAddrNo(num+1);
				} else {
					local.setAddrNo(num+size);
				}
				CSymbolTableEntry e = new CSymbolTableEntry(type, size, constp, false, num);
				local.register(name, e);
				return e;
			}
		} else {
			if (global.search(name) != null) {
				return null;
			} else {
				CSymbolTableEntry e = new CSymbolTableEntry(type, size, constp, true, 0);
				global.register(name, e);
				return e;
			}
		}
	}

	public CSymbolTableEntry searchTable(String name) {
		CSymbolTableEntry e = null;
		if (local != null) {
			e = local.search(name);
		}
		if (e != null) {	//まずlocal
			return e;
		} else {
			e = global.search(name);
			if (e != null) {	//次にglobal
				return e;
			} else {
				return null;	//見つからない
			}
		}
	}

	public void setupLocalSymbolTable() {
		this.local = new OneSymbolTable();
	}

	public void deleteLocalSymbolTable() {
		this.local = null;
	}

	public int getAddrsize() {
		if (local != null) {
			return local.getAddrNo();
		}
		return 0;	//こないはず
	}

	public void showTable() {
		local.show();
	}
}