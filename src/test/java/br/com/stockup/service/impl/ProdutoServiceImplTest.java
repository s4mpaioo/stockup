package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.dto.request.EditarProdutoDTO;
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
    void cadastroLancaExcecaoParaCorAndReferenciaJaCadastrado() {
        //ARRANGE
        Loja loja = new Loja();
        loja.setId(10L);
        CadastroProdutoDTO cadastroProduto = new CadastroProdutoDTO();
        cadastroProduto.setReferencia("2332");
        cadastroProduto.setCor("Preto");
        cadastroProduto.setLojaId(loja.getId());

        when(lojaRepository.findById(loja.getId())).thenReturn(Optional.of(loja));
        when(produtoRepository.existsByReferenciaAndCorAndExcluidoFalse(cadastroProduto.getReferencia(),  cadastroProduto.getCor())).thenReturn(true);

        //ACT
        RuntimeException excecao = Assertions.assertThrows(
                RuntimeException.class,
                () -> produtoServiceImpl.cadastrarProduto(cadastroProduto)
        );
        //ASSERT
        Assertions.assertEquals("Já existe um produto com esta referência e cor.", excecao.getMessage());

        verify(produtoRepository, never()).save(any(Produto.class));
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
        produto.setEstoqueMinimo(2);
        produto.setDescricao("Tênis de corrida");

        when(produtoRepository.findByIdAndExcluidoFalse(produto.getId())).thenReturn(Optional.of(produto));

        EditarProdutoDTO produtoEditado = new EditarProdutoDTO();
        produtoEditado.setReferencia("1953");
        produtoEditado.setNome("Nike Court Vision");
        produtoEditado.setMarca("Nike");
        produtoEditado.setModelo(ModeloProduto.TENIS);
        produtoEditado.setCor("Branco");
        produtoEditado.setEstoqueMinimo(10);
        produtoEditado.setDescricao("Tênis para o dia a dia");

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
        Assertions.assertEquals(produtoEditado.getEstoqueMinimo(), produtoSalvo.getEstoqueMinimo());
        Assertions.assertEquals(produtoEditado.getDescricao(), produtoSalvo.getDescricao());
    }

    @Test
    void editarLancaExcecaoQuandoReferenciaECorJaPertencemAOutroProduto() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setReferencia("123");
        produto.setCor("Preto");

        when(produtoRepository.findByIdAndExcluidoFalse(produto.getId())).thenReturn(Optional.of(produto));

        Produto outroProduto = new Produto();
        outroProduto.setId(170L);

        when(produtoRepository.findByReferenciaAndCorAndExcluidoFalse("123", "Preto")).thenReturn(Optional.of(outroProduto));

        EditarProdutoDTO produtoDTO = new EditarProdutoDTO();
        produtoDTO.setReferencia("123");
        produtoDTO.setCor("Preto");

        RuntimeException excecao = Assertions.assertThrows(
                RuntimeException.class,
                () -> produtoServiceImpl.editar(produto.getId(), produtoDTO)
        );

        Assertions.assertEquals("Já existe um produto com esta referência e cor.", excecao.getMessage());

        verify(produtoRepository).findByIdAndExcluidoFalse(produto.getId());
        verify(produtoRepository)
                .findByReferenciaAndCorAndExcluidoFalse("123", "Preto");
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void editarProdutoPermiteManterReferenciaECorDoProprioProduto() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setReferencia("123");
        produto.setCor("Preto");

        when(produtoRepository.findByIdAndExcluidoFalse(produto.getId()))
                .thenReturn(Optional.of(produto));
        when(produtoRepository.findByReferenciaAndCorAndExcluidoFalse("123", "Preto"))
                .thenReturn(Optional.of(produto));

        EditarProdutoDTO produtoDTO = new EditarProdutoDTO();
        produtoDTO.setReferencia("123");
        produtoDTO.setNome("Tênis");
        produtoDTO.setMarca("Marca");
        produtoDTO.setModelo(ModeloProduto.TENIS);
        produtoDTO.setCor("Preto");
        produtoDTO.setEstoqueMinimo(0);
        produtoDTO.setDescricao("Descrição");

        assertDoesNotThrow(() -> produtoServiceImpl.editar(produto.getId(), produtoDTO));

        verify(produtoRepository).save(produto);
    }

    @Test
    void produtoNaoEncontradoLancaExcecao() {
        //ARRANGE
        Long id = 149864L;

        when(produtoRepository.findByIdAndExcluidoFalse(id)).thenReturn(Optional.empty());
        EditarProdutoDTO produtoDTO = new EditarProdutoDTO();

        //ACT
        RuntimeException excecao = Assertions.assertThrows(
                RuntimeException.class,
                () -> produtoServiceImpl.editar(id, produtoDTO)
        );

        //ASSERT
        Assertions.assertEquals("Produto não encontrado.", excecao.getMessage());

        verify(produtoRepository, never()).save(any(Produto.class));
    }
}
