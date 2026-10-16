package br.com.stockup.repository;

import br.com.stockup.model.Divida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividaRepository extends JpaRepository<Divida, Long> {
}
