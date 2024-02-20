package dev.bstk.cooperativa.pauta.repository;

import dev.bstk.cooperativa.pauta.model.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pauta p WHERE lower(p.titulo) = lower(:titulo)")
    boolean existePautaJaCadastrada(@Param("titulo") final String titulo);
}
