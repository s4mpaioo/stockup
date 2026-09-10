package br.com.stockup.repository;

import br.com.stockup.enums.StatusProduto;
import br.com.stockup.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByReferenciaAndCorAndExcluidoFalse(String referencia, String cor);

    Optional<Produto> findByIdAndExcluidoFalse(Long id);

    Optional<Produto> findByReferenciaAndCorAndExcluidoFalse(String referencia, String cor);

    Optional<Produto> findByReferenciaAndExcluidoFalse(String referencia);

    List<Produto> findByExcluidoFalse();

    List<Produto> findByNomeContainingIgnoreCaseAndExcluidoFalse(String nome);
}
