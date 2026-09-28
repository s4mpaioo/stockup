package br.com.stockup.controller;

import br.com.stockup.dto.request.CadastroProdutoDTO;
import br.com.stockup.dto.response.ProdutoResponseDTO;
import br.com.stockup.enums.StatusProduto;
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

    @GetMapping("/buscar")
    public List<ProdutoResponseDTO> buscar(@RequestParam String termo) {
        return produtoService.buscar(termo);
    }

    @GetMapping("/listar-produtos")
    public List<ProdutoResponseDTO> listarProdutos (@RequestParam(required = false) StatusProduto status) {
        return produtoService.listarProdutos(status);
    }
}