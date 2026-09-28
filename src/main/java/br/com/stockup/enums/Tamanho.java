package br.com.stockup.enums;

public enum Tamanho {
    N18(18),
    N19(19),
    N20(20),
    N21(21),
    N22(22),
    N23(23),
    N24(24),
    N25(25),
    N26(26),
    N27(27),
    N28(28),
    N29(29),
    N30(30),
    N31(31),
    N32(32),
    N33(33),
    N34(34),
    N35(35),
    N36(36),
    N37(37),
    N38(38),
    N39(39),
    N40(40),
    N41(41),
    N42(42),
    N43(43),
    N44(44);

    private final Integer numero;

    Tamanho(Integer numero) {
        this.numero = numero;
    }

    public Integer getNumero() {
        return numero;
    }
}
