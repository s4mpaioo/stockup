package br.com.stockup.repository;

import br.com.stockup.enums.TipoEstoque;
import br.com.stockup.model.EstoqueProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueProdutoRepository extends JpaRepository<EstoqueProduto, Long> {
    Optional<EstoqueProduto> findByProdutoIdAndTipoEstoque(
            Long produtoId, TipoEstoque tipoEstoque);
}
