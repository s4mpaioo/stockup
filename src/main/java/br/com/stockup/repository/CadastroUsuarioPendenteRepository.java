package br.com.stockup.repository;

import br.com.stockup.model.CadastroUsuarioPendente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CadastroUsuarioPendenteRepository extends JpaRepository<CadastroUsuarioPendente, Long> {

    Optional<CadastroUsuarioPendente> findByEmail(String email);
}
