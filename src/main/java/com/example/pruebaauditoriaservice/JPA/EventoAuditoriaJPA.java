package com.example.pruebaauditoriaservice.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTOAUDITORIA")
public class EventoAuditoriaJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ideventoauditoria")
    private int idEventoAuditoria;

    @ManyToOne
    @JoinColumn(name = "idtipoevento")
    private TipoEventoJPA tipoEvento;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private UsuarioJPA usuario;

    @Column(name = "tiempoevento")
    private LocalDateTime tiempoEvento;

    @Column(name = "descripcion")
    private String Descripcion;

    public int getIdEventoAuditoria() {
        return idEventoAuditoria;
    }

    public void setIdEventoAuditoria(int idEventoAuditoria) {
        this.idEventoAuditoria = idEventoAuditoria;
    }

    public TipoEventoJPA getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoJPA tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public UsuarioJPA getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioJPA usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getTiempoEvento() {
        return tiempoEvento;
    }

    public void setTiempoEvento(LocalDateTime tiempoEvento) {
        this.tiempoEvento = tiempoEvento;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

}
