package br.com.stockup.model;

import br.com.stockup.enums.PeriodicidadePagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "divida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Divida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private Integer quantidadeParcelas;

    @Enumerated(EnumType.STRING)
    private PeriodicidadePagamento periodicidade;

    @Column(nullable = false)
    private Boolean quitada;

    @OneToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;

    @OneToMany(mappedBy = "divida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcela> parcelas = new ArrayList<>(); //cria lista vazia
}
