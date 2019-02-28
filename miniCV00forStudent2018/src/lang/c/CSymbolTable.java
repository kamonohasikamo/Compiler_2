package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
    private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
        @Override
        public CSymbolTableEntry register(String name, CSymbolTableEntry e) { return put(name, e); }
        @Override
        public CSymbolTableEntry search(String name) { return get(name); }
    }
    private OneSymbolTable global = new OneSymbolTable(); // 大域変数用
    @SuppressWarnings("unused")
    private OneSymbolTable local = new OneSymbolTable(); // 局所変数用
    // private SymbolTable<CSymbolTableEntry> global; // こう書いても、もちろん OK
    // private SymbolTable<CSymbolTableEntry> local; // （同上）

    public boolean registerGlobal(String name, CType type, int size, boolean constp, boolean isGlobal, int addr) {
        if (global.search(name) != null) {
            return false;
        } else {
            CSymbolTableEntry e = new CSymbolTableEntry(type, size, constp, isGlobal, addr);
            global.register(name, e);
            return true;
        }
    }

    public CSymbolTableEntry searchGlobal(String name) {
        CSymbolTableEntry e;
        e = global.search(name);
        if (e != null) {
            return e;
        }
        return null;
    }
}