package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.model.SessaoVotacao;
import dev.bstk.cooperativa.pauta.model.Status;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import dev.bstk.cooperativa.pauta.repository.SessaoVotacaoRepository;
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
    private final SessaoVotacaoRepository sessaoVotacaoRepository;


    public Pauta cadastrarNovaPauta(final Pauta pauta) {
        final boolean existePautaJaCadastrada = pautaRepository.existePautaJaCadastrada(pauta.getTitulo());
        if (existePautaJaCadastrada) {
            throw new IllegalArgumentException(String.format("Pauta [ %s ] já cadastrada!", pauta.getTitulo()));
        }

        return pautaRepository.save(pauta);
    }

    @Transactional
    public SessaoVotacao iniciarSessaoVotacao(final Long pautaId, final Long tempoDuracao) {
        final var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId)));

        if (pauta.estaEncerrada()) {
            throw new IllegalArgumentException(String.format("Está pauta já está encerrada [ id: %s ]!", pautaId));
        }

        final var pautaEstaEmVotacao = sessaoVotacaoRepository.pautaEstaEmVotacao(pautaId);
        if (pautaEstaEmVotacao) {
            throw new IllegalArgumentException(String.format("Está pauta já está em votação [ id: %s ]!", pautaId));
        }

        final var dataHoraInicio = LocalDateTime.now();
        final var dataHoraTermino = Objects.nonNull(tempoDuracao) && tempoDuracao > 0L
                ? dataHoraInicio.plus(tempoDuracao, ChronoUnit.MINUTES)
                : dataHoraInicio.plus(TEMPO_DEFAULT_DURACAO_SESSAO_EM_MINUTOS, ChronoUnit.MINUTES);

        pauta.setStatus(Status.PautaStatus.EM_VOTACAO);
        pautaRepository.saveAndFlush(pauta);

        final var novaSessaoVotacaoIniciada = SessaoVotacao.builder()
                .pauta(pauta)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraTermino(dataHoraTermino)
                .build();

        return sessaoVotacaoRepository.save(novaSessaoVotacaoIniciada);
    }
}
