// 識別子に関する意味解析テスト（構文解析のテストは、各自が行うこと）
//
// ident.javaのsemanticCheckメソッドのところは、こうなっているはず
//
// public void semanticCheck(CParseContext pcx) throws FatalErrorException {
// 	this.setCType(CType.getCType(CType.XXXXX));
// 	this.setConstant(YYYYY);
// }

// (1) XXXXX を整数型 T_int に、YYYYYをfalseにして
//a	// 正当（生成コードが正しいかどうかも確認）
//*a	// 不当
//a[3]	// 不当
//
//// (2) XXXXX をポインタ型 T_pint に、YYYYYをfalseにして
//a	// 正当
//*a	// 正当（生成コードが正しいかどうかも確認）
//a[3]	// 不当（Ｃでは正当だが、この実験では不当にすること）
//
//// (3) XXXXX を配列型 T_array（人によってこの名前は異なる）に、YYYYYをfalseにして
////  ＊＊＊＊＊＊＊＊＊＊＊＊＊ 2018/10/26 変更 ＊＊＊＊＊＊＊＊＊＊
//a	// 不当（10/26より前は正当となっていた）
//*a	// 不当（Ｃでは正当だが、この実験では不当にすること）
//a[3]	// 正当（生成コードが正しいかどうかも確認）
//a[a[3]] // 正当（11/2追加）
//a[&3]	// 不当（[]内は整数型でなければならない）
//
//// (4) XXXXX をポインタ配列型 T_parray（人によってこの名前は異なる）に、YYYYYをfalseにして
////  ＊＊＊＊＊＊＊＊＊＊＊＊＊ 2018/10/26 変更 ＊＊＊＊＊＊＊＊＊＊
//a	// 不当（10/26より前は正当となっていた）
//*a	// 不当（Ｃでは正当だが、この実験では不当にすること）
//a[3]	// 正当
//a[3]+a[3] // 不当（11/2追加）
//*a[3]	// 正当（生成コードが正しいかどうかも確認）
//a[&3]	// 不当（[]内は整数型でなければならない）
//
//// (5)
//&*var	// 不当