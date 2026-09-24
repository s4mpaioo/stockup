package br.com.stockup.dto.request;

import br.com.stockup.enums.Ficha;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CadastroEstoqueAtacadoDTO {
    @NotNull
    private Long produtoId;

    @NotNull
    private Ficha ficha;

    @NotNull
    @Positive
    private Integer quantidadeFichas;

    @NotNull
    @Positive
    private BigDecimal precoCusto;

    @NotNull
    @Positive
    private BigDecimal precoVenda;
}
