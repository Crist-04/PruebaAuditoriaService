package com.example.pruebaauditoriaservice.Service;

import com.example.pruebaauditoriaservice.JPA.EventoAuditoriaJPA;
import com.example.pruebaauditoriaservice.JPA.Result;
import com.example.pruebaauditoriaservice.JPA.TipoEventoJPA;
import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import com.example.pruebaauditoriaservice.Repository.IEventoAuditoriaRepository;
import com.example.pruebaauditoriaservice.Repository.ITipoEventoRepository;
import com.example.pruebaauditoriaservice.Repository.IUsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private IUsuarioRepository iUsuarioRepository;

    @Autowired
    private IEventoAuditoriaRepository iEventoAuditoriaRepository;

    @Autowired
    private ITipoEventoRepository iTipoEventoRepository;

    public UsuarioService(IUsuarioRepository iUsuarioRepository, IEventoAuditoriaRepository iEventoAuditoriaRepository, ITipoEventoRepository iTipoEventoRepository) {
        this.iUsuarioRepository = iUsuarioRepository;
        this.iEventoAuditoriaRepository = iEventoAuditoriaRepository;
        this.iEventoAuditoriaRepository = iEventoAuditoriaRepository;
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
            registrarAuditoria(1, usuario, "Se creó el usuario: " + usuario.getIdUsuario());

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

            if (usuarioActualizado.getRol() != null) {
                usuario.setRol(usuarioActualizado.getRol());
            }

            if (usuarioActualizado.getPassword() != null) {
                usuario.setPassword(usuarioActualizado.getPassword());
            }
            iUsuarioRepository.save(usuario);
            registrarAuditoria(2, usuario, "Se actualizo el usuario: " + usuario.getIdUsuario());

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
            registrarAuditoria(3, usuario, "Se desactivo al usuario: " + usuario.getIdUsuario());

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

    private void registrarAuditoria(int idTipoEvento, UsuarioJPA usuario, String descripcion) {
        TipoEventoJPA tipoEvento = iTipoEventoRepository.findById(idTipoEvento).orElse(null);

        EventoAuditoriaJPA eventoAuditoria = new EventoAuditoriaJPA();
        eventoAuditoria.setTipoEvento(tipoEvento);
        eventoAuditoria.setUsuario(usuario);
        eventoAuditoria.setTiempoEvento(LocalDateTime.now());
        eventoAuditoria.setDescripcion(descripcion);

        iEventoAuditoriaRepository.save(eventoAuditoria);

    }

}
