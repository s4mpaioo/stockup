package br.com.stockup.model;

import br.com.stockup.enums.Tamanho;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tamanho_estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TamanhoEstoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Tamanho tamanho;

    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "estoque_produto_id")
    private EstoqueProduto estoqueProduto;
}
