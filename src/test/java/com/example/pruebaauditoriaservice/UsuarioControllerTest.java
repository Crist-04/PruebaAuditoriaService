package com.example.pruebaauditoriaservice;

import com.example.pruebaauditoriaservice.DTO.LoginRequest;
import com.example.pruebaauditoriaservice.JPA.Result;
import com.example.pruebaauditoriaservice.JPA.RolJPA;
import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import com.example.pruebaauditoriaservice.Repository.IUsuarioRepository;
import com.example.pruebaauditoriaservice.RestController.UsuarioRestController;
import com.example.pruebaauditoriaservice.Service.UsuarioService;
import com.example.pruebaauditoriaservice.Util.JwtUtil;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private IUsuarioRepository iUsuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    private UsuarioJPA usuarioPrueba;
    private RolJPA rolAdmin;
    private Result resultSuccess;

    @BeforeEach
    void setUp() {
        rolAdmin = new RolJPA();
        rolAdmin.setIdRol(1);
        rolAdmin.setNombreRol("Administrador");

        usuarioPrueba = new UsuarioJPA();
        usuarioPrueba.setIdUsuario(1);
        usuarioPrueba.setNombre("Cristobal");
        usuarioPrueba.setCorreo("cris@email.com");
        usuarioPrueba.setPassword("criss123");
        usuarioPrueba.setActivo(1);
        usuarioPrueba.setRol(rolAdmin);
        usuarioPrueba.setFechaRegistro(LocalDateTime.now());

        resultSuccess = new Result();
        resultSuccess.correct = true;
        resultSuccess.object = usuarioPrueba;
    }

    @Test
    void login_credencialesValidas_debeRetornarToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("cris@email.com");
        loginRequest.setPassword("criss123");

        String tokenEsperado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        when(iUsuarioRepository.findByCorreo("cris@email.com"))
                .thenReturn(usuarioPrueba);
        when(jwtUtil.generateToken("cris@email.com", "Administrador"))
                .thenReturn(tokenEsperado);

        ResponseEntity<?> response = usuarioRestController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tokenEsperado, response.getBody());

        verify(iUsuarioRepository, times(1)).findByCorreo("cris@email.com");
        verify(jwtUtil, times(1)).generateToken("cris@email.com", "Administrador");
    }

    @Test
    void login_usuarioNoExiste_debeRetornar401() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("noexiste@email.com");
        loginRequest.setPassword("password");

        when(iUsuarioRepository.findByCorreo("noexiste@email.com"))
                .thenReturn(null);

        ResponseEntity<?> response = usuarioRestController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales invalidas", response.getBody());

        verify(iUsuarioRepository, times(1)).findByCorreo("noexiste@email.com");
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_passwordIncorrecto_debeRetornar401() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("cris@email.com");
        loginRequest.setPassword("passwordIncorrecto");

        when(iUsuarioRepository.findByCorreo("cris@email.com"))
                .thenReturn(usuarioPrueba);

        ResponseEntity<?> response = usuarioRestController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales invalidas", response.getBody());

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void listaUsuarios_debeRetornarListaPaginada() {
        Result result = new Result();
        result.correct = true;
        result.objects = Arrays.asList(usuarioPrueba);
        result.object = 1L; // total elementos

        when(usuarioService.listaUsuarios(1, 1, 0, 5))
                .thenReturn(result);

        ResponseEntity<Result> response = usuarioRestController.listaUsuarios(1, 1, 0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().correct);
        assertEquals(1, response.getBody().objects.size());
        assertEquals(1L, response.getBody().object);

        verify(usuarioService, times(1)).listaUsuarios(1, 1, 0, 5);
    }

    @Test
    void listaUsuarios_conDiferentesFiltros_debeInvocarServicio() {
        Result result = new Result();
        result.correct = true;

        when(usuarioService.listaUsuarios(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(result);

        usuarioRestController.listaUsuarios(0, 2, 0, 10);

        verify(usuarioService, times(1)).listaUsuarios(0, 2, 0, 10);
    }

    @Test
    void agregarUsuario_usuarioValido_debeRetornarOk() {
        when(usuarioService.agregarUsuario(any(UsuarioJPA.class)))
                .thenReturn(resultSuccess);

        ResponseEntity<Result> response = usuarioRestController.agregarUsuario(usuarioPrueba);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().correct);
        assertNotNull(response.getBody().object);

        verify(usuarioService, times(1)).agregarUsuario(usuarioPrueba);
    }

    @Test
    void agregarUsuario_conError_debeRetornarResultConError() {
        Result resultError = new Result();
        resultError.correct = false;
        resultError.errorMessage = "Error al crear usuario";

        when(usuarioService.agregarUsuario(any(UsuarioJPA.class)))
                .thenReturn(resultError);

        ResponseEntity<Result> response = usuarioRestController.agregarUsuario(usuarioPrueba);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().correct);
        assertEquals("Error al crear usuario", response.getBody().errorMessage);
    }

    @Test
    void actualizarUsuario_usuarioExiste_debeRetornarOk() {
        when(usuarioService.actualizarUsuario(eq(1), any(UsuarioJPA.class)))
                .thenReturn(resultSuccess);

        ResponseEntity<Result> response = usuarioRestController.actualizarUsuario(1, usuarioPrueba);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().correct);

        verify(usuarioService, times(1)).actualizarUsuario(1, usuarioPrueba);
    }

    @Test
    void actualizarUsuario_usuarioNoExiste_debeRetornarError() {
        Result resultError = new Result();
        resultError.correct = false;
        resultError.errorMessage = "USsuario no encontrado";

        when(usuarioService.actualizarUsuario(eq(99), any(UsuarioJPA.class)))
                .thenReturn(resultError);

        ResponseEntity<Result> response = usuarioRestController.actualizarUsuario(99, usuarioPrueba);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().correct);
        assertEquals("USsuario no encontrado", response.getBody().errorMessage);
    }

    @Test
    void eliminarUsuario_usuarioExiste_debeRetornarOk() {
        Result result = new Result();
        result.correct = true;
        result.status = 200;
        result.errorMessage = "Usuario Eliminado";

        when(usuarioService.elimimarUsuario(1))
                .thenReturn(result);

        ResponseEntity<Result> response = usuarioRestController.eliminarUsuario(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().correct);
        assertEquals(200, response.getBody().status);

        verify(usuarioService, times(1)).elimimarUsuario(1);
    }

    @Test
    void eliminarUsuario_usuarioNoExiste_debeRetornarError() {
        Result resultError = new Result();
        resultError.correct = false;
        resultError.errorMessage = "Usuario no encontrado";

        when(usuarioService.elimimarUsuario(99))
                .thenReturn(resultError);

        ResponseEntity<Result> response = usuarioRestController.eliminarUsuario(99);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().correct);
        assertEquals("Usuario no encontrado", response.getBody().errorMessage);
    }

    @Test
    void exportarCSV_debeRetornarArchivoCSV() {
        String csvContent = "ID,Nombre,Correo\n1,Cristobal,cris@email.com";
        ByteArrayInputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(usuarioService.exportarUsuariosCSV())
                .thenReturn(csvStream);

        ResponseEntity<InputStreamResource> response = usuarioRestController.exportarCSV();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=usuarios.csv",
                response.getHeaders().getFirst("Content-Disposition"));

        verify(usuarioService, times(1)).exportarUsuariosCSV();
    }

    @Test
    void exportarCSV_contentType_debeSerTextCSV() {
        ByteArrayInputStream csvStream = new ByteArrayInputStream("test".getBytes());
        when(usuarioService.exportarUsuariosCSV()).thenReturn(csvStream);

        ResponseEntity<InputStreamResource> response = usuarioRestController.exportarCSV();

        assertEquals("text/csv", response.getHeaders().getContentType().toString());
    }

    @Test
    void exportarPDF_debeRetornarArchivoPDF() {
        byte[] pdfContent = new byte[]{37, 80, 68, 70}; // %PDF header simulado
        ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdfContent);

        when(usuarioService.exportarUsuarioPDF())
                .thenReturn(pdfStream);

        ResponseEntity<InputStreamResource> response = usuarioRestController.exportarPDF();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=usuarios.pdf",
                response.getHeaders().getFirst("Content-Disposition"));

        verify(usuarioService, times(1)).exportarUsuarioPDF();
    }

    @Test
    void exportarPDF_contentType_debeSerApplicationPDF() {
        ByteArrayInputStream pdfStream = new ByteArrayInputStream("test".getBytes());
        when(usuarioService.exportarUsuarioPDF()).thenReturn(pdfStream);

        ResponseEntity<InputStreamResource> response = usuarioRestController.exportarPDF();

        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
    }

    @Test
    void todosLosMetodos_debenInvocarServicioCorrectamente() {
        Result mockResult = new Result();
        mockResult.correct = true;

        when(usuarioService.listaUsuarios(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(mockResult);
        when(usuarioService.agregarUsuario(any())).thenReturn(mockResult);
        when(usuarioService.actualizarUsuario(anyInt(), any())).thenReturn(mockResult);
        when(usuarioService.elimimarUsuario(anyInt())).thenReturn(mockResult);

        usuarioRestController.listaUsuarios(1, 1, 0, 5);
        usuarioRestController.agregarUsuario(usuarioPrueba);
        usuarioRestController.actualizarUsuario(1, usuarioPrueba);
        usuarioRestController.eliminarUsuario(1);

        verify(usuarioService, times(1)).listaUsuarios(1, 1, 0, 5);
        verify(usuarioService, times(1)).agregarUsuario(usuarioPrueba);
        verify(usuarioService, times(1)).actualizarUsuario(1, usuarioPrueba);
        verify(usuarioService, times(1)).elimimarUsuario(1);
    }
}
