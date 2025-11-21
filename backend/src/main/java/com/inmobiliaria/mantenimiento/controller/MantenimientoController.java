package com.inmobiliaria.mantenimiento.controller;

import com.inmobiliaria.mantenimiento.domain.CategoriaMantenimiento;
import com.inmobiliaria.mantenimiento.domain.EstadoOrden;
import com.inmobiliaria.mantenimiento.dto.*;
import com.inmobiliaria.mantenimiento.service.MantenimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    // ==================== PROVEEDORES ====================

    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorDTO>> findAllProveedores() {
        return ResponseEntity.ok(mantenimientoService.findAllProveedores());
    }

    @GetMapping("/proveedores/activos")
    public ResponseEntity<List<ProveedorDTO>> findProveedoresActivos() {
        return ResponseEntity.ok(mantenimientoService.findProveedoresActivos());
    }

    @GetMapping("/proveedores/{id}")
    public ResponseEntity<ProveedorDTO> findProveedorById(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.findProveedorById(id));
    }

    @GetMapping("/proveedores/categoria/{categoria}")
    public ResponseEntity<List<ProveedorDTO>> findProveedoresByCategoria(
            @PathVariable CategoriaMantenimiento categoria) {
        return ResponseEntity.ok(mantenimientoService.findProveedoresByCategoria(categoria));
    }

    @PostMapping("/proveedores")
    public ResponseEntity<ProveedorDTO> createProveedor(
            @Valid @RequestBody CreateProveedorRequest request) {
        return ResponseEntity.ok(mantenimientoService.createProveedor(request));
    }

    @PutMapping("/proveedores/{id}")
    public ResponseEntity<ProveedorDTO> updateProveedor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProveedorRequest request) {
        return ResponseEntity.ok(mantenimientoService.updateProveedor(id, request));
    }

    @DeleteMapping("/proveedores/{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Long id) {
        mantenimientoService.deleteProveedor(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ÓRDENES DE MANTENIMIENTO ====================

    @GetMapping("/ordenes")
    public ResponseEntity<List<OrdenMantenimientoDTO>> findAllOrdenes() {
        return ResponseEntity.ok(mantenimientoService.findAllOrdenes());
    }

    @GetMapping("/ordenes/activas")
    public ResponseEntity<List<OrdenMantenimientoDTO>> findOrdenesActivas() {
        return ResponseEntity.ok(mantenimientoService.findOrdenesActivas());
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<OrdenMantenimientoDTO> findOrdenById(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.findOrdenById(id));
    }

    @GetMapping("/ordenes/propiedad/{propiedadId}")
    public ResponseEntity<List<OrdenMantenimientoDTO>> findOrdenesByPropiedad(
            @PathVariable Long propiedadId) {
        return ResponseEntity.ok(mantenimientoService.findOrdenesByPropiedad(propiedadId));
    }

    @GetMapping("/ordenes/proveedor/{proveedorId}")
    public ResponseEntity<List<OrdenMantenimientoDTO>> findOrdenesByProveedor(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(mantenimientoService.findOrdenesByProveedor(proveedorId));
    }

    @GetMapping("/ordenes/estado/{estado}")
    public ResponseEntity<List<OrdenMantenimientoDTO>> findOrdenesByEstado(
            @PathVariable EstadoOrden estado) {
        return ResponseEntity.ok(mantenimientoService.findOrdenesByEstado(estado));
    }

    @GetMapping("/ordenes/programadas")
    public ResponseEntity<List<OrdenMantenimientoDTO>> findOrdenesProgramadas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(mantenimientoService.findOrdenesProgramadas(inicio, fin));
    }

    @PostMapping("/ordenes")
    public ResponseEntity<OrdenMantenimientoDTO> createOrden(
            @Valid @RequestBody CreateOrdenRequest request) {
        return ResponseEntity.ok(mantenimientoService.createOrden(request));
    }

    @PutMapping("/ordenes/{id}")
    public ResponseEntity<OrdenMantenimientoDTO> updateOrden(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrdenRequest request) {
        return ResponseEntity.ok(mantenimientoService.updateOrden(id, request));
    }

    @PatchMapping("/ordenes/{id}/estado")
    public ResponseEntity<OrdenMantenimientoDTO> cambiarEstadoOrden(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(mantenimientoService.cambiarEstadoOrden(id, request));
    }

    @DeleteMapping("/ordenes/{id}")
    public ResponseEntity<Void> deleteOrden(@PathVariable Long id) {
        mantenimientoService.deleteOrden(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== SEGUIMIENTO ====================

    @GetMapping("/ordenes/{ordenId}/seguimiento")
    public ResponseEntity<List<SeguimientoOrdenDTO>> findSeguimientoByOrden(
            @PathVariable Long ordenId) {
        return ResponseEntity.ok(mantenimientoService.findSeguimientoByOrden(ordenId));
    }

    // ==================== ESTADÍSTICAS ====================

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        return ResponseEntity.ok(mantenimientoService.getEstadisticas());
    }

    // ==================== CATÁLOGOS ====================

    @GetMapping("/categorias")
    public ResponseEntity<CategoriaMantenimiento[]> getCategorias() {
        return ResponseEntity.ok(CategoriaMantenimiento.values());
    }

    @GetMapping("/prioridades")
    public ResponseEntity<com.inmobiliaria.mantenimiento.domain.PrioridadOrden[]> getPrioridades() {
        return ResponseEntity.ok(com.inmobiliaria.mantenimiento.domain.PrioridadOrden.values());
    }

    @GetMapping("/estados")
    public ResponseEntity<EstadoOrden[]> getEstados() {
        return ResponseEntity.ok(EstadoOrden.values());
    }
}
