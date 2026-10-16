package br.com.stockup.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroClienteDTO {
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    private String telefone;

    @NotNull(message = "A loja é obrigatória.")
    private Long lojaId;
}
