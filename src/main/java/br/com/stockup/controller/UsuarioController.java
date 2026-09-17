package br.com.stockup.controller;

import br.com.stockup.dto.request.*;
import br.com.stockup.dto.response.LoginResponseDTO;
import br.com.stockup.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public void cadastrar(@Valid @RequestBody CadastroUsuarioDTO cadastroUsuarioDTO) {
        usuarioService.cadastrar(cadastroUsuarioDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginUsuarioDTO loginUsuarioDTO) {
        LoginResponseDTO response = usuarioService.login(loginUsuarioDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso.");
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO redefinirSenhaDTO) {
        usuarioService.redefinirSenha(redefinirSenhaDTO);
        return ResponseEntity.ok("O código foi enviado para seu email.");
    }

    @PostMapping("/validar-codigo")
    public ResponseEntity<String> validarCodigo(@Valid @RequestBody ValidarCodigoDTO validarCodigo) {
        usuarioService.validarCodigo(validarCodigo);
        return ResponseEntity.ok("Código válido.");
    }

    @PostMapping("/nova-senha")
    public ResponseEntity<String> novaSenha(@Valid @RequestBody NovaSenhaDTO novaSenhaDTO) {
        usuarioService.novaSenha(novaSenhaDTO);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}
