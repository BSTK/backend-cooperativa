package dev.bstk.cooperativa.pauta.model;

public interface Status {

    enum PautaStatus {
        CRIADA,
        EM_VOTACAO,
        ENCERRADA
    }

    enum SessaoVotacaoStatus {
        INICIADA,
        ENCERRADA
    }
}
