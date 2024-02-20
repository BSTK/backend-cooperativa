package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociadoRepository extends JpaRepository<Associado, Long> {  }
