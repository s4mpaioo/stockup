package br.com.stockup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "item_venda_atacado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ItemVendaAtacado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantidadeFichas;

    @Column(nullable = false)
    private BigDecimal valorFicha;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "estoque_produto_id", nullable = false)
    private EstoqueProduto estoqueProduto;
}
