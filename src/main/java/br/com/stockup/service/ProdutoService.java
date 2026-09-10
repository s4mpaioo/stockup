package br.com.stockup.service;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.model.Produto;

import java.util.List;

public interface ProdutoService {

    void cadastrarProduto(CadastroProdutoDTO cadastroProdutoDTO);

    void editar(Long id, CadastroProdutoDTO cadastroProdutoDTO);

    void excluir(Long id);

    Produto buscarPorReferencia(String referencia);

    List<Produto> buscarPorNome(String nome);

    List<Produto> listarTodos();
}
