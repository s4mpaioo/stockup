package br.com.stockup.service.impl;


import br.com.stockup.dto.request.CadastroItemVendaVarejoDTO;
import br.com.stockup.dto.request.CadastroVendaDTO;
import br.com.stockup.enums.FormaPagamento;
import br.com.stockup.enums.StatusProduto;
import br.com.stockup.enums.TipoEstoque;
import br.com.stockup.enums.TipoLoja;
import br.com.stockup.model.*;
import br.com.stockup.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceImplTest {
    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EstoqueProdutoRepository estoqueProdutoRepository;

    @Mock
    private EstoqueTamanhoRepository estoqueTamanhoRepository;

    @Mock
    private DividaRepository dividaRepository;

    @InjectMocks
    private VendaServiceImpl vendaService;

    @Test
    void realizarVendaVarejoValida() {
        // ARRANGE
        Loja loja = new Loja();
        loja.setId(2L);
        loja.setTipo(TipoLoja.VAREJO);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setLoja(loja);
        produto.setExcluido(false);
        produto.setStatus(StatusProduto.EM_ESTOQUE);

        EstoqueProduto estoqueProduto = new EstoqueProduto();
        estoqueProduto.setId(10L);
        estoqueProduto.setTipoEstoque(TipoEstoque.PAR);
        estoqueProduto.setPrecoCusto(new BigDecimal("50.00"));
        estoqueProduto.setPrecoVenda(new BigDecimal("100.00"));
        estoqueProduto.setProduto(produto);

        EstoqueTamanho estoqueTamanho = new EstoqueTamanho();
        estoqueTamanho.setId(20L);
        estoqueTamanho.setQuantidade(5);
        estoqueTamanho.setEstoqueProduto(estoqueProduto);

        estoqueProduto.setQuantidadePorTamanho(
                new ArrayList<>(List.of(estoqueTamanho))
        );

        produto.setEstoques(new ArrayList<>(List.of(estoqueProduto)));

        CadastroItemVendaVarejoDTO itemDTO = new CadastroItemVendaVarejoDTO();
        itemDTO.setEstoqueTamanhoId(20L);
        itemDTO.setQuantidadePares(2);

        CadastroVendaDTO vendaDTO = new CadastroVendaDTO();
        vendaDTO.setLojaId(2L);
        vendaDTO.setFormaPagamento(FormaPagamento.PIX);
        vendaDTO.setDesconto(BigDecimal.ZERO);
        vendaDTO.setItensVarejo(List.of(itemDTO));

        when(lojaRepository.findById(2L))
                .thenReturn(Optional.of(loja));

        when(estoqueTamanhoRepository.findById(20L))
                .thenReturn(Optional.of(estoqueTamanho));

        // ACT
        vendaService.realizarVenda(vendaDTO);

        // ASSERT
        assertEquals(3, estoqueTamanho.getQuantidade());
        assertEquals(StatusProduto.EM_ESTOQUE, produto.getStatus());

        ArgumentCaptor<Venda> vendaCaptor =
                ArgumentCaptor.forClass(Venda.class);

        verify(vendaRepository).save(vendaCaptor.capture());

        Venda vendaSalva = vendaCaptor.getValue();

        assertEquals(loja, vendaSalva.getLoja());
        assertEquals(FormaPagamento.PIX, vendaSalva.getFormaPagamento());
        assertEquals(new BigDecimal("200.00"), vendaSalva.getValorTotal());
        assertEquals(1, vendaSalva.getItensVarejo().size());

        verify(dividaRepository, never()).save(any(Divida.class));
    }
}