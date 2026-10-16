package br.com.stockup.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroItemVendaVarejoDTO {
    @NotNull(message = "O estoque por tamanho é obrigatório.")
    private Long estoqueTamanhoId;

    @NotNull(message = "A quantidade de pares é obrigatória.")
    @Positive(message = "A quantidade de pares deve ser maior que zero.")
    private Integer quantidadePares;
}
