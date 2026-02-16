package com.example.pruebaauditoriaservice.Repository;

import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<UsuarioJPA, Integer> {

    UsuarioJPA findByCorreo(String correo);

}
