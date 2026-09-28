package br.com.stockup.service.impl;

import br.com.stockup.dto.request.*;
import br.com.stockup.dto.response.LoginResponseDTO;
import br.com.stockup.enums.PerfilUsuario;
import br.com.stockup.model.Loja;
import br.com.stockup.model.Usuario;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.repository.UsuarioRepository;
import br.com.stockup.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Random;
import java.time.LocalDateTime;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final LojaRepository lojaRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, LojaRepository lojaRepository, EmailServiceImpl emailServiceImpl, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.lojaRepository = lojaRepository;
        this.emailServiceImpl = emailServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void cadastrar(CadastroUsuarioDTO cadastroUsuarioDTO) {
        if(usuarioRepository.existsByEmail(cadastroUsuarioDTO.getEmail())){
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = criarUsuario(cadastroUsuarioDTO);

        usuarioRepository.save(usuario);

        Loja loja = criarLoja(cadastroUsuarioDTO, usuario);

        lojaRepository.save(loja);
    }

    private Usuario criarUsuario(CadastroUsuarioDTO cadastroUsuarioDTO) {
        Usuario usuario = new Usuario();

        usuario.setNome(cadastroUsuarioDTO.getNome());
        usuario.setEmail(cadastroUsuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(cadastroUsuarioDTO.getSenha()));
        usuario.setPerfil(PerfilUsuario.PROPRIETARIO);

        return usuario;
    }

    private Loja criarLoja(CadastroUsuarioDTO cadastroUsuarioDTO, Usuario usuario) {
        Loja loja = new Loja();

        loja.setNome(cadastroUsuarioDTO.getNomeLoja());
        loja.setTipo(cadastroUsuarioDTO.getTipoLoja());
        loja.setUsuario(usuario);

        return loja;
    }

    public LoginResponseDTO login(LoginUsuarioDTO loginUsuarioDTO) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginUsuarioDTO.getEmail());

        if (usuario.isEmpty()) {
            throw new RuntimeException("Email inválido.");
        }

        Usuario usuarioEncontrado = usuario.get();

        if (!passwordEncoder.matches(loginUsuarioDTO.getSenha(), usuarioEncontrado.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos.");
        }

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setId(usuarioEncontrado.getId());
        loginResponseDTO.setNome(usuarioEncontrado.getNome());
        loginResponseDTO.setEmail(usuarioEncontrado.getEmail());

        return loginResponseDTO;
    }

    public void redefinirSenha(RedefinirSenhaDTO redefinirSenhaDTO) {
        Optional <Usuario> usuario = usuarioRepository.findByEmail(redefinirSenhaDTO.getEmail());
        if(usuario.isEmpty()) {
            throw new RuntimeException("Email não encontrado.");
        }

        Usuario usuarioEncontrado = usuario.get();
        Random random = new Random();
        int codigo = random.nextInt(900000) + 100000;

        usuarioEncontrado.setCodigoRecuperacao(String.valueOf(codigo)); //converte de inteiro para string
        usuarioEncontrado.setExpiracaoCodigo(LocalDateTime.now().plusMinutes(10));

        usuarioRepository.save(usuarioEncontrado);

        emailServiceImpl.enviarCodigoRecuperacao(usuarioEncontrado.getEmail(), usuarioEncontrado.getCodigoRecuperacao());
    }

    public void validarCodigo(ValidarCodigoDTO validarCodigoDTO) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(validarCodigoDTO.getEmail());
        if (usuario.isEmpty()) {
            throw new RuntimeException("Email não encontrado.");
        }

        Usuario usuarioEncontrado = usuario.get();

        if (usuarioEncontrado.getCodigoRecuperacao() == null || usuarioEncontrado.getExpiracaoCodigo() == null) {
            throw new RuntimeException("Nenhum código de recuperação foi solicitado para este email.");
        }

        if (LocalDateTime.now().isAfter(usuarioEncontrado.getExpiracaoCodigo())) {
            throw new RuntimeException("Código expirado.");
        }

        if (!usuarioEncontrado.getCodigoRecuperacao().equals(validarCodigoDTO.getCodigo())) {
            throw new RuntimeException("Código inválido.");
        }
    }

    public void novaSenha(NovaSenhaDTO novaSenhaDTO) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(novaSenhaDTO.getEmail());
        if (usuario.isEmpty()) {
            throw new RuntimeException("Email não encontrado.");
        }

        Usuario usuarioEncontrado = usuario.get();

        if (usuarioEncontrado.getCodigoRecuperacao() == null || usuarioEncontrado.getExpiracaoCodigo() == null) {
            throw new RuntimeException("Nenhum código de recuperação foi solicitado para este email.");
        }

        if (LocalDateTime.now().isAfter(usuarioEncontrado.getExpiracaoCodigo())) {
            throw new RuntimeException("Código expirado.");
        }

        if (!usuarioEncontrado.getCodigoRecuperacao().equals(novaSenhaDTO.getCodigo())) {
            throw new RuntimeException("Código inválido.");
        }

        if (!novaSenhaDTO.getNovaSenha().equals(novaSenhaDTO.getConfirmarSenha())) {
            throw new RuntimeException("As senhas não coincidem.");
        }

        // Atualiza a senha com hash BCrypt
        usuarioEncontrado.setSenha(passwordEncoder.encode(novaSenhaDTO.getNovaSenha()));

        // Invalida o código de recuperação
        usuarioEncontrado.setCodigoRecuperacao(null);
        usuarioEncontrado.setExpiracaoCodigo(null);

        usuarioRepository.save(usuarioEncontrado);
    }
}