package com.inmobiliaria.contrato;

import com.inmobiliaria.contrato.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contratos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE')")
public class ContratoController {

    private final ContratoService contratoService;

    // --- CRUD ---

    @GetMapping
    public ResponseEntity<List<ContratoDTO>> getAllContratos(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) EstadoContrato estado,
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) Long arrendatarioId) {

        List<ContratoDTO> contratos;

        if (propiedadId != null) {
            contratos = contratoService.getContratosByPropiedad(propiedadId);
        } else if (arrendatarioId != null) {
            contratos = contratoService.getContratosByArrendatario(arrendatarioId);
        } else {
            contratos = contratoService.getAllContratos(activeOnly, estado);
        }

        return ResponseEntity.ok(contratos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoDTO> getContratoById(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.getContratoById(id));
    }

    @PostMapping
    public ResponseEntity<ContratoDTO> createContrato(@Valid @RequestBody CreateContratoRequest request) {
        ContratoDTO contrato = contratoService.createContrato(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(contrato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoDTO> updateContrato(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContratoRequest request) {
        return ResponseEntity.ok(contratoService.updateContrato(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrato(@PathVariable Long id) {
        contratoService.deleteContrato(id);
        return ResponseEntity.noContent().build();
    }

    // --- Lifecycle Operations ---

    @PostMapping("/{id}/activar")
    public ResponseEntity<ContratoDTO> activarContrato(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.activarContrato(id));
    }

    @PostMapping("/{id}/terminar")
    public ResponseEntity<ContratoDTO> terminarContrato(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = body != null ? body.get("motivo") : null;
        return ResponseEntity.ok(contratoService.terminarContrato(id, motivo));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ContratoDTO> cancelarContrato(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = body != null ? body.get("motivo") : null;
        return ResponseEntity.ok(contratoService.cancelarContrato(id, motivo));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<ContratoDTO> renovarContrato(
            @PathVariable Long id,
            @Valid @RequestBody RenovarContratoRequest request) {
        return ResponseEntity.ok(contratoService.renovarContrato(id, request));
    }

    // --- Vencimientos ---

    @GetMapping("/por-vencer")
    public ResponseEntity<List<ContratoDTO>> getContratosPorVencer(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(contratoService.getContratosPorVencer(dias));
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<ContratoDTO>> getContratosVencidos() {
        return ResponseEntity.ok(contratoService.getContratosVencidos());
    }

    @PostMapping("/actualizar-vencimientos")
    public ResponseEntity<Void> actualizarEstadosVencimiento() {
        contratoService.actualizarEstadosVencimiento();
        return ResponseEntity.ok().build();
    }

    // --- Statistics ---

    @GetMapping("/estadisticas")
    public ResponseEntity<ContratoService.ContratoStats> getEstadisticas() {
        return ResponseEntity.ok(contratoService.getEstadisticas());
    }
}
