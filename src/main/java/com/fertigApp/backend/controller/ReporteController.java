package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.reportes.*;
import com.fertigApp.backend.requestModels.RequestDate;
import com.fertigApp.backend.services.CompletadaService;
import com.fertigApp.backend.services.TareaService;
import com.fertigApp.backend.services.TiempoService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ReporteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteController.class);

    private final UsuarioService usuarioService;
    private final TareaService tareaService;
    private final CompletadaService completadaService;
    private final TiempoService tiempoService;

    public ReporteController(UsuarioService usuarioService, TareaService tareaService, CompletadaService completadaService, TiempoService tiempoService) {
        this.usuarioService = usuarioService;
        this.tareaService = tareaService;
        this.completadaService = completadaService;
        this.tiempoService = tiempoService;
    }

    @GetMapping(path="report/month")
    public ResponseEntity<Reporte> getReporteMensual(@RequestBody RequestDate requestDate) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario =usuarioService.findById(userDetails.getUsername());
        ReporteMensualBuilder reporteMensualBuilder =  new ReporteMensualBuilder(this.tareaService, this.completadaService, this.tiempoService);
        return ResponseEntity.ok(reporteMensualBuilder.crearReporte(requestDate.getFecha(),optUsuario.orElse(null)));
    }

    @GetMapping(path="report/week")
    public ResponseEntity<Reporte> getReporteSemanal(@RequestBody RequestDate requestDate) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario =usuarioService.findById(userDetails.getUsername());
        ReporteSemanalBuilder reporteSemanalBuilder =  new ReporteSemanalBuilder(this.tareaService, this.completadaService, this.tiempoService);
        return ResponseEntity.ok(reporteSemanalBuilder.crearReporte(requestDate.getFecha(),optUsuario.orElse(null)));
    }

    @GetMapping(path="report/year")
    public ResponseEntity<Reporte> getReporteAnual(@RequestBody RequestDate requestDate) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());
        ReporteAnualBuilder reporteAnualBuilder =  new ReporteAnualBuilder(this.tareaService, this.completadaService, this.tiempoService);
        return ResponseEntity.ok(reporteAnualBuilder.crearReporte(requestDate.getFecha(),optUsuario.orElse(null)));
    }

    @GetMapping(path="graphic/month")
    public ResponseEntity<Grafica> getGraficaMensual(@RequestBody RequestDate requestDate) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario =usuarioService.findById(userDetails.getUsername());
        GraficaMensualBuilder graficaMensualBuilder =  new GraficaMensualBuilder(this.tareaService, this.completadaService, this.tiempoService);
        return ResponseEntity.ok(graficaMensualBuilder.crearGrafica(requestDate.getFecha(),optUsuario.orElse(null)));
    }

    @GetMapping(path="graphic/week")
    public ResponseEntity<Grafica> getGraficaSemanal(@RequestBody RequestDate requestDate) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario =usuarioService.findById(userDetails.getUsername());
        GraficaSemanalBuilder graficaSemanalBuilder =  new GraficaSemanalBuilder(this.tareaService, this.completadaService, this.tiempoService);
        return ResponseEntity.ok(graficaSemanalBuilder.crearGrafica(requestDate.getFecha(),optUsuario.orElse(null)));
    }

    @GetMapping(path="graphic/year")
    public ResponseEntity<Grafica> getGraficaAnual(@RequestBody RequestDate requestDate) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());
        GraficaAnualBuilder graficaAnualBuilder =  new GraficaAnualBuilder(this.tareaService, this.completadaService, this.tiempoService);
        return ResponseEntity.ok(graficaAnualBuilder.crearGrafica(requestDate.getFecha(),optUsuario.orElse(null)));
    }
}
