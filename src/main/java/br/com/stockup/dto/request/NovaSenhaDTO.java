package br.com.stockup.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovaSenhaDTO {
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Informe um email válido.")
    private String email;

    @NotBlank(message = "O código é obrigatório.")
    @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres.")
    private String codigo;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, message = "A senha precisa ter pelo menos 8 caracteres.")
    private String novaSenha;

    @NotBlank(message = "A confirmação da senha é obrigatória.")
    @Size(min = 8, message = "A senha precisa ter pelo menos 8 caracteres.")
    private String confirmarSenha;
}
