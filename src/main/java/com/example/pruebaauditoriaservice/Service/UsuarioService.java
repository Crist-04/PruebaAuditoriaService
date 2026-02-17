package com.example.pruebaauditoriaservice.Service;

import com.example.pruebaauditoriaservice.JPA.Result;
import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import com.example.pruebaauditoriaservice.Repository.IUsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final IUsuarioRepository iUsuarioRepository;

    public UsuarioService(IUsuarioRepository iUsuarioRepository) {
        this.iUsuarioRepository = iUsuarioRepository;
    }

    public Result listaUsuarios() {
        Result result = new Result();
        try {
            result.objects = (List<Object>) (List<?>) iUsuarioRepository.findAll();
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getMessage();
        }
        return result;
    }

    public Result agregarUsuario(UsuarioJPA usuario) {
        Result result = new Result();
        try {
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setActivo(1);

            iUsuarioRepository.save(usuario);
            result.correct = true;
            result.object = usuario;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getMessage();
        }
        return result;
    }

    public Result actualizarUsuario(int idUsuario, UsuarioJPA usuarioActualizado) {
        Result result = new Result();
        try {
            UsuarioJPA usuario = iUsuarioRepository.findById(idUsuario).orElse(null);
            if (usuario == null) {
                result.correct = false;
                result.errorMessage = "USsuario no encontrado";
                return result;
            }
            if (usuarioActualizado.getNombre() != null) {
                usuario.setNombre(usuarioActualizado.getNombre());
            }

            if (usuarioActualizado.getCorreo() != null) {
                usuario.setCorreo(usuarioActualizado.getCorreo());
            }

            usuario.setActivo(usuarioActualizado.getActivo());

            if (usuario.getRol() != null) {
                usuario.setRol(usuarioActualizado.getRol());
            }

            if (usuarioActualizado.getPassword() != null) {
                usuario.setPassword(usuarioActualizado.getPassword());
            }
            iUsuarioRepository.save(usuario);
            result.correct = true;
            result.object = usuario;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getMessage();
        }
        return result;
    }

    public Result elimimarUsuario(int idUsuario) {
        Result result = new Result();
        try {
            UsuarioJPA usuario = iUsuarioRepository.findById(idUsuario).orElse(null);

            if (usuario == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
                return result;
            }

            usuario.setActivo(0);
            iUsuarioRepository.save(usuario);

            result.correct = true;
            result.errorMessage = "Usuario Eliminado";
            result.status = 200;
            result.object = usuario;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.status = 500;
            return result;
        }
        return result;
    }

}
