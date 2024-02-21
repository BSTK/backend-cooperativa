package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Sessao;
import dev.bstk.cooperativa.pauta.model.Status.SessaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    @Query("SELECT s FROM Sessao s WHERE s.pauta.id = :pautaId")
    Optional<Sessao> buscarSessaoPorPauta(@Param("pautaId") final Long pautaId);

    @Query("SELECT s FROM Sessao s WHERE s.pauta.id = :pautaId AND s.status = :status")
    Optional<Sessao> buscarSessaoPorPautaEStatus(@Param("pautaId") final Long pautaId,
                                                 @Param("status") final SessaoStatus status);

    default Optional<Sessao> buscarSessaoAberta(@Param("pautaId") final Long pautaId) {
        return buscarSessaoPorPautaEStatus(pautaId, SessaoStatus.ABERTA);
    }
}
