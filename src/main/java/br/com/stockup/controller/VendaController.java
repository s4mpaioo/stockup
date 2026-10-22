package br.com.stockup.controller;

import br.com.stockup.dto.request.CadastroVendaDTO;
import br.com.stockup.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.stockup.dto.response.VendaResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrarVenda(@Valid @RequestBody CadastroVendaDTO cadastroVendaDTO) {
        vendaService.realizarVenda(cadastroVendaDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<VendaResponseDTO> listarVendas(@RequestParam Long lojaId) {
        return vendaService.listarVendas(lojaId);
    }

}
