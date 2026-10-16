package br.com.stockup.controller;

import br.com.stockup.dto.request.CadastroClienteDTO;
import br.com.stockup.dto.response.ClienteResponseDTO;
import br.com.stockup.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrarCliente(@Valid @RequestBody CadastroClienteDTO cadastroClienteDTO) {
        clienteService.cadastrarCliente(cadastroClienteDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<ClienteResponseDTO> listarClientes(@RequestParam Long lojaId) {
        return clienteService.listarClientes(lojaId);
    }
}
