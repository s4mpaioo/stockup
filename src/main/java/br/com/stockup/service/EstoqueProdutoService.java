package br.com.stockup.service;

import br.com.stockup.dto.request.CadastroEstoqueAtacadoDTO;
import br.com.stockup.dto.request.CadastroEstoqueVarejoDTO;
import br.com.stockup.dto.request.EditarEstoqueDTO;

public interface EstoqueProdutoService {

    void cadastrarAtacado(CadastroEstoqueAtacadoDTO cadastroEstoqueAtacadoDTO);

    void cadastrarVarejo(CadastroEstoqueVarejoDTO cadastroEstoqueVarejoDTO);

    void editarAtacado(EditarEstoqueDTO dto);

    void editarVarejo(EditarEstoqueDTO dto);
}