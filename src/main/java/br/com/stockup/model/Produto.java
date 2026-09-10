package br.com.stockup.model;

import br.com.stockup.enums.ModeloProduto;
import br.com.stockup.enums.StatusProduto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome do produto não pode ser vazio")
    private String nome;

    @Column(nullable = false)
    @NotBlank(message = "A marca do produto não pode ser vazio")
    private String marca;

    @Column(nullable = false)
    @NotBlank(message = "A referência do produto não pode ser vazio")
    private String referencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeloProduto modelo;

    @Enumerated(EnumType.STRING)
    private StatusProduto status = StatusProduto.EM_ESTOQUE;

    @Column(nullable = false)
    private boolean excluido = false;

    @Column(nullable = false)
    @NotBlank(message = "A cor do produto não pode ser vazio")
    private String cor;

    @Column(nullable = false)
    @PositiveOrZero(message = "O estoque mínimo não pode ser negativo")
    private Integer estoqueMinimo = 0;

    @Column(length = 500)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "loja_id")
    private Loja loja;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<EstoqueProduto> estoques = new ArrayList<>();
}
