package br.com.stockup.controller;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.model.Produto;
import br.com.stockup.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping("/cadastro")
    public void cadastrar(@Valid @RequestBody CadastroProdutoDTO cadastroProdutoDTO) {
        produtoService.cadastrarProduto(cadastroProdutoDTO);
    }

    @PutMapping("/{id}")
    public void editar(@PathVariable Long id, @Valid @RequestBody CadastroProdutoDTO cadastroProdutoDTO) {
        produtoService.editar(id, cadastroProdutoDTO);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }

    @GetMapping("/buscar-nome")
    public List<Produto> buscarPorNome(@RequestParam String nome) {
        return produtoService.buscarPorNome(nome);
    }

    @GetMapping("/{referencia}")
    public Produto buscarPorReferencia(@PathVariable String referencia) {
        return produtoService.buscarPorReferencia(referencia);
    }

    @GetMapping("/listar-produtos")
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }
}

