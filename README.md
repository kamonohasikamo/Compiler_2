# Compiler_2

C言語っぽいものを読み込んでアセンブラ言語を吐き出すコンパイラ

Javaで作ります

目標は全課題の実施
1から14まで頑張る。

  - 必須実験
実験0：加算式の計算コンパイラ　miniC　ver.00
　ー加算のできるコンパイラ
　1．構文定義(LL(1)文法)
 program        := expression EOF
 expression     := term { expressionAdd }
 expressionAdd  := PLUS term
 term           := factor
 factor         := number
 number         := NUM
 
実験1：減算の導入　miniC　ver.00

実験2：アドレス値の導入　miniC　ver.00

実験3：四則演算と符号の導入　miniC　ver.00

実験4：変数参照の導入　miniC　ver.00

実験5：変数への代入の導入　miniC　ver.00

実験6:変数宣言の導入　miniC　ver.00

実験7:条件判定の導入　miniC　ver.00

実験8:if、while文の導入　miniC　ver.00

 - オプション実験
  - 関数について
実験9:局所変数が使用可能な miniC

実験10:引数なし関数が使用可能な　miniC

実験11:引数付き関数が使用可能な　miniC

実験12:複雑な条件判定が可能な　miniC

実験13:エラー回復が可能な　miniC

実験14:多少の最適化が可能な　miniC
