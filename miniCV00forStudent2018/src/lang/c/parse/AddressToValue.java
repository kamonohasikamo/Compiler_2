package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

import java.io.PrintStream;

public class AddressToValue extends CParseRule {
    private CParseRule primary;
    public AddressToValue(CParseContext pcx) {

    }
    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }
    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (Primary.isFirst(tk)) {
            primary = new Primary(pcx);
            primary.parse(pcx);
        }
    }
    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
            setCType(primary.getCType());
            setConstant(primary.isConstant());
        }
    }
    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; AddresstToValue starts");
        if (primary != null) { primary.codeGen(pcx); }
        o.println(";;; AddresstToValue completes");
    }
}
