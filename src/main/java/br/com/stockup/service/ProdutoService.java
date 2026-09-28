package br.com.stockup.service;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.dto.response.ProdutoResponseDTO;
import br.com.stockup.enums.StatusProduto;

import java.util.List;

public interface ProdutoService {

    void cadastrarProduto(CadastroProdutoDTO cadastroProdutoDTO);

    void editar(Long id, CadastroProdutoDTO cadastroProdutoDTO);

    void excluir(Long id);

    List<ProdutoResponseDTO> buscar(String termo);

    List<ProdutoResponseDTO> listarProdutos(StatusProduto status);
}
