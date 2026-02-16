package com.example.pruebaauditoriaservice.Repository;

import com.example.pruebaauditoriaservice.JPA.TipoEventoJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoEventoRepository extends JpaRepository<TipoEventoJPA, Integer> {

}
