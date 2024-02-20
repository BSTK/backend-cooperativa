package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Associado;
import dev.bstk.cooperativa.pauta.model.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Long> {  }
