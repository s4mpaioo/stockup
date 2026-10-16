package br.com.stockup.service.impl;

import br.com.stockup.dto.request.CadastroClienteDTO;
import br.com.stockup.dto.response.ClienteResponseDTO;
import br.com.stockup.model.Cliente;
import br.com.stockup.model.Loja;
import br.com.stockup.repository.ClienteRepository;
import br.com.stockup.repository.LojaRepository;
import br.com.stockup.service.ClienteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final LojaRepository lojaRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, LojaRepository lojaRepository) {
        this.clienteRepository = clienteRepository;
        this.lojaRepository = lojaRepository;
    }

    @Override
    public void cadastrarCliente(CadastroClienteDTO cadastroClienteDTO) {
        Loja loja = lojaRepository.findById(cadastroClienteDTO.getLojaId()).orElseThrow(() -> new RuntimeException("Loja não encontrada."));

        Cliente cliente = new Cliente();
        cliente.setNome(cadastroClienteDTO.getNome());
        cliente.setTelefone(cadastroClienteDTO.getTelefone());
        cliente.setLoja(loja);

        clienteRepository.save(cliente);
    }

    @Override
    public List<ClienteResponseDTO> listarClientes(Long lojaId) {
        lojaRepository.findById(lojaId)
                .orElseThrow(() -> new RuntimeException("Loja não encontrada."));

        return clienteRepository.findByLojaId(lojaId)
                .stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

}
