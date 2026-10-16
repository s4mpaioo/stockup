package br.com.stockup.service;

import br.com.stockup.dto.request.CadastroClienteDTO;
import br.com.stockup.dto.response.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {

    void cadastrarCliente(CadastroClienteDTO cadastroClienteDTO);

    List<ClienteResponseDTO> listarClientes(Long lojaId);
}
