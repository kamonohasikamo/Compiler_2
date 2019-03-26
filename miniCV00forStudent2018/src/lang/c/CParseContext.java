package lang.c;

import lang.IOContext;
import lang.ParseContext;

public class CParseContext extends ParseContext {
	public CParseContext(IOContext ioCtx,  CTokenizer tknz) {
		super(ioCtx, tknz);
	}
	private CSymbolTable st = new CSymbolTable();

	@Override
	public CTokenizer getTokenizer()		{ return (CTokenizer) super.getTokenizer(); }

	private int seqNo = 0;
	private int brcNo = 0;

	public int getSeqId() { return ++seqNo; }
	public int getBrcId() { return ++brcNo; }

	public CSymbolTable getTable()			{ return st;}
}
