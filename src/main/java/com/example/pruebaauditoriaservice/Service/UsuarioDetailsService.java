package com.example.pruebaauditoriaservice.Service;

import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import com.example.pruebaauditoriaservice.Repository.IUsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final IUsuarioRepository iUsuarioRepository;

    public UsuarioDetailsService(IUsuarioRepository iUsuarioRepository) {
        this.iUsuarioRepository = iUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        UsuarioJPA usuario = iUsuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
        }

        if (usuario.getActivo() != 1) {
            throw new UsernameNotFoundException("Usuario inactivo: " + correo);
        }

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getPassword())
                //.passwordEncoder(encoder)
                .roles(usuario.getRol().getNombreRol().toUpperCase())
                .build();
    }
}
