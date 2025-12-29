package com.inmobiliaria.propiedad;

import com.inmobiliaria.catalogo.TipoPropiedad;
import com.inmobiliaria.propiedad.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE')")
public class PropiedadController {

    private final PropiedadService propiedadService;

    // --- Propiedad CRUD ---

    @GetMapping
    public ResponseEntity<List<PropiedadDTO>> getAllPropiedades(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) Integer tipoId,
            @RequestParam(required = false) Long propietarioId) {

        List<PropiedadDTO> propiedades;
        if (tipoId != null) {
            propiedades = propiedadService.getPropiedadesByTipo(tipoId);
        } else if (propietarioId != null) {
            propiedades = propiedadService.getPropiedadesByPropietario(propietarioId);
        } else {
            propiedades = propiedadService.getAllPropiedades(activeOnly, disponible);
        }
        return ResponseEntity.ok(propiedades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropiedadDTO> getPropiedadById(@PathVariable Long id) {
        return ResponseEntity.ok(propiedadService.getPropiedadById(id));
    }

    @PostMapping
    public ResponseEntity<PropiedadDTO> createPropiedad(@Valid @RequestBody CreatePropiedadRequest request) {
        PropiedadDTO propiedad = propiedadService.createPropiedad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(propiedad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropiedadDTO> updatePropiedad(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePropiedadRequest request) {
        return ResponseEntity.ok(propiedadService.updatePropiedad(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePropiedad(@PathVariable Long id) {
        propiedadService.deletePropiedad(id);
        return ResponseEntity.noContent().build();
    }

    // --- Propietarios ---

    @GetMapping("/{propiedadId}/propietarios")
    public ResponseEntity<List<PropiedadPropietarioDTO>> getPropiedadPropietarios(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(propiedadService.getPropiedadPropietarios(propiedadId));
    }

    @PostMapping("/{propiedadId}/propietarios")
    public ResponseEntity<PropiedadPropietarioDTO> addPropietario(
            @PathVariable Long propiedadId,
            @Valid @RequestBody AddPropietarioRequest request) {
        PropiedadPropietarioDTO pp = propiedadService.addPropietarioToPropiedad(propiedadId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pp);
    }

    @DeleteMapping("/{propiedadId}/propietarios/{propietarioId}")
    public ResponseEntity<Void> removePropietario(
            @PathVariable Long propiedadId,
            @PathVariable Long propietarioId) {
        propiedadService.removePropietarioFromPropiedad(propiedadId, propietarioId);
        return ResponseEntity.noContent().build();
    }

    // --- Catálogos ---

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoPropiedad>> getTiposPropiedad() {
        return ResponseEntity.ok(propiedadService.getTiposPropiedad());
    }
}
