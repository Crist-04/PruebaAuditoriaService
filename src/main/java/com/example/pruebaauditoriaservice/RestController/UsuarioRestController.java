package com.example.pruebaauditoriaservice.RestController;

import com.example.pruebaauditoriaservice.JPA.Result;
import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import com.example.pruebaauditoriaservice.Service.UsuarioService;
import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    public ResponseEntity<Result> listaUsuarios(@RequestParam int activo, @RequestParam int idRol, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(usuarioService.listaUsuarios(activo, idRol, page, size));
    }
    
    @PostMapping("/agregarusuario")
    public ResponseEntity<Result> agregarUsuario(@RequestBody UsuarioJPA usuario) {
        Result result = usuarioService.agregarUsuario(usuario);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/actualizarusuario/{idUsuario}")
    public ResponseEntity<Result> actualizarUsuario(@PathVariable int idUsuario, @RequestBody UsuarioJPA usuario) {
        Result result = usuarioService.actualizarUsuario(idUsuario, usuario);
        return ResponseEntity.ok(result);
        
    }
    
    @DeleteMapping("/eliminarusuario/{idUsuario}")
    public ResponseEntity<Result> eliminarUsuario(@PathVariable int idUsuario) {
        Result result = usuarioService.elimimarUsuario(idUsuario);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/exportar/csv")
    public ResponseEntity<InputStreamResource> exportarCSV() {
        ByteArrayInputStream csv = usuarioService.exportarUsuariosCSV();
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=usuarios.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(csv));
    }
    
}
