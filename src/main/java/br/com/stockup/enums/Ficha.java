package br.com.stockup.enums;

public enum Ficha {
    F6(6),
    F8(8),
    F10(10),
    F12(12),
    F15(15),
    F18(18),
    F20(20),
    F24(24);

    private final Integer quantidadePares;

    Ficha(Integer quantidadePares) {
        this.quantidadePares = quantidadePares;
    }
    public Integer getQuantidadePares() {
        return quantidadePares;
    }
    public String getDescricao() {
        return quantidadePares + " pares";
    }
}
