package com.example.pruebaauditoriaservice.RestController;

import com.example.pruebaauditoriaservice.JPA.Result;
import com.example.pruebaauditoriaservice.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Result> listaUsuarios() {
        Result result = usuarioService.listaUsuarios();
        return ResponseEntity.ok(result);
    }

}
