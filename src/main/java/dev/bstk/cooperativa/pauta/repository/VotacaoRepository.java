package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Long> {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Votacao v WHERE v.associadoId = :associadoId")
    boolean associadoJaVotou(@Param("associadoId") final Long associadoId);
}
