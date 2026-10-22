package br.com.stockup.model;

import br.com.stockup.enums.TipoLoja;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cadastro_usuario_pendente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CadastroUsuarioPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String nomeLoja;

    @Enumerated(EnumType.STRING)
    private TipoLoja tipoLoja;

    private String codigoVerificacao;

    private LocalDateTime expiracaoCodigoVerificacao;
}
