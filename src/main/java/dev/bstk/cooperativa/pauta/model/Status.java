package dev.bstk.cooperativa.pauta.model;

public interface Status {

    enum PautaStatus {
        ABERTA,
        EM_VOTACAO,
        FECHADA
    }

    enum SessaoStatus {
        ABERTA,
        FECHADA
    }
}
