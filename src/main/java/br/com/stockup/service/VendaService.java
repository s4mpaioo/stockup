package br.com.stockup.service;

import br.com.stockup.dto.request.CadastroVendaDTO;
import br.com.stockup.dto.response.VendaResponseDTO;

import java.util.List;

public interface VendaService {

    void realizarVenda(CadastroVendaDTO cadastroVendaDTO);

    List<VendaResponseDTO> listarVendas(Long lojaId);
}
