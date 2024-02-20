package dev.bstk.cooperativa.pauta.service;

import dev.bstk.cooperativa.pauta.model.Pauta;
import dev.bstk.cooperativa.pauta.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
