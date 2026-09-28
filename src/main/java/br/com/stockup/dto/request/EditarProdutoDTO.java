package br.com.stockup.dto.request;

import br.com.stockup.enums.ModeloProduto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EditarProdutoDTO {
    @NotBlank(message = "A referência é obrigatória.")
    private String referencia;

    @NotBlank(message = "O nome do produto é obrigatório.")
    private String nome;

    @NotBlank(message = "A marca é obrigatória.")
    private String marca;

    @NotNull(message = "O modelo é obrigatório.")
    private ModeloProduto modelo;

    @NotBlank(message = "A cor é obrigatória.")
    private String cor;

    @NotNull(message = "O estoque mínimo é obrigatório.")
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo.")
    private Integer estoqueMinimo;

    private String descricao;
}
