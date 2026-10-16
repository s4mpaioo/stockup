package br.com.stockup.repository;

import br.com.stockup.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByLojaId(Long lojaId);
}
