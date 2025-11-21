package com.inmobiliaria.mantenimiento;

import com.inmobiliaria.mantenimiento.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    // --- Proveedores ---

    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorDTO>> getAllProveedores() {
        return ResponseEntity.ok(mantenimientoService.getAllProveedores());
    }

    @GetMapping("/proveedores/{id}")
    public ResponseEntity<ProveedorDTO> getProveedorById(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.getProveedorById(id));
    }

    @PostMapping("/proveedores")
    public ResponseEntity<ProveedorDTO> createProveedor(@Valid @RequestBody CreateProveedorRequest request) {
        ProveedorDTO proveedor = mantenimientoService.createProveedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedor);
    }

    @PutMapping("/proveedores/{id}")
    public ResponseEntity<ProveedorDTO> updateProveedor(
            @PathVariable Long id,
            @Valid @RequestBody CreateProveedorRequest request) {
        return ResponseEntity.ok(mantenimientoService.updateProveedor(id, request));
    }

    @DeleteMapping("/proveedores/{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Long id) {
        mantenimientoService.deleteProveedor(id);
        return ResponseEntity.noContent().build();
    }

    // --- Órdenes de Mantenimiento ---

    @GetMapping("/ordenes")
    public ResponseEntity<List<OrdenMantenimientoDTO>> getAllOrdenes(
            @RequestParam(required = false) EstadoOrden estado) {
        return ResponseEntity.ok(mantenimientoService.getAllOrdenes(estado));
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<OrdenMantenimientoDTO> getOrdenById(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.getOrdenById(id));
    }

    @PostMapping("/ordenes")
    public ResponseEntity<OrdenMantenimientoDTO> createOrden(@Valid @RequestBody CreateOrdenRequest request) {
        OrdenMantenimientoDTO orden = mantenimientoService.createOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    @PutMapping("/ordenes/{id}")
    public ResponseEntity<OrdenMantenimientoDTO> updateOrden(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrdenRequest request) {
        return ResponseEntity.ok(mantenimientoService.updateOrden(id, request));
    }

    // --- Seguimiento ---

    @GetMapping("/ordenes/{ordenId}/seguimiento")
    public ResponseEntity<List<SeguimientoOrdenDTO>> getSeguimientos(@PathVariable Long ordenId) {
        return ResponseEntity.ok(mantenimientoService.getSeguimientosByOrden(ordenId));
    }

    @PostMapping("/ordenes/{ordenId}/seguimiento")
    public ResponseEntity<SeguimientoOrdenDTO> addSeguimiento(
            @PathVariable Long ordenId,
            @Valid @RequestBody CreateSeguimientoRequest request) {
        SeguimientoOrdenDTO seguimiento = mantenimientoService.addSeguimiento(ordenId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(seguimiento);
    }
}
