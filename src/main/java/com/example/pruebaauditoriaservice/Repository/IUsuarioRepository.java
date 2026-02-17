package com.example.pruebaauditoriaservice.Repository;

import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<UsuarioJPA, Integer> {

    UsuarioJPA findByCorreo(String correo);

    Page<UsuarioJPA> findByActivoAndRolIdRol(int activo, int idRol, Pageable pageable);

}
