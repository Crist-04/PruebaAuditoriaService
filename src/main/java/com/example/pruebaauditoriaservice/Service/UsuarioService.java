package com.example.pruebaauditoriaservice.Service;

import com.example.pruebaauditoriaservice.JPA.EventoAuditoriaJPA;
import com.example.pruebaauditoriaservice.JPA.Result;
import com.example.pruebaauditoriaservice.JPA.TipoEventoJPA;
import com.example.pruebaauditoriaservice.JPA.UsuarioJPA;
import com.example.pruebaauditoriaservice.Repository.IEventoAuditoriaRepository;
import com.example.pruebaauditoriaservice.Repository.ITipoEventoRepository;
import com.example.pruebaauditoriaservice.Repository.IUsuarioRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Result listaUsuarios(int activo, int idRol, int page, int size) {
        Result result = new Result();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UsuarioJPA> usuarios = iUsuarioRepository.findByActivoAndRolIdRol(activo, idRol, pageable);
            result.correct = true;
            result.objects = (List<Object>) (List<?>) usuarios.getContent();
            result.object = usuarios.getTotalElements();
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

    public ByteArrayInputStream exportarUsuariosCSV() {
        List<UsuarioJPA> usuarios = iUsuarioRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            String[] header = {
                "ID", "Nombre", "Correo", "Activo", "Rol", "FechaRegistro", "Descripcion"
            };
            writer.writeNext(header);

            for (UsuarioJPA usuario : usuarios) {

                String estado = (usuario.getActivo() == 1) ? "Activo" : "Desactivado";
                EventoAuditoriaJPA ultimoEvento = iEventoAuditoriaRepository.findTopByUsuarioOrderByTiempoEventoDesc(usuario);

                String descripcion = (ultimoEvento != null) ? ultimoEvento.getDescripcion() : "Sin eventos";
                String[] data = {
                    String.valueOf(usuario.getIdUsuario()),
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    estado,
                    usuario.getRol().getNombreRol(),
                    String.valueOf(usuario.getFechaRegistro()),
                    descripcion
                };

                writer.writeNext(data);
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            throw new RuntimeException("Error al exportar el csv");
        }
    }

    public ByteArrayInputStream exportarUsuarioPDF() {
        List<UsuarioJPA> usuarios = iUsuarioRepository.findAll();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.LETTER.rotate());

            document.add(new Paragraph("Reporte Usuarios")
                    .setBold()
                    .setFontSize(18)
            );

            Table table = new Table(7);
            table.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.setBorder(new SolidBorder(ColorConstants.RED,2));
            table.addHeaderCell("ID");
            table.addHeaderCell("Nombre");
            table.addHeaderCell("Correo");
            table.addHeaderCell("Activo");
            table.addHeaderCell("Rol");
            table.addHeaderCell("Fecha Registro");
            table.addHeaderCell("Descripcion");

            for (UsuarioJPA usuario : usuarios) {

                String estado = (usuario.getActivo() == 1) ? "Activo" : "Desactivado";

                EventoAuditoriaJPA ultimoEvento = iEventoAuditoriaRepository.findTopByUsuarioOrderByTiempoEventoDesc(usuario);

                String descripcion = (ultimoEvento != null) ? ultimoEvento.getDescripcion() : "Sin eventos";

                table.addCell(String.valueOf(usuario.getIdUsuario()));
                table.addCell(usuario.getNombre());
                table.addCell(usuario.getCorreo());
                table.addCell(estado);
                table.addCell(usuario.getRol().getNombreRol());
                table.addCell(String.valueOf(usuario.getFechaRegistro()));
                table.addCell(descripcion);
            }

            document.add(table);
            document.close();;

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            throw new RuntimeException("Error al exportar el pdf");
        }
    }

}
