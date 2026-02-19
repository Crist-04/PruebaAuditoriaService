package com.example.pruebaauditoriaservice.Repository;

import com.example.pruebaauditoriaservice.JPA.EventoAuditoriaJPA;
import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEventoAuditoriaRepository extends JpaRepository<EventoAuditoriaJPA, Integer> {
    
    EventoAuditoriaJPA findTopByUsuarioOrderByTiempoEventoDesc(UsuarioJPA usuario);


}
