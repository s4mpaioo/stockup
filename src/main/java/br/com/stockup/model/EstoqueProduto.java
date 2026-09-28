package br.com.stockup.model;

import br.com.stockup.enums.Ficha;
import br.com.stockup.enums.TipoEstoque;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estoque_produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EstoqueProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal precoCusto;

    @Column(nullable = false)
    private BigDecimal precoVenda;

    @Enumerated(EnumType.STRING)
    private TipoEstoque tipoEstoque;

    @Enumerated(EnumType.STRING)
    private Ficha ficha;

    @Column(nullable = false)
    @PositiveOrZero(message = "A quantidade de fichas não pode ser negativa")
    private Integer quantidadeFichas;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @OneToMany(mappedBy = "estoqueProduto", cascade = CascadeType.ALL)
    private List<EstoqueTamanho> quantidadePorTamanho = new ArrayList<>();
}
