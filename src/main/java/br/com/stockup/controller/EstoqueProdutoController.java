package br.com.stockup.controller;

import br.com.stockup.dto.request.CadastroEstoqueAtacadoDTO;

import br.com.stockup.dto.request.CadastroEstoqueVarejoDTO;

import br.com.stockup.dto.request.EditarEstoqueDTO;
import br.com.stockup.service.EstoqueProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoque")
public class EstoqueProdutoController {

    private final EstoqueProdutoService estoqueProdutoService;

    public EstoqueProdutoController(EstoqueProdutoService estoqueProdutoService) {
        this.estoqueProdutoService = estoqueProdutoService;
    }

    @PostMapping("/atacado")
    public ResponseEntity<Void> cadastrarAtacado(@Valid @RequestBody CadastroEstoqueAtacadoDTO cadastroEstoqueAtacadoDTO) {
        estoqueProdutoService.cadastrarAtacado(cadastroEstoqueAtacadoDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/varejo")
    public ResponseEntity<Void> cadastrarVarejo(@Valid @RequestBody CadastroEstoqueVarejoDTO cadastroEstoqueVarejoDTO) {
        estoqueProdutoService.cadastrarVarejo(cadastroEstoqueVarejoDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/atacado")
    public ResponseEntity<Void> editarAtacado(
            @Valid @RequestBody EditarEstoqueDTO dto) {

        estoqueProdutoService.editarAtacado(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/varejo")
    public ResponseEntity<Void> editarVarejo(
            @Valid @RequestBody EditarEstoqueDTO dto) {

        estoqueProdutoService.editarVarejo(dto);

        return ResponseEntity.ok().build();
    }
}
