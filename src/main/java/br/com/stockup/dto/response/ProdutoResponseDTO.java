package br.com.stockup.dto.response;

import br.com.stockup.enums.ModeloProduto;
import br.com.stockup.enums.StatusProduto;
import br.com.stockup.model.Produto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProdutoResponseDTO {
    private Long id;

    private String nome;

    private String marca;

    private String referencia;

    private String cor;

    private ModeloProduto modelo;

    private StatusProduto status;

    private Integer estoqueMinimo;

    private String descricao;

    public ProdutoResponseDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.marca = produto.getMarca();
        this.referencia = produto.getReferencia();
        this.cor = produto.getCor();
        this.modelo = produto.getModelo();
        this.status = produto.getStatus();
        this.estoqueMinimo = produto.getEstoqueMinimo();
        this.descricao = produto.getDescricao();
    }
}

