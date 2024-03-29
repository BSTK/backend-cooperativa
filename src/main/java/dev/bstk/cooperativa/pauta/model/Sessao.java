package dev.bstk.cooperativa.pauta.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SESSAO")
public class Sessao implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "PAUTA_ID", referencedColumnName = "ID")
    private Pauta pauta;

    @NotNull
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Enums.SessaoStatus status;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATA_HORA_INICIO")
    private LocalDateTime dataHoraInicio;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATA_HORA_FIM")
    private LocalDateTime dataHoraFim;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sessao that = (Sessao) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
