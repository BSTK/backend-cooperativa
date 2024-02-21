package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Votacao;
import dev.bstk.cooperativa.pauta.repository.projections.VotacaoPautaResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Long> {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Votacao v "
        + " WHERE v.sessao.pauta.id = :pautaId AND v.associadoId = :associadoId")
    boolean associadoJaVotou(@Param("pautaId") final Long pautaId,
                             @Param("associadoId") final Long associadoId);

    @Query(value = "select count(v.voto) as totalVotos, "
            + " count(nullif(v.voto = false, true)) as totalVotosSim, "
            + " count(nullif(v.voto = true, true)) as totalVotosNao   "
            + " from votacao v "
            + " where v.sessao_id = :sessaoId "
            + " group by v.sessao_id", nativeQuery = true)
    VotacaoPautaResultado contabilizarResultado(@Param("sessaoId") final Long sessaoId);
}
