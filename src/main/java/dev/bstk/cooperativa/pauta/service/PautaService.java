package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Enums.PautaStatus;
import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;


    public Pauta cadastrarNovaPauta(final Pauta pauta) {
        final boolean existePautaJaCadastrada = pautaRepository.existePautaJaCadastrada(pauta.getTitulo());
        if (existePautaJaCadastrada) {
            throw new IllegalArgumentException(String.format("Pauta [ %s ] já cadastrada!", pauta.getTitulo()));
        }

        return pautaRepository.save(pauta);
    }

    public Pauta buscarPautaFinalizada(final Long pautaId) {
        final var pauta = pautaRepository
                .findById(pautaId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Não existe pauta cadastrada [ id: %s ]!", pautaId)));

        if (!PautaStatus.FECHADA.equals(pauta.getStatus())) {
            throw new IllegalArgumentException(String.format("Pauta ainda não está finalizada. Status: [ %s ]!", pauta.getStatus()));
        }

        return pauta;
    }
}
