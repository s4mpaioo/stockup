package br.com.stockup.dto.response;

import br.com.stockup.enums.FormaPagamento;
import br.com.stockup.enums.StatusVenda;
import br.com.stockup.model.Venda;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class VendaResponseDTO {
    private Long id;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private BigDecimal desconto;
    private BigDecimal lucro;
    private FormaPagamento formaPagamento;
    private Integer quantidadeParcelas;
    private StatusVenda status;
    private Long clienteId;
    private String clienteNome;
    private Long lojaId;

    public VendaResponseDTO(Venda venda) {
        this.id = venda.getId();
        this.dataVenda = venda.getDataVenda();
        this.valorTotal = venda.getValorTotal();
        this.desconto = venda.getDesconto();
        this.lucro = venda.getLucro();
        this.formaPagamento = venda.getFormaPagamento();
        this.quantidadeParcelas = venda.getQuantidadeParcelas();
        this.status = venda.getStatus();

        if (venda.getCliente() != null) {
            this.clienteId = venda.getCliente().getId();
            this.clienteNome = venda.getCliente().getNome();
        }

        this.lojaId = venda.getLoja().getId();
    }
}