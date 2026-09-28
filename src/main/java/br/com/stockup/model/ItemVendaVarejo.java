package br.com.stockup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "item_venda_varejo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ItemVendaVarejo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantidadePares;

    @Column(nullable = false)
    private BigDecimal valorPar;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "estoque_tamanho_id")
    private TamanhoEstoque tamanhoEstoque;
}