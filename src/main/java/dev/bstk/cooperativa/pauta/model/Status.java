package dev.bstk.cooperativa.pauta.model;

public abstract class Status {

    public enum PautaStatus {
        CRIADA,
        EM_VOTACAO,
        ENCERRADA
    }

    public enum SessaoVotacaoStatus {
        INICIADA,
        ENCERRADA
    }
}
