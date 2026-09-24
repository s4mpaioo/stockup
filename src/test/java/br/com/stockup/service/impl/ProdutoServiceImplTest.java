package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.enums.ModeloProduto;
import br.com.stockup.model.Loja;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ProdutoServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private LojaRepository lojaRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoServiceImpl;

    @Test
    void validarCadastroProduto() {
    //ARRANGE - PREPARAR
        Loja loja = new Loja();
        Long lojaId = 1L;

        CadastroProdutoDTO cadastroProdutoDTO = new CadastroProdutoDTO();

        cadastroProdutoDTO.setReferencia("123");
        cadastroProdutoDTO.setNome("Molekinha");
        cadastroProdutoDTO.setMarca("Moleca");
        cadastroProdutoDTO.setModelo(ModeloProduto.RASTEIRINHA);
        cadastroProdutoDTO.setCor("Nude");
        cadastroProdutoDTO.setEstoqueMinimo(2);
        cadastroProdutoDTO.setLojaId(lojaId);

        when(lojaRepository.findById(lojaId)).thenReturn(Optional.of(loja));

        when(produtoRepository.existsByReferenciaAndCorAndExcluidoFalse(cadastroProdutoDTO.getReferencia(), cadastroProdutoDTO.getCor())).thenReturn(false);
        //ACT - EXECUTAR
        produtoServiceImpl.cadastrarProduto(cadastroProdutoDTO);
        //ASSERT - VERIFICAR
        verify(produtoRepository, times(1)).save((any()));

    }

}