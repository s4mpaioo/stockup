package br.com.stockup.dto.request;

import br.com.stockup.enums.FormaPagamento;
import br.com.stockup.enums.PeriodicidadePagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CadastroVendaDTO {
    private Long clienteId;

    @NotNull(message = "A loja é obrigatória.")
    private Long lojaId;

    @NotNull(message = "A forma de pagamento é obrigatória.")
    private FormaPagamento formaPagamento;

    @Min(value = 1, message = "A quantidade de parcelas deve ser no mínimo 1.")
    private Integer quantidadeParcelas;

    private PeriodicidadePagamento periodicidade;

    @PositiveOrZero(message = "O desconto não pode ser negativo.")
    private BigDecimal desconto;

    @Valid
    private List<CadastroItemVendaVarejoDTO> itensVarejo;

    @Valid
    private List<CadastroItemVendaAtacadoDTO> itensAtacado;
}