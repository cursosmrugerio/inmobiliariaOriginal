package com.inmobiliaria.pago;

import com.inmobiliaria.pago.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    // ==================== PAGOS ====================

    @GetMapping
    public ResponseEntity<List<PagoDTO>> getAllPagos() {
        return ResponseEntity.ok(pagoService.getAllPagos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> getPagoById(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.getPagoById(id));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<PagoDTO>> getPagosByContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(pagoService.getPagosByContrato(contratoId));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<PagoDTO>> getPagosByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(pagoService.getPagosByPeriodo(fechaInicio, fechaFin));
    }

    @PostMapping
    public ResponseEntity<PagoDTO> createPago(@Valid @RequestBody CreatePagoRequest request) {
        return new ResponseEntity<>(pagoService.createPago(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/aplicar")
    public ResponseEntity<PagoDTO> aplicarPago(
            @PathVariable Long id,
            @Valid @RequestBody AplicarPagoRequest request) {
        return ResponseEntity.ok(pagoService.aplicarPago(id, request));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPago(@PathVariable Long id) {
        pagoService.cancelarPago(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CARGOS ====================

    @GetMapping("/cargos")
    public ResponseEntity<List<CargoDTO>> getAllCargos() {
        return ResponseEntity.ok(pagoService.getAllCargos());
    }

    @GetMapping("/cargos/{id}")
    public ResponseEntity<CargoDTO> getCargoById(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.getCargoById(id));
    }

    @GetMapping("/cargos/contrato/{contratoId}")
    public ResponseEntity<List<CargoDTO>> getCargosByContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(pagoService.getCargosByContrato(contratoId));
    }

    @GetMapping("/cargos/pendientes")
    public ResponseEntity<List<CargoDTO>> getCargosPendientes() {
        return ResponseEntity.ok(pagoService.getCargosPendientes());
    }

    @GetMapping("/cargos/vencidos")
    public ResponseEntity<List<CargoDTO>> getCargosVencidos() {
        return ResponseEntity.ok(pagoService.getCargosVencidos());
    }

    @PostMapping("/cargos")
    public ResponseEntity<CargoDTO> createCargo(@Valid @RequestBody CreateCargoRequest request) {
        return new ResponseEntity<>(pagoService.createCargo(request), HttpStatus.CREATED);
    }

    @PostMapping("/cargos/{id}/cancelar")
    public ResponseEntity<Void> cancelarCargo(@PathVariable Long id) {
        pagoService.cancelarCargo(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== GENERACIÓN AUTOMÁTICA ====================

    @PostMapping("/cargos/generar-fijos")
    public ResponseEntity<List<CargoDTO>> generarCargosFijos(
            @Valid @RequestBody GenerarCargosFijosRequest request) {
        return new ResponseEntity<>(pagoService.generarCargosFijos(request), HttpStatus.CREATED);
    }

    @PostMapping("/cargos/actualizar-vencidos")
    public ResponseEntity<Void> actualizarCargosVencidos() {
        pagoService.actualizarCargosVencidos();
        return ResponseEntity.noContent().build();
    }

    // ==================== ESTADÍSTICAS ====================

    @GetMapping("/estadisticas")
    public ResponseEntity<PagoService.PagoEstadisticas> getEstadisticas() {
        return ResponseEntity.ok(pagoService.getEstadisticas());
    }

    @GetMapping("/saldo-pendiente/{contratoId}")
    public ResponseEntity<BigDecimal> getSaldoPendienteContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(pagoService.getSaldoPendienteContrato(contratoId));
    }
}
