package br.com.stockup.model;

import br.com.stockup.enums.TipoLoja;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="loja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Loja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoLoja tipo;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "loja")
    private List<Produto> produtos;

    @OneToMany(mappedBy = "loja")
    private List<Cliente> clientes;

    @OneToMany(mappedBy = "loja")
    private List<Venda> vendas;
}