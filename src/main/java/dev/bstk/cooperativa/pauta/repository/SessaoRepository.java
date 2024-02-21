package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Enums.SessaoStatus;
import dev.bstk.cooperativa.pauta.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    @Query("SELECT s FROM Sessao s WHERE s.pauta.id = :pautaId")
    Optional<Sessao> buscarSessaoPorPauta(@Param("pautaId") final Long pautaId);

    @Query("SELECT s FROM Sessao s WHERE s.pauta.id = :pautaId AND s.status = :status AND s.dataHoraFim > :dataHoraFim")
    Optional<Sessao> buscarSessaoPorPautaStatusDataHoraFimMenorQue(@Param("pautaId") final Long pautaId,
                                                                   @Param("status") final SessaoStatus status,
                                                                   @Param("dataHoraFim") final LocalDateTime dataHoraFim);

    @Query("SELECT s FROM Sessao s WHERE s.status = :status")
    List<Sessao> buscarSessoesComStatus(@Param("status") final SessaoStatus status);

    default Optional<Sessao> buscarSessaoAberta(@Param("pautaId") final Long pautaId) {
        return buscarSessaoPorPautaStatusDataHoraFimMenorQue(pautaId, SessaoStatus.ABERTA, LocalDateTime.now());
    }

    default List<Sessao> buscarSessoesAberta() {
        return buscarSessoesComStatus(SessaoStatus.ABERTA);
    }
}
