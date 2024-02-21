package dev.bstk.cooperativa.pauta.model;

import dev.bstk.cooperativa.pauta.model.Enums.PautaResultado;
import dev.bstk.cooperativa.pauta.model.Enums.PautaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PAUTA")
public class Pauta implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "TITULO", unique = true)
    private String titulo;

    @NotNull
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private PautaStatus status;

    @Column(name = "RESULTADO")
    @Enumerated(EnumType.STRING)
    private PautaResultado resultado;

    @Column(name = "TOTAL_VOTOS")
    private Integer totalVotos;

    @Column(name = "TOTAL_VOTOS_SIM")
    private Integer totalVotosSim;

    @Column(name = "TOTAL_VOTOS_NAO")
    private Integer totalVotosNao;

    @PrePersist
    private void prePersiste() {
        setStatus(PautaStatus.ABERTA);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pauta pauta = (Pauta) o;
        return id.equals(pauta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
