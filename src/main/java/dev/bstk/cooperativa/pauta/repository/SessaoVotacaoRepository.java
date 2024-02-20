package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    /// TODO - REFATIRAR NOMES DE MÉTODOS
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SessaoVotacao s "
            + " WHERE s.status = 'INICIADA' "
            + " AND s.pauta.id = :pautaId "
            + " AND s.pauta.status = 'EM_VOTACAO'")
    boolean pautaEstaEmVotacao(@Param("pautaId") final Long pautaId);

    /// TODO - REFATIRAR NOMES DE MÉTODOS
    default boolean pautaNaoEstaEmVotacao(@Param("pautaId") final Long pautaId) {
        return !pautaEstaEmVotacao(pautaId);
    }
}
