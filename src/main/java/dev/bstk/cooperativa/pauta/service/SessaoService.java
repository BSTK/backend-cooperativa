package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.handlerexception.exception.NaoEncontradoException;
import dev.bstk.cooperativa.pauta.handlerexception.exception.PautaInvalidaException;
import dev.bstk.cooperativa.pauta.handlerexception.exception.VotoInvalidoException;
import dev.bstk.cooperativa.pauta.infra.Evento;
import dev.bstk.cooperativa.pauta.infra.NotificarEventoMq;
import dev.bstk.cooperativa.pauta.model.Enums.PautaStatus;
import dev.bstk.cooperativa.pauta.model.Enums.SessaoStatus;
import dev.bstk.cooperativa.pauta.model.Sessao;
import dev.bstk.cooperativa.pauta.model.Votacao;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import dev.bstk.cooperativa.pauta.repository.SessaoRepository;
import dev.bstk.cooperativa.pauta.repository.VotacaoRepository;
import dev.bstk.cooperativa.pauta.repository.projections.VotacaoPautaResultado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessaoService {

    private static final long TEMPO_DEFAULT_DURACAO_SESSAO_EM_MINUTOS = 1;

    private final PautaRepository pautaRepository;
    private final SessaoRepository sessaoRepository;
    private final VotacaoRepository votacaoRepository;
    private final NotificarEventoMq notificarEventoMq;

    @Transactional
    public Sessao iniciarSessao(final Long pautaId, final Long tempoDuracao) {
        final var pauta = pautaRepository
                .findById(pautaId)
                .orElseThrow(() -> {
                    log.warn(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId));
                    throw new NaoEncontradoException(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId));
                });

        final var sessaoVotacaoOptional = sessaoRepository.buscarSessaoPorPauta(pautaId);
        if (sessaoVotacaoOptional.isPresent()) {
            final var statusPauta = sessaoVotacaoOptional.get().getPauta().getStatus();
            log.warn(String.format("Está pauta está [ %s ]!", statusPauta));
            throw new PautaInvalidaException(String.format("Está pauta está [ %s ]!", statusPauta));
        }

        final var dataHoraInicio = LocalDateTime.now();
        final var dataHoraFim = Objects.nonNull(tempoDuracao) && tempoDuracao > 0L
                ? dataHoraInicio.plus(tempoDuracao, ChronoUnit.MINUTES)
                : dataHoraInicio.plus(TEMPO_DEFAULT_DURACAO_SESSAO_EM_MINUTOS, ChronoUnit.MINUTES);

        pauta.setStatus(PautaStatus.EM_VOTACAO);

        final var novaSessaoVotacaoIniciada = Sessao.builder()
                .pauta(pauta)
                .status(SessaoStatus.ABERTA)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraFim(dataHoraFim)
                .build();

        final var novaSessaoVotacaoIniciadaSalva = sessaoRepository.save(novaSessaoVotacaoIniciada);

        log.info("Sessão iniciada. Pauta: [ {} ], Inicio: [ {} ], Fim: [ {} ]",
            novaSessaoVotacaoIniciadaSalva.getPauta().getTitulo(),
            novaSessaoVotacaoIniciadaSalva.getDataHoraInicio(),
            novaSessaoVotacaoIniciadaSalva.getDataHoraFim()
        );

        return novaSessaoVotacaoIniciadaSalva;
    }

    @Transactional
    public void finalizarSessao() {
        final var sessoes = sessaoRepository.buscarSessoesAberta();
        if (sessoes.isEmpty()) {
            log.info("Não há sessões para ser finalizadas.");
            return;
        }

        for (Sessao sessao : sessoes) {
            final var deveFecharSessao = sessao.getDataHoraFim().isBefore(LocalDateTime.now());
            if (deveFecharSessao) {
                var resultadoVotacao = votacaoRepository.contabilizarResultado(sessao.getId());
                if (Objects.isNull(resultadoVotacao)) {
                    log.warn("Sessão finalizada com votação zerada!");
                    resultadoVotacao = VotacaoPautaResultado.votacaoZerada();
                }

                final var pauta = sessao.getPauta();
                pauta.setStatus(PautaStatus.FECHADA);
                pauta.setResultado(resultadoVotacao.resultado());
                pauta.setTotalVotos(resultadoVotacao.getTotalVotos());
                pauta.setTotalVotosSim(resultadoVotacao.getTotalVotosSim());
                pauta.setTotalVotosNao(resultadoVotacao.getTotalVotosNao());

                sessao.setStatus(SessaoStatus.FECHADA);
                sessaoRepository.saveAndFlush(sessao);
                log.info("Sessão finalizada. Pauta: [ {} ], Inicio: [ {} ], Fim: [ {} ]",
                   sessao.getPauta().getTitulo(),
                   sessao.getDataHoraInicio(),
                   sessao.getDataHoraFim()
                );

                final var evento = Evento.builder()
                        .data(Map.of("sessao", sessao))
                        .build();

                notificarEventoMq.notificarEvento(evento);
            }
        }
    }

    public void votar(final Long pautaId, final Votacao votacao) {
        final var associadoJaVotou = votacaoRepository.associadoJaVotou(pautaId, votacao.getAssociadoId());
        if (associadoJaVotou) {
            log.warn("Associado Já votou. Permitido apenas um voto por associado!");
            throw new VotoInvalidoException("Associado Já votou. Permitido apenas um voto por associado!");
        }

        final var sessao = sessaoRepository
                .buscarSessaoAberta(pautaId)
                .orElseThrow(() -> {
                    log.warn(String.format("Não há sessão aberta para está pauta [ id: %s ]!", pautaId));
                    throw new NaoEncontradoException(String.format("Não há sessão aberta para está pauta [ id: %s ]!", pautaId));
                });

        final var votoComputado = Votacao
                .builder()
                .sessao(sessao)
                .voto(votacao.getVoto())
                .associadoId(votacao.getAssociadoId())
                .build();

        votacaoRepository.saveAndFlush(votoComputado);
    }
}
