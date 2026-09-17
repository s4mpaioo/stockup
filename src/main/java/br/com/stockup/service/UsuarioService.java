package br.com.stockup.service;

import br.com.stockup.dto.request.*;
import br.com.stockup.dto.response.LoginResponseDTO;

public interface UsuarioService {

    void cadastrar(CadastroUsuarioDTO cadastroUsuarioDTO);

    public LoginResponseDTO login(LoginUsuarioDTO loginUsuarioDTO);

    void redefinirSenha(RedefinirSenhaDTO redefinirSenhaDTO);

    void validarCodigo(ValidarCodigoDTO validarCodigoDTO);

    void novaSenha(NovaSenhaDTO novaSenhaDTO);
}
