package br.com.stockup.model;

import br.com.stockup.enums.Tamanho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estoque_tamanho")
@Getter
@Setter

public class EstoqueTamanho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estoque_produto_id")
    private EstoqueProduto estoqueProduto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tamanho tamanho;

    @Column(nullable = false)
    private Integer quantidade;
}
