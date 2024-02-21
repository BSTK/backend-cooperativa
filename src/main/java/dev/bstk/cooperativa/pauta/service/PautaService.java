package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.model.Sessao;
import dev.bstk.cooperativa.pauta.model.Status;
import dev.bstk.cooperativa.pauta.model.Votacao;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import dev.bstk.cooperativa.pauta.repository.SessaoRepository;
import dev.bstk.cooperativa.pauta.repository.VotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PautaService {

    private static final long TEMPO_DEFAULT_DURACAO_SESSAO_EM_MINUTOS = 1;

    private final PautaRepository pautaRepository;
    private final VotacaoRepository votacaoRepository;
    private final SessaoRepository sessaoRepository;


    public Pauta cadastrarNovaPauta(final Pauta pauta) {
        final boolean existePautaJaCadastrada = pautaRepository.existePautaJaCadastrada(pauta.getTitulo());
        if (existePautaJaCadastrada) {
            throw new IllegalArgumentException(String.format("Pauta [ %s ] já cadastrada!", pauta.getTitulo()));
        }

        return pautaRepository.save(pauta);
    }

    @Transactional
    public Sessao iniciarSessao(final Long pautaId, final Long tempoDuracao) {
        final var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId)));

        final var sessaoVotacaoOptional = sessaoRepository.buscarSessaoPorPauta(pautaId);
        if (sessaoVotacaoOptional.isPresent()) {
            final var statusPauta = sessaoVotacaoOptional.get().getPauta().getStatus();
            throw new IllegalArgumentException(String.format("Está pauta está [ %s ]!", statusPauta));
        }

        final var dataHoraInicio = LocalDateTime.now();
        final var dataHoraFim = Objects.nonNull(tempoDuracao) && tempoDuracao > 0L
                ? dataHoraInicio.plus(tempoDuracao, ChronoUnit.MINUTES)
                : dataHoraInicio.plus(TEMPO_DEFAULT_DURACAO_SESSAO_EM_MINUTOS, ChronoUnit.MINUTES);

        pauta.setStatus(Status.PautaStatus.EM_VOTACAO);
        pautaRepository.saveAndFlush(pauta);

        final var novaSessaoVotacaoIniciada = Sessao.builder()
                .pauta(pauta)
                .status(Status.SessaoStatus.ABERTA)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraFim(dataHoraFim)
                .build();

        return sessaoRepository.save(novaSessaoVotacaoIniciada);
    }

    public void votar(final Long pautaId, final Votacao votacao) {
        final var sessao = sessaoRepository
                .buscarSessaoAberta(pautaId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Não há sessão aberta para está pauta [ id: %s ]!", pautaId)));

        final var associadoJaVotou = votacaoRepository.associadoJaVotou(votacao.getAssociadoId());
        if (associadoJaVotou) {
            throw new IllegalArgumentException("Associado Já votou. Permitido apenas um voto por associado!");
        }

        final var votoComputado = Votacao
                .builder()
                .sessao(sessao)
                .voto(votacao.getVoto())
                .associadoId(votacao.getAssociadoId())
                .build();

        votacaoRepository.saveAndFlush(votoComputado);
    }
}
