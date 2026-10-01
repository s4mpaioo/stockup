package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.enums.ModeloProduto;
import br.com.stockup.model.Loja;
import br.com.stockup.model.Produto;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.repository.ProdutoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

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

    @Captor
    private ArgumentCaptor<Produto> produtoCaptor;

    @Test
    void validarCadastroProduto() {
        //ARRANGE - PREPARAR
        Loja loja = new Loja();
        Long lojaId = 1L;

        CadastroProdutoDTO cadastroProduto = new CadastroProdutoDTO();

        cadastroProduto.setReferencia("123");
        cadastroProduto.setNome("Molekinha");
        cadastroProduto.setMarca("Moleca");
        cadastroProduto.setModelo(ModeloProduto.RASTEIRINHA);
        cadastroProduto.setCor("Nude");
        cadastroProduto.setEstoqueMinimo(2);
        cadastroProduto.setLojaId(lojaId);

        when(lojaRepository.findById(lojaId)).thenReturn(Optional.of(loja));

        when(produtoRepository.existsByReferenciaAndCorAndExcluidoFalse("123", "Nude")).thenReturn(false);

        //ACT - EXECUTAR
        produtoServiceImpl.cadastrarProduto(cadastroProduto);

        //ASSERT - VERIFICAR
        verify(produtoRepository, times(1)).save(produtoCaptor.capture());

        Produto produtoSalvo = produtoCaptor.getValue();

        Assertions.assertNotNull(produtoSalvo);
        Assertions.assertEquals("123", produtoSalvo.getReferencia());
        Assertions.assertEquals("Molekinha", produtoSalvo.getNome());
        Assertions.assertEquals("Moleca", produtoSalvo.getMarca());
        Assertions.assertEquals(
                ModeloProduto.RASTEIRINHA,
                produtoSalvo.getModelo()
        );
        Assertions.assertEquals("Nude", produtoSalvo.getCor());
        Assertions.assertEquals(2, produtoSalvo.getEstoqueMinimo());
        Assertions.assertEquals(loja, produtoSalvo.getLoja());
    }

    @Test
    void editarProdutoExistenteAtualizaTodosOsDados() {
        //ARRANGE
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setReferencia("123");
        produto.setNome("Duramo AC2");
        produto.setMarca("Adidas");
        produto.setModelo(ModeloProduto.TENIS);
        produto.setCor("Preto");
        produto.setDescricao("Tênis de corrida");

        when(produtoRepository.findByIdAndExcluidoFalse(produto.getId())).thenReturn(Optional.of(produto));

        CadastroProdutoDTO produtoEditado = new CadastroProdutoDTO();
        produtoEditado.setReferencia("1953");
        produtoEditado.setNome("Nike Court Vision");
        produtoEditado.setMarca("Nike");
        produtoEditado.setModelo(ModeloProduto.TENIS);
        produtoEditado.setCor("Branco");
        produto.setDescricao("Tênis para o dia a dia");

        when(produtoRepository.findByReferenciaAndCorAndExcluidoFalse(
                produtoEditado.getReferencia(),
                produtoEditado.getCor()
        )).thenReturn(Optional.empty());

        //ACT
        produtoServiceImpl.editar(produto.getId(), produtoEditado);

        //ASSERT
        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);

        verify(produtoRepository).save(produtoCaptor.capture());

        Produto produtoSalvo = produtoCaptor.getValue();

        Assertions.assertEquals(produtoEditado.getReferencia(), produtoSalvo.getReferencia());
        Assertions.assertEquals(produtoEditado.getNome(), produtoSalvo.getNome());
        Assertions.assertEquals(produtoEditado.getMarca(), produtoSalvo.getMarca());
        Assertions.assertEquals(produtoEditado.getModelo(), produtoSalvo.getModelo());
        Assertions.assertEquals(produtoEditado.getCor(), produtoSalvo.getCor());
        Assertions.assertEquals(produtoEditado.getDescricao(), produtoSalvo.getDescricao());
    }
}