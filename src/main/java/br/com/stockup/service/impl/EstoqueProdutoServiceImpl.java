package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroEstoqueAtacadoDTO;
import br.com.stockup.dto.request.CadastroEstoqueTamanhoDTO;
import br.com.stockup.dto.request.CadastroEstoqueVarejoDTO;
import br.com.stockup.dto.request.EditarEstoqueDTO;
import br.com.stockup.enums.StatusProduto;
import br.com.stockup.enums.TipoEstoque;
import br.com.stockup.model.EstoqueProduto;
import br.com.stockup.model.EstoqueTamanho;
import br.com.stockup.model.Produto;
import br.com.stockup.repository.EstoqueProdutoRepository;
import br.com.stockup.repository.ProdutoRepository;
import br.com.stockup.service.EstoqueProdutoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EstoqueProdutoServiceImpl implements EstoqueProdutoService {

    private final EstoqueProdutoRepository estoqueProdutoRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueProdutoServiceImpl(EstoqueProdutoRepository estoqueProdutoRepository,
                                     ProdutoRepository produtoRepository) {
        this.estoqueProdutoRepository = estoqueProdutoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    @Transactional
    public void cadastrarAtacado(CadastroEstoqueAtacadoDTO cadastroEstoqueAtacadoDTO) {
        validarCadastroAtacado(cadastroEstoqueAtacadoDTO);

        Produto produto = produtoRepository.findByIdAndExcluidoFalse(cadastroEstoqueAtacadoDTO.getProdutoId()).orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        EstoqueProduto estoqueProduto = new EstoqueProduto();

        estoqueProduto.setProduto(produto);
        estoqueProduto.setPrecoVenda(cadastroEstoqueAtacadoDTO.getPrecoVenda());
        estoqueProduto.setPrecoCusto(cadastroEstoqueAtacadoDTO.getPrecoCusto());
        estoqueProduto.setTipoEstoque(TipoEstoque.FICHA);
        estoqueProduto.setFicha(cadastroEstoqueAtacadoDTO.getFicha());
        estoqueProduto.setQuantidadeFichas(cadastroEstoqueAtacadoDTO.getQuantidadeFichas());

        if (cadastroEstoqueAtacadoDTO.getQuantidadeFichas() > 0) {
            produto.setStatus(StatusProduto.EM_ESTOQUE);
        } else {
            produto.setStatus(StatusProduto.ESGOTADO);
        }

        estoqueProdutoRepository.save(estoqueProduto);
    }

    @Override
    @Transactional
    public void cadastrarVarejo(CadastroEstoqueVarejoDTO cadastroEstoqueVarejoDTO) {
        validarCadastroVarejo(cadastroEstoqueVarejoDTO);

        Produto produto = produtoRepository.findByIdAndExcluidoFalse(cadastroEstoqueVarejoDTO.getProdutoId()).orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        EstoqueProduto estoqueProduto = new EstoqueProduto();

        estoqueProduto.setProduto(produto);
        estoqueProduto.setPrecoVenda(cadastroEstoqueVarejoDTO.getPrecoVenda());
        estoqueProduto.setPrecoCusto(cadastroEstoqueVarejoDTO.getPrecoCusto());
        estoqueProduto.setTipoEstoque(TipoEstoque.PAR);

        for(CadastroEstoqueTamanhoDTO tamanhoDTO : cadastroEstoqueVarejoDTO.getQuantidadePorTamanho()) {
            EstoqueTamanho estoqueTamanho = new EstoqueTamanho();

            estoqueTamanho.setTamanho(tamanhoDTO.getTamanho());
            estoqueTamanho.setQuantidade(tamanhoDTO.getQuantidade());
            estoqueTamanho.setEstoqueProduto(estoqueProduto);
            estoqueProduto.getQuantidadePorTamanho().add(estoqueTamanho);
        }

        int quantidadeTotal = estoqueProduto.getQuantidadePorTamanho()
                .stream()
                .mapToInt(EstoqueTamanho::getQuantidade)
                .sum();

        if(quantidadeTotal > 0) {
            produto.setStatus(StatusProduto.EM_ESTOQUE);
        } else {
            produto.setStatus(StatusProduto.ESGOTADO);
        }

        estoqueProdutoRepository.save(estoqueProduto);
    }

    private void validarCadastroAtacado(CadastroEstoqueAtacadoDTO cadastroEstoqueAtacadoDTO) {
        if(cadastroEstoqueAtacadoDTO.getPrecoVenda().compareTo(cadastroEstoqueAtacadoDTO.getPrecoCusto()) <= 0) {
            throw new RuntimeException("Preço de venda não pode ser menor ou igual ao preço de custo.");
        }
    }

    private void validarCadastroVarejo(CadastroEstoqueVarejoDTO cadastroEstoqueVarejoDTO) {
        if (cadastroEstoqueVarejoDTO.getPrecoVenda().compareTo(cadastroEstoqueVarejoDTO.getPrecoCusto()) <= 0) {
            throw new RuntimeException("O preço de venda não pode ser menor ou igual ao preço de custo.");
        }

        validarQuantidadeTamanho(cadastroEstoqueVarejoDTO.getQuantidadePorTamanho());
    }

    @Override
    @Transactional
    public void editarAtacado(EditarEstoqueDTO dto) {
        if (dto.getFicha() == null) {
            throw new RuntimeException("A ficha é obrigatória para estoque de atacado.");
        }

        if (dto.getQuantidadeFichas() == null) {
            throw new RuntimeException("A quantidade de fichas é obrigatória para estoque de atacado.");
        }

        validarPrecos(dto.getPrecoCusto(), dto.getPrecoVenda());

        EstoqueProduto estoqueProduto =
                estoqueProdutoRepository.findByProdutoIdAndTipoEstoque(
                                dto.getProdutoId(),
                                TipoEstoque.FICHA
                        )
                        .orElseThrow(() -> new RuntimeException("Estoque de atacado não encontrado para o produto informado."));

        Produto produto = estoqueProduto.getProduto();

        if (produto.isExcluido()) {
            throw new RuntimeException("Produto não encontrado.");
        }

        estoqueProduto.setPrecoCusto(dto.getPrecoCusto());
        estoqueProduto.setPrecoVenda(dto.getPrecoVenda());
        estoqueProduto.setFicha(dto.getFicha());
        estoqueProduto.setQuantidadeFichas(dto.getQuantidadeFichas());

        if (dto.getQuantidadeFichas() > 0) {
            produto.setStatus(StatusProduto.EM_ESTOQUE);
        } else {
            produto.setStatus(StatusProduto.ESGOTADO);
        }

        estoqueProdutoRepository.save(estoqueProduto);
    }

    @Override
    @Transactional
    public void editarVarejo(EditarEstoqueDTO cadastroEstoqueDTO) {
        if (cadastroEstoqueDTO.getTamanho() == null) {
            throw new RuntimeException("O tamanho é obrigatório para estoque.");
        }

        if (cadastroEstoqueDTO.getQuantidade() == null) {
            throw new RuntimeException("A quantidade é obrigatória para o estoque.");
        }

        validarPrecos(cadastroEstoqueDTO.getPrecoCusto(), cadastroEstoqueDTO.getPrecoVenda());

        EstoqueProduto estoqueProduto = estoqueProdutoRepository.findByProdutoIdAndTipoEstoque(
                                cadastroEstoqueDTO.getProdutoId(),
                                TipoEstoque.PAR)
                        .orElseThrow(() ->
                                new RuntimeException("Estoque não encontrado para o produto informado."));

        Produto produto = estoqueProduto.getProduto();

        if(produto.isExcluido()) {
            throw new RuntimeException("Produto não encontrado.");
        }

        estoqueProduto.setPrecoCusto(cadastroEstoqueDTO.getPrecoCusto());
        estoqueProduto.setPrecoVenda(cadastroEstoqueDTO.getPrecoVenda());

        EstoqueTamanho estoqueTamanho =
                estoqueProduto.getQuantidadePorTamanho()
                        .stream()
                        .filter(item -> item.getTamanho() == cadastroEstoqueDTO.getTamanho())
                        .findFirst()
                        .orElse(null);



        if (estoqueTamanho != null) {

            estoqueTamanho.setQuantidade(cadastroEstoqueDTO.getQuantidade());

        } else {

            estoqueTamanho = new EstoqueTamanho();

            estoqueTamanho.setTamanho(cadastroEstoqueDTO.getTamanho());
            estoqueTamanho.setQuantidade(cadastroEstoqueDTO.getQuantidade());
            estoqueTamanho.setEstoqueProduto(estoqueProduto);

            estoqueProduto.getQuantidadePorTamanho().add(estoqueTamanho);
        }

        int quantidadeTotal = estoqueProduto.getQuantidadePorTamanho()
                .stream()
                .mapToInt(EstoqueTamanho::getQuantidade)
                .sum();

        if (quantidadeTotal > 0) {
            produto.setStatus(StatusProduto.EM_ESTOQUE);
        } else {
            produto.setStatus(StatusProduto.ESGOTADO);
        }

        estoqueProdutoRepository.save(estoqueProduto);
    }

    private void validarPrecos(BigDecimal precoCusto, BigDecimal precoVenda) {
        if (precoVenda.compareTo(precoCusto) <= 0) {
            throw new RuntimeException("O preço de venda não pode ser menor ou igual ao preço de custo.");
        }
    }

    private void validarQuantidadeTamanho(List<CadastroEstoqueTamanhoDTO> tamanhosDTO) {
        boolean possuiQuantidade = false;

        for (CadastroEstoqueTamanhoDTO tamanho : tamanhosDTO) {
            if(tamanho.getQuantidade() > 0) {
                possuiQuantidade = true;
                break;
            }
        }
        if(!possuiQuantidade){
            throw new RuntimeException("Informe pelo menos um tamanho.");
        }
    }
}
