package dev.bstk.cooperativa.pauta.repository.projections;

import dev.bstk.cooperativa.pauta.model.Enums.PautaResultado;

public interface VotacaoPautaResultado {

    Integer getTotalVotos();
    Integer getTotalVotosSim();
    Integer getTotalVotosNao();

    default PautaResultado resultado() {
        return getTotalVotosSim() > getTotalVotosNao()
                ? PautaResultado.APROVADA
                : PautaResultado.NAO_APROVADA;
    }

    static VotacaoPautaResultado votacaoZerada() {
        return new VotacaoPautaResultado() {
            @Override
            public Integer getTotalVotos() { return 0; }

            @Override
            public Integer getTotalVotosSim() { return 0; }

            @Override
            public Integer getTotalVotosNao() { return 0; }
        };
    }
}
