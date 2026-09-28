package br.com.stockup.dto.request;

import br.com.stockup.enums.Tamanho;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroEstoqueTamanhoDTO {

    @NotNull
    private Tamanho tamanho;

    @NotNull
    @PositiveOrZero
    private Integer quantidade;
}