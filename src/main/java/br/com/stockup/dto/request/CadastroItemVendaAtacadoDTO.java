package br.com.stockup.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroItemVendaAtacadoDTO {
    @NotNull(message = "O estoque do produto é obrigatório.")
    private Long estoqueProdutoId;

    @NotNull(message = "A quantidade de fichas é obrigatória.")
    @Positive(message = "A quantidade de fichas deve ser maior que zero.")
    private Integer quantidadeFichas;
}
