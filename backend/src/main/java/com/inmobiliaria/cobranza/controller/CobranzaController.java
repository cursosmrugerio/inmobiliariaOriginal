package com.inmobiliaria.cobranza.controller;

import com.inmobiliaria.cobranza.dto.*;
import com.inmobiliaria.cobranza.service.CobranzaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cobranza")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE')")
public class CobranzaController {

    private final CobranzaService cobranzaService;

    // ========== CARTERA VENCIDA ==========

    @GetMapping("/cartera")
    public ResponseEntity<List<CarteraVencidaDTO>> getAllCarteraVencida(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(cobranzaService.getAllCarteraVencida(activeOnly));
    }

    @GetMapping("/cartera/{id}")
    public ResponseEntity<CarteraVencidaDTO> getCarteraVencidaById(@PathVariable Long id) {
        return ResponseEntity.ok(cobranzaService.getCarteraVencidaById(id));
    }

    @PostMapping("/cartera")
    public ResponseEntity<CarteraVencidaDTO> createCarteraVencida(
            @Valid @RequestBody CreateCarteraVencidaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cobranzaService.createCarteraVencida(request));
    }

    @PatchMapping("/cartera/{id}/estado")
    public ResponseEntity<CarteraVencidaDTO> updateEstadoCobranza(
            @PathVariable Long id,
            @RequestParam String estadoCobranza) {
        return ResponseEntity.ok(cobranzaService.updateEstadoCobranza(id, estadoCobranza));
    }

    @PostMapping("/cartera/{id}/pago")
    public ResponseEntity<CarteraVencidaDTO> registrarPago(
            @PathVariable Long id,
            @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(cobranzaService.registrarPago(id, monto));
    }

    @PostMapping("/cartera/{id}/penalidad")
    public ResponseEntity<CarteraVencidaDTO> calcularPenalidad(@PathVariable Long id) {
        return ResponseEntity.ok(cobranzaService.calcularPenalidad(id));
    }

    @DeleteMapping("/cartera/{id}")
    public ResponseEntity<Void> deleteCarteraVencida(@PathVariable Long id) {
        cobranzaService.deleteCarteraVencida(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cartera/persona/{personaId}")
    public ResponseEntity<List<CarteraVencidaDTO>> getCarteraByPersona(@PathVariable Long personaId) {
        return ResponseEntity.ok(cobranzaService.getCarteraByPersona(personaId));
    }

    @GetMapping("/cartera/propiedad/{propiedadId}")
    public ResponseEntity<List<CarteraVencidaDTO>> getCarteraByPropiedad(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(cobranzaService.getCarteraByPropiedad(propiedadId));
    }

    @GetMapping("/cartera/estado/{estado}")
    public ResponseEntity<List<CarteraVencidaDTO>> getCarteraByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(cobranzaService.getCarteraByEstado(estado));
    }

    @GetMapping("/cartera/clasificacion/{clasificacion}")
    public ResponseEntity<List<CarteraVencidaDTO>> getCarteraByClasificacion(@PathVariable String clasificacion) {
        return ResponseEntity.ok(cobranzaService.getCarteraByClasificacion(clasificacion));
    }

    // ========== RESUMEN ==========

    @GetMapping("/resumen")
    public ResponseEntity<ResumenCobranzaDTO> getResumenCobranza() {
        return ResponseEntity.ok(cobranzaService.getResumenCobranza());
    }

    // ========== SEGUIMIENTO ==========

    @GetMapping("/seguimiento/cartera/{carteraVencidaId}")
    public ResponseEntity<List<SeguimientoCobranzaDTO>> getSeguimientoByCartera(
            @PathVariable Long carteraVencidaId) {
        return ResponseEntity.ok(cobranzaService.getSeguimientoByCartera(carteraVencidaId));
    }

    @PostMapping("/seguimiento")
    public ResponseEntity<SeguimientoCobranzaDTO> createSeguimiento(
            @Valid @RequestBody CreateSeguimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cobranzaService.createSeguimiento(request));
    }

    @GetMapping("/seguimiento/pendientes")
    public ResponseEntity<List<SeguimientoCobranzaDTO>> getAccionesPendientes() {
        return ResponseEntity.ok(cobranzaService.getAccionesPendientes());
    }

    // ========== PROYECCIÓN ==========

    @GetMapping("/proyeccion")
    public ResponseEntity<List<ProyeccionCobranzaDTO>> getProyecciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFin) {
        return ResponseEntity.ok(cobranzaService.getProyecciones(periodoInicio, periodoFin));
    }

    @PostMapping("/proyeccion")
    public ResponseEntity<ProyeccionCobranzaDTO> createOrUpdateProyeccion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo,
            @RequestParam BigDecimal montoProyectado,
            @RequestParam Integer cantidadContratos,
            @RequestParam Integer cantidadPagosEsperados) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cobranzaService.createOrUpdateProyeccion(periodo, montoProyectado, cantidadContratos, cantidadPagosEsperados));
    }

    @PatchMapping("/proyeccion/cobrado")
    public ResponseEntity<ProyeccionCobranzaDTO> actualizarProyeccionCobrado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo,
            @RequestParam BigDecimal montoCobrado,
            @RequestParam Integer pagosRecibidos) {
        return ResponseEntity.ok(cobranzaService.actualizarProyeccionCobrado(periodo, montoCobrado, pagosRecibidos));
    }
}
