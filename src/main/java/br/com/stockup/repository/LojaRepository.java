package br.com.stockup.repository;

import br.com.stockup.model.Loja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LojaRepository extends JpaRepository<Loja, Long> {

}
