package br.com.stockup.dto.request;

import br.com.stockup.enums.Ficha;
import br.com.stockup.enums.Tamanho;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EditarEstoqueDTO {
    @NotNull(message = "O produto é obrigatório.")
    private Long produtoId;

    @NotNull(message = "O preço de custo é obrigatório.")
    @Positive(message = "O preço de custo deve ser maior que zero.")
    private BigDecimal precoCusto;

    @NotNull(message = "O preço de venda é obrigatório.")
    @Positive(message = "O preço de venda deve ser maior que zero.")
    private BigDecimal precoVenda;

    private Ficha ficha;

    @PositiveOrZero(message = "A quantidade de fichas não pode ser negativa.")
    private Integer quantidadeFichas;

    private Tamanho tamanho;

    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    private Integer quantidade;
}
