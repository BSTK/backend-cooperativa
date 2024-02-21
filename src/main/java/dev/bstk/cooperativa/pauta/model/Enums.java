package dev.bstk.cooperativa.pauta.model;

public interface Enums {

    enum PautaStatus {
        ABERTA,
        EM_VOTACAO,
        FECHADA
    }

    enum PautaResultado {
        APROVADA,
        NAO_APROVADA
    }

    enum SessaoStatus {
        ABERTA,
        FECHADA
    }
}
