package br.com.stockup.repository;

import br.com.stockup.model.EstoqueTamanho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueTamanhoRepository extends JpaRepository<EstoqueTamanho, Long> {
}
