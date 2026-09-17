package br.com.stockup.model;

import br.com.stockup.enums.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    @Column(nullable = false, unique = true) //Não pode existir dois usuários com o mesmo email.
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotBlank
    @JsonIgnore
    private String senha;

    private String codigoRecuperacao;

    private LocalDateTime expiracaoCodigo;

    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfil;

    @OneToOne(mappedBy = "usuario")
    @JsonIgnore
    private Loja loja;
}