package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroUsuarioDTO;
import br.com.stockup.enums.TipoLoja;
import br.com.stockup.model.CadastroUsuarioPendente;
import br.com.stockup.model.Loja;
import br.com.stockup.model.Usuario;
import br.com.stockup.repository.CadastroUsuarioPendenteRepository;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private CadastroUsuarioPendenteRepository cadastroUsuarioPendenteRepository;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void cadastrarDeveSalvarCadastroPendenteEEnviarCodigo() {
        // ARRANGE
        CadastroUsuarioDTO cadastroUsuarioDTO = new CadastroUsuarioDTO();
        cadastroUsuarioDTO.setNome("Proprietário Teste");
        cadastroUsuarioDTO.setEmail("proprietario@gmail.com");
        cadastroUsuarioDTO.setSenha("12345678");
        cadastroUsuarioDTO.setNomeLoja("Loja Teste");
        cadastroUsuarioDTO.setTipoLoja(TipoLoja.VAREJO);

        when(usuarioRepository.existsByEmail(cadastroUsuarioDTO.getEmail()))
                .thenReturn(false);

        when(cadastroUsuarioPendenteRepository.findByEmail(cadastroUsuarioDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(cadastroUsuarioDTO.getSenha()))
                .thenReturn("senha-criptografada");

        // ACT
        usuarioService.cadastrar(cadastroUsuarioDTO);

        // ASSERT
        ArgumentCaptor<CadastroUsuarioPendente> cadastroCaptor =
                ArgumentCaptor.forClass(CadastroUsuarioPendente.class);

        verify(cadastroUsuarioPendenteRepository)
                .save(cadastroCaptor.capture());

        CadastroUsuarioPendente cadastroSalvo = cadastroCaptor.getValue();

        assertEquals("Proprietário Teste", cadastroSalvo.getNome());
        assertEquals("proprietario@email.com", cadastroSalvo.getEmail());
        assertEquals("senha-criptografada", cadastroSalvo.getSenha());
        assertEquals("Loja Teste", cadastroSalvo.getNomeLoja());
        assertEquals(TipoLoja.VAREJO, cadastroSalvo.getTipoLoja());
        assertEquals(6, cadastroSalvo.getCodigoVerificacao().length());

        verify(emailServiceImpl).enviarCodigoVerificacao(
                eq("proprietario@email.com"),
                eq(cadastroSalvo.getCodigoVerificacao())
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(lojaRepository, never()).save(any(Loja.class));
    }

    @Test
    void cadastrarComEmailJaCadastradoDeveLancarExcecao() {
        //ARRANGE
        CadastroUsuarioDTO cadastroUsuarioDTO = new CadastroUsuarioDTO();
        cadastroUsuarioDTO.setEmail("emailjaexiste@gmail.com");

        when(usuarioRepository.existsByEmail(cadastroUsuarioDTO.getEmail())).thenReturn(true);

        //ACT
        RuntimeException excecao = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cadastrar(cadastroUsuarioDTO)
        );

        //ASSERT
        assertEquals("Email já cadastrado", excecao.getMessage());

        verify(usuarioRepository).existsByEmail(cadastroUsuarioDTO.getEmail());
        verify(cadastroUsuarioPendenteRepository, never()).save(any(CadastroUsuarioPendente.class));
        verify(emailServiceImpl, never()).enviarCodigoVerificacao(anyString(), anyString());
    }
}