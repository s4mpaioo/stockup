package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroItemVendaAtacadoDTO;
import br.com.stockup.dto.request.CadastroItemVendaVarejoDTO;
import br.com.stockup.dto.request.CadastroVendaDTO;
import br.com.stockup.dto.response.VendaResponseDTO;
import br.com.stockup.enums.FormaPagamento;
import br.com.stockup.enums.PeriodicidadePagamento;
import br.com.stockup.enums.StatusProduto;
import br.com.stockup.enums.StatusVenda;
import br.com.stockup.enums.TipoEstoque;
import br.com.stockup.enums.TipoLoja;
import br.com.stockup.model.Cliente;
import br.com.stockup.model.Divida;
import br.com.stockup.model.EstoqueProduto;
import br.com.stockup.model.EstoqueTamanho;
import br.com.stockup.model.ItemVendaAtacado;
import br.com.stockup.model.ItemVendaVarejo;
import br.com.stockup.model.Loja;
import br.com.stockup.model.Parcela;
import br.com.stockup.model.Produto;
import br.com.stockup.model.Venda;
import br.com.stockup.repository.ClienteRepository;
import br.com.stockup.repository.DividaRepository;
import br.com.stockup.repository.EstoqueProdutoRepository;
import br.com.stockup.repository.EstoqueTamanhoRepository;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.repository.VendaRepository;
import br.com.stockup.service.VendaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class VendaServiceImpl implements VendaService {

    private final VendaRepository vendaRepository;
    private final LojaRepository lojaRepository;
    private final ClienteRepository clienteRepository;
    private final EstoqueProdutoRepository estoqueProdutoRepository;
    private final EstoqueTamanhoRepository estoqueTamanhoRepository;
    private final DividaRepository dividaRepository;

    public VendaServiceImpl(
            VendaRepository vendaRepository,
            LojaRepository lojaRepository,
            ClienteRepository clienteRepository,
            EstoqueProdutoRepository estoqueProdutoRepository,
            EstoqueTamanhoRepository estoqueTamanhoRepository,
            DividaRepository dividaRepository) {
        this.vendaRepository = vendaRepository;
        this.lojaRepository = lojaRepository;
        this.clienteRepository = clienteRepository;
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.estoqueTamanhoRepository = estoqueTamanhoRepository;
        this.dividaRepository = dividaRepository;
    }

    @Override
    @Transactional
    public void realizarVenda(CadastroVendaDTO cadastroVendaDTO) {
        validarPagamento(cadastroVendaDTO);

        Loja loja = lojaRepository.findById(cadastroVendaDTO.getLojaId())
                .orElseThrow(() -> new RuntimeException("Loja não encontrada."));

        Cliente cliente = buscarCliente(cadastroVendaDTO, loja);
        validarItensDaLoja(cadastroVendaDTO, loja);

        Venda venda = criarVenda(cadastroVendaDTO, loja, cliente);

        ResultadoVenda resultado;
        if (loja.getTipo() == TipoLoja.VAREJO) {
            resultado = processarItensVarejo(cadastroVendaDTO, venda, loja);
        } else {
            resultado = processarItensAtacado(cadastroVendaDTO, venda, loja);
        }

        BigDecimal valorTotal = resultado.valorBruto().subtract(venda.getDesconto());
        if (valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("O desconto não pode ser maior que o valor da venda.");
        }

        venda.setValorTotal(valorTotal);
        venda.setLucro(resultado.lucro().subtract(venda.getDesconto()));

        atualizarStatusProdutos(venda);
        vendaRepository.save(venda);

        if (deveCriarDivida(venda.getFormaPagamento())) {
            criarDivida(venda, cadastroVendaDTO);
        }
    }

    private Venda criarVenda(CadastroVendaDTO cadastroVendaDTO, Loja loja, Cliente cliente) {
        Venda venda = new Venda();
        venda.setDataVenda(LocalDateTime.now());
        venda.setLoja(loja);
        venda.setCliente(cliente);
        venda.setFormaPagamento(cadastroVendaDTO.getFormaPagamento());
        venda.setQuantidadeParcelas(cadastroVendaDTO.getQuantidadeParcelas());
        venda.setStatus(StatusVenda.CONCLUIDA);
        venda.setDesconto(obterDesconto(cadastroVendaDTO));
        return venda;
    }

    @Override
    @Transactional
    public List<VendaResponseDTO> listarVendas(Long lojaId) {
        lojaRepository.findById(lojaId)
                .orElseThrow(() -> new RuntimeException("Loja não encontrada."));

        return vendaRepository.findByLojaId(lojaId)
                .stream()
                .map(VendaResponseDTO::new)
                .toList();
    }

    private BigDecimal obterDesconto(CadastroVendaDTO cadastroVendaDTO) {
        return cadastroVendaDTO.getDesconto() == null
                ? BigDecimal.ZERO
                : cadastroVendaDTO.getDesconto();
    }

    private boolean deveCriarDivida(FormaPagamento formaPagamento) {
        return formaPagamento == FormaPagamento.BOLETO || formaPagamento == FormaPagamento.CREDITO_PARCELADO;
    }

    private void validarPagamento(CadastroVendaDTO cadastroVendaDTO) {
        if (cadastroVendaDTO == null) {
            throw new RuntimeException("Os dados da venda são obrigatórios.");
        }

        if (cadastroVendaDTO.getFormaPagamento() == null) {
            throw new RuntimeException("A forma de pagamento é obrigatória.");
        }

        FormaPagamento formaPagamento = cadastroVendaDTO.getFormaPagamento();

        if (formaPagamento == FormaPagamento.BOLETO || formaPagamento == FormaPagamento.CREDITO_PARCELADO) {
            if (cadastroVendaDTO.getQuantidadeParcelas() == null
                    || cadastroVendaDTO.getQuantidadeParcelas() < 1) {
                throw new RuntimeException(
                        "A quantidade de parcelas é obrigatória para esta forma de pagamento.");
            }
        }

        if (formaPagamento == FormaPagamento.BOLETO
                && cadastroVendaDTO.getPeriodicidade() == null) {
            throw new RuntimeException("A periodicidade é obrigatória para boleto.");
        }
    }

    private Cliente buscarCliente(CadastroVendaDTO cadastroVendaDTO, Loja loja) {
        if (cadastroVendaDTO.getClienteId() == null) {
            boolean clienteObrigatorio = loja.getTipo() == TipoLoja.ATACADO
                    || cadastroVendaDTO.getFormaPagamento() == FormaPagamento.BOLETO
                    || cadastroVendaDTO.getFormaPagamento() == FormaPagamento.CREDITO_PARCELADO;

            if (clienteObrigatorio) {
                throw new RuntimeException("O cliente é obrigatório para esta venda.");
            }

            return null;
        }

        Cliente cliente = clienteRepository.findById(cadastroVendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        validarClienteDaLoja(cliente, loja);
        return cliente;
    }

    private void validarClienteDaLoja(Cliente cliente, Loja loja) {
        if (cliente.getLoja() == null
                || !cliente.getLoja().getId().equals(loja.getId())) {
            throw new RuntimeException("O cliente não pertence à loja informada.");
        }
    }

    private void validarItensDaLoja(CadastroVendaDTO cadastroVendaDTO, Loja loja) {
        boolean possuiItensVarejo = cadastroVendaDTO.getItensVarejo() != null
                && !cadastroVendaDTO.getItensVarejo().isEmpty();
        boolean possuiItensAtacado = cadastroVendaDTO.getItensAtacado() != null
                && !cadastroVendaDTO.getItensAtacado().isEmpty();

        if (!possuiItensVarejo && !possuiItensAtacado) {
            throw new RuntimeException("A venda deve possuir pelo menos um item.");
        }

        if (possuiItensVarejo && possuiItensAtacado) {
            throw new RuntimeException("A venda não pode misturar itens de varejo e atacado.");
        }

        if (loja.getTipo() == TipoLoja.VAREJO && !possuiItensVarejo) {
            throw new RuntimeException("A loja varejista deve possuir itens de varejo.");
        }

        if (loja.getTipo() == TipoLoja.ATACADO && !possuiItensAtacado) {
            throw new RuntimeException("A loja atacadista deve possuir itens de atacado.");
        }
    }

    private ResultadoVenda processarItensVarejo(CadastroVendaDTO cadastroVendaDTO, Venda venda, Loja loja) {
        Map<Long, Integer> quantidadesSolicitadas = new HashMap<>();
        Map<Long, EstoqueTamanho> estoques = new HashMap<>();

        for (CadastroItemVendaVarejoDTO itemDTO : cadastroVendaDTO.getItensVarejo()) {
            EstoqueTamanho estoqueTamanho = estoqueTamanhoRepository
                    .findById(itemDTO.getEstoqueTamanhoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Estoque por tamanho não encontrado."));

            validarEstoqueDaLoja(estoqueTamanho.getEstoqueProduto(), loja, TipoEstoque.PAR);

            Long estoqueTamanhoId = estoqueTamanho.getId();
            quantidadesSolicitadas.merge(
                    estoqueTamanhoId,
                    itemDTO.getQuantidadePares(),
                    Integer::sum);
            estoques.put(estoqueTamanhoId, estoqueTamanho);
        }

        for (Map.Entry<Long, Integer> entrada : quantidadesSolicitadas.entrySet()) {
            EstoqueTamanho estoqueTamanho = estoques.get(entrada.getKey());

            if (estoqueTamanho.getQuantidade() < entrada.getValue()) {
                throw new RuntimeException("Estoque insuficiente para o tamanho informado.");
            }
        }

        BigDecimal valorBruto = BigDecimal.ZERO;
        BigDecimal lucro = BigDecimal.ZERO;

        for (CadastroItemVendaVarejoDTO itemDTO : cadastroVendaDTO.getItensVarejo()) {
            EstoqueTamanho estoqueTamanho = estoques.get(itemDTO.getEstoqueTamanhoId());
            EstoqueProduto estoqueProduto = estoqueTamanho.getEstoqueProduto();
            BigDecimal quantidade = BigDecimal.valueOf(itemDTO.getQuantidadePares());
            BigDecimal valorPar = estoqueProduto.getPrecoVenda();

            ItemVendaVarejo item = new ItemVendaVarejo();
            item.setQuantidadePares(itemDTO.getQuantidadePares());
            item.setValorPar(valorPar);
            item.setSubtotal(valorPar.multiply(quantidade));
            item.setVenda(venda);
            item.setEstoqueTamanho(estoqueTamanho);
            venda.getItensVarejo().add(item);

            estoqueTamanho.setQuantidade(
                    estoqueTamanho.getQuantidade() - itemDTO.getQuantidadePares());
            valorBruto = valorBruto.add(item.getSubtotal());
            lucro = lucro.add(valorPar.subtract(estoqueProduto.getPrecoCusto()).multiply(quantidade));
        }

        return new ResultadoVenda(valorBruto, lucro);
    }

    private ResultadoVenda processarItensAtacado(
            CadastroVendaDTO cadastroVendaDTO,
            Venda venda,
            Loja loja) {
        Map<Long, Integer> quantidadesSolicitadas = new HashMap<>();
        Map<Long, EstoqueProduto> estoques = new HashMap<>();

        for (CadastroItemVendaAtacadoDTO itemDTO : cadastroVendaDTO.getItensAtacado()) {
            EstoqueProduto estoqueProduto = estoqueProdutoRepository
                    .findById(itemDTO.getEstoqueProdutoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Estoque de atacado não encontrado."));

            validarEstoqueDaLoja(estoqueProduto, loja, TipoEstoque.FICHA);

            Long estoqueProdutoId = estoqueProduto.getId();
            quantidadesSolicitadas.merge(
                    estoqueProdutoId,
                    itemDTO.getQuantidadeFichas(),
                    Integer::sum);
            estoques.put(estoqueProdutoId, estoqueProduto);
        }

        for (Map.Entry<Long, Integer> entrada : quantidadesSolicitadas.entrySet()) {
            EstoqueProduto estoqueProduto = estoques.get(entrada.getKey());

            if (estoqueProduto.getQuantidadeFichas() == null
                    || estoqueProduto.getQuantidadeFichas() < entrada.getValue()) {
                throw new RuntimeException("Quantidade de fichas insuficiente.");
            }
        }

        BigDecimal valorBruto = BigDecimal.ZERO;
        BigDecimal lucro = BigDecimal.ZERO;

        for (CadastroItemVendaAtacadoDTO itemDTO : cadastroVendaDTO.getItensAtacado()) {
            EstoqueProduto estoqueProduto = estoques.get(itemDTO.getEstoqueProdutoId());
            BigDecimal quantidade = BigDecimal.valueOf(itemDTO.getQuantidadeFichas());
            BigDecimal valorFicha = estoqueProduto.getPrecoVenda();

            ItemVendaAtacado item = new ItemVendaAtacado();
            item.setQuantidadeFichas(itemDTO.getQuantidadeFichas());
            item.setValorFicha(valorFicha);
            item.setSubtotal(valorFicha.multiply(quantidade));
            item.setVenda(venda);
            item.setEstoqueProduto(estoqueProduto);
            venda.getItensAtacado().add(item);

            estoqueProduto.setQuantidadeFichas(
                    estoqueProduto.getQuantidadeFichas() - itemDTO.getQuantidadeFichas());
            valorBruto = valorBruto.add(item.getSubtotal());
            lucro = lucro.add(
                    valorFicha.subtract(estoqueProduto.getPrecoCusto()).multiply(quantidade));
        }

        return new ResultadoVenda(valorBruto, lucro);
    }

    private void validarEstoqueDaLoja(
            EstoqueProduto estoqueProduto,
            Loja loja,
            TipoEstoque tipoEstoque) {
        if (estoqueProduto == null || estoqueProduto.getProduto() == null) {
            throw new RuntimeException("Estoque ou produto não encontrado.");
        }

        Produto produto = estoqueProduto.getProduto();

        if (produto.isExcluido()) {
            throw new RuntimeException("Produto não encontrado.");
        }

        if (produto.getLoja() == null
                || !produto.getLoja().getId().equals(loja.getId())) {
            throw new RuntimeException("O estoque não pertence à loja informada.");
        }

        if (estoqueProduto.getTipoEstoque() != tipoEstoque) {
            throw new RuntimeException("O tipo de estoque não corresponde ao tipo da venda.");
        }
    }

    private void atualizarStatusProdutos(Venda venda) {
        Set<Produto> produtos = new HashSet<>();

        for (ItemVendaVarejo item : venda.getItensVarejo()) {
            produtos.add(item.getEstoqueTamanho().getEstoqueProduto().getProduto());
        }

        for (ItemVendaAtacado item : venda.getItensAtacado()) {
            produtos.add(item.getEstoqueProduto().getProduto());
        }

        for (Produto produto : produtos) {
            boolean possuiEstoque = produto.getEstoques().stream()
                    .anyMatch(this::possuiQuantidadeDisponivel);

            produto.setStatus(
                    possuiEstoque ? StatusProduto.EM_ESTOQUE : StatusProduto.ESGOTADO);
        }
    }

    private boolean possuiQuantidadeDisponivel(EstoqueProduto estoqueProduto) {
        if (estoqueProduto.getTipoEstoque() == TipoEstoque.FICHA) {
            return estoqueProduto.getQuantidadeFichas() != null
                    && estoqueProduto.getQuantidadeFichas() > 0;
        }

        return estoqueProduto.getQuantidadePorTamanho().stream()
                .anyMatch(estoqueTamanho -> estoqueTamanho.getQuantidade() > 0);
    }

    private void criarDivida(Venda venda, CadastroVendaDTO cadastroVendaDTO) {
        Divida divida = new Divida();
        divida.setValorTotal(venda.getValorTotal());
        divida.setQuantidadeParcelas(cadastroVendaDTO.getQuantidadeParcelas());
        divida.setPeriodicidade(obterPeriodicidadeDivida(cadastroVendaDTO));
        divida.setQuitada(false);
        divida.setVenda(venda);

        List<BigDecimal> valoresParcelas = dividirValor(venda.getValorTotal(), cadastroVendaDTO.getQuantidadeParcelas());
        LocalDate dataBase = LocalDate.now();

        for (int i = 0; i < valoresParcelas.size(); i++) {
            Parcela parcela = new Parcela();
            parcela.setNumero(i + 1);
            parcela.setValor(valoresParcelas.get(i));
            parcela.setDataVencimento(calcularVencimento(dataBase, divida.getPeriodicidade(), i));
            parcela.setPaga(false);
            parcela.setDivida(divida);
            divida.getParcelas().add(parcela);
        }

        dividaRepository.save(divida);
    }

    private PeriodicidadePagamento obterPeriodicidadeDivida(CadastroVendaDTO cadastroVendaDTO) {
        if (cadastroVendaDTO.getFormaPagamento() == FormaPagamento.CREDITO_PARCELADO) {
            return PeriodicidadePagamento.MENSAL;
        }
        return cadastroVendaDTO.getPeriodicidade();
    }

    private List<BigDecimal> dividirValor(BigDecimal valorTotal, Integer quantidadeParcelas) {
        BigDecimal valorBase = valorTotal.divide(BigDecimal.valueOf(quantidadeParcelas), 2, RoundingMode.DOWN);
        BigDecimal diferenca = valorTotal.subtract(valorBase.multiply(BigDecimal.valueOf(quantidadeParcelas)));
        List<BigDecimal> valores = new ArrayList<>();

        for (int i = 0; i < quantidadeParcelas; i++) {
            BigDecimal valorParcela = valorBase;

            if (i == 0) {
                valorParcela = valorParcela.add(diferenca);
            }

            valores.add(valorParcela);
        }

        return valores;
    }

    private LocalDate calcularVencimento(
            LocalDate dataBase,
            PeriodicidadePagamento periodicidade,
            int indiceParcela) {
        return switch (periodicidade) {
            case SEMANAL -> dataBase.plusWeeks(indiceParcela + 1L);
            case QUINZENAL -> dataBase.plusDays((indiceParcela + 1L) * 15);
            case MENSAL -> dataBase.plusMonths(indiceParcela + 1L);
        };
    }

    private record ResultadoVenda(BigDecimal valorBruto, BigDecimal lucro) {
    }
}
