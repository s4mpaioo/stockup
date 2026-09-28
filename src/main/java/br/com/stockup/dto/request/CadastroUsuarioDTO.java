package br.com.stockup.dto.request;

import br.com.stockup.enums.TipoLoja;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CadastroUsuarioDTO {
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Informe um email válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String senha;

    @NotBlank(message = "O nome da loja é obrigatório.")
    @Size(max = 100, message = "O nome da loja deve possuir no máximo 100 caracteres.")
    private String nomeLoja;

    @NotNull(message = "O tipo da loja é obrigatório.")
    private TipoLoja tipoLoja;
}
