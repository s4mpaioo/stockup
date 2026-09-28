package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.dto.response.ProdutoResponseDTO;
import br.com.stockup.enums.StatusProduto;
import br.com.stockup.enums.TipoEstoque;
import br.com.stockup.model.EstoqueProduto;
import br.com.stockup.model.EstoqueTamanho;
import br.com.stockup.model.Loja;
import br.com.stockup.model.Produto;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.service.ProdutoService;
import org.springframework.stereotype.Service;
import br.com.stockup.repository.ProdutoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final LojaRepository lojaRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository, LojaRepository lojaRepository) {
        this.produtoRepository = produtoRepository;
        this.lojaRepository = lojaRepository;
    }

    @Override
    public void cadastrarProduto(CadastroProdutoDTO cadastroProdutoDTO) {
        Loja loja = lojaRepository.findById(cadastroProdutoDTO.getLojaId())
                .orElseThrow(() -> new RuntimeException("Loja não encontrada."));

        if(produtoRepository.existsByReferenciaAndCorAndExcluidoFalse(cadastroProdutoDTO.getReferencia(), cadastroProdutoDTO.getCor())) {
            throw new RuntimeException("Já existe um produto com esta referência e cor.");
        }

        Produto produto = criarProduto(cadastroProdutoDTO, loja);

        produtoRepository.save(produto);
    }

    private Produto criarProduto(CadastroProdutoDTO cadastroProdutoDTO, Loja loja) {
        Produto produto = new Produto();

        produto.setReferencia(cadastroProdutoDTO.getReferencia());
        produto.setNome(cadastroProdutoDTO.getNome());
        produto.setMarca(cadastroProdutoDTO.getMarca());
        produto.setModelo(cadastroProdutoDTO.getModelo());
        produto.setCor(cadastroProdutoDTO.getCor());
        produto.setDescricao(cadastroProdutoDTO.getDescricao());
        produto.setEstoqueMinimo(cadastroProdutoDTO.getEstoqueMinimo());

        produto.setLoja(loja);

        return produto;
    }

    @Override
    public void editar(Long id, CadastroProdutoDTO cadastroProdutoDTO) {
        Optional<Produto> produto = produtoRepository.findByIdAndExcluidoFalse(id);

        if (produto.isEmpty()) {
            throw new RuntimeException("Produto não encontrado.");
        }

        Produto produtoEncontrado = produto.get();

        Optional<Produto> produtoReferencia = produtoRepository.findByReferenciaAndCorAndExcluidoFalse(cadastroProdutoDTO.getReferencia(), cadastroProdutoDTO.getCor());

        if (produtoReferencia.isPresent() && !produtoReferencia.get().getId().equals(id)) {
            throw new RuntimeException("Já existe um produto com esta referência e cor.");
        }

        produtoEncontrado.setReferencia(cadastroProdutoDTO.getReferencia());
        produtoEncontrado.setNome(cadastroProdutoDTO.getNome());
        produtoEncontrado.setMarca(cadastroProdutoDTO.getMarca());
        produtoEncontrado.setModelo(cadastroProdutoDTO.getModelo());
        produtoEncontrado.setCor(cadastroProdutoDTO.getCor());
        produtoEncontrado.setDescricao(cadastroProdutoDTO.getDescricao());

        produtoRepository.save(produtoEncontrado);
    }

    @Override
    public void excluir(Long id) {
        Optional <Produto> produto = produtoRepository.findByIdAndExcluidoFalse(id);
        if (produto.isEmpty()) {
            throw new  RuntimeException("Produto não encontrado.");
        }

        Produto produtoEncontrado = produto.get();

        if (!produtoEncontrado.getEstoques().isEmpty()) {

            EstoqueProduto estoque = produtoEncontrado.getEstoques().get(0);

            if (estoque.getTipoEstoque() == TipoEstoque.PAR) {
                validarProdutoVarejoSemEstoque(produtoEncontrado);

            } else if (estoque.getTipoEstoque() == TipoEstoque.FICHA) {
                validarProdutoAtacadoSemEstoque(produtoEncontrado);
            }
        }

        produtoEncontrado.setExcluido(true);

        produtoRepository.save(produtoEncontrado);
    }

    @Override
    public List<ProdutoResponseDTO> buscar(String termo) {
        return produtoRepository
                .findByNomeContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrCorContainingIgnoreCaseOrReferenciaContainingIgnoreCaseAndExcluidoFalse(
                        termo, termo, termo, termo
                )
                .stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoResponseDTO> listarProdutos(StatusProduto status) {
        List<Produto> produtos;

        if(status == null) {
            produtos = produtoRepository.findByExcluidoFalse();
        } else {
            produtos = produtoRepository.findByExcluidoFalseAndStatus(status);
        }

        return produtos.stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    private void validarProdutoVarejoSemEstoque(Produto produto) {
        for(EstoqueProduto estoque : produto.getEstoques()) {
            if(estoque.getTipoEstoque() == TipoEstoque.PAR) {
                for(EstoqueTamanho estoqueTamanho : estoque.getQuantidadePorTamanho()) {
                    if(estoqueTamanho.getQuantidade() != null && estoqueTamanho.getQuantidade() > 0) {
                        throw new RuntimeException("Não é possível excluir um produto que possui em estoque.");
                    }
                }
            }
        }
    }

    private void validarProdutoAtacadoSemEstoque(Produto produto) {
        for(EstoqueProduto estoque : produto.getEstoques()) {
            if(estoque.getTipoEstoque() == TipoEstoque.FICHA && estoque.getQuantidadeFichas() > 0) {
                throw new RuntimeException("Não é possível excluir um produto que possui em estoque.");
            }
        }
    }
}
