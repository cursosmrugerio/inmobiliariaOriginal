package com.inmobiliaria.persona;

import com.inmobiliaria.persona.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    // --- Persona CRUD ---

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> getAllPersonas(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) Integer rolId) {
        List<PersonaDTO> personas = rolId != null
                ? personaService.getPersonasByRol(rolId)
                : personaService.getAllPersonas(activeOnly);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> getPersonaById(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.getPersonaById(id));
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> createPersona(@Valid @RequestBody CreatePersonaRequest request) {
        PersonaDTO persona = personaService.createPersona(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(persona);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> updatePersona(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePersonaRequest request) {
        return ResponseEntity.ok(personaService.updatePersona(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersona(@PathVariable Long id) {
        personaService.deletePersona(id);
        return ResponseEntity.noContent().build();
    }

    // --- Roles ---

    @GetMapping("/{personaId}/roles")
    public ResponseEntity<List<PersonaRolDTO>> getPersonaRoles(@PathVariable Long personaId) {
        return ResponseEntity.ok(personaService.getPersonaRoles(personaId));
    }

    @PostMapping("/{personaId}/roles/{rolId}")
    public ResponseEntity<PersonaRolDTO> addRolToPersona(
            @PathVariable Long personaId,
            @PathVariable Integer rolId) {
        PersonaRolDTO personaRol = personaService.addRolToPersona(personaId, rolId);
        return ResponseEntity.status(HttpStatus.CREATED).body(personaRol);
    }

    @DeleteMapping("/{personaId}/roles/{rolId}")
    public ResponseEntity<Void> removeRolFromPersona(
            @PathVariable Long personaId,
            @PathVariable Integer rolId) {
        personaService.removeRolFromPersona(personaId, rolId);
        return ResponseEntity.noContent().build();
    }

    // --- Direcciones ---

    @GetMapping("/{personaId}/direcciones")
    public ResponseEntity<List<DireccionDTO>> getPersonaDirecciones(@PathVariable Long personaId) {
        return ResponseEntity.ok(personaService.getPersonaDirecciones(personaId));
    }

    @PostMapping("/{personaId}/direcciones")
    public ResponseEntity<DireccionDTO> addDireccion(
            @PathVariable Long personaId,
            @Valid @RequestBody CreateDireccionRequest request) {
        DireccionDTO direccion = personaService.addDireccion(personaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(direccion);
    }

    @PutMapping("/{personaId}/direcciones/{direccionId}")
    public ResponseEntity<DireccionDTO> updateDireccion(
            @PathVariable Long personaId,
            @PathVariable Long direccionId,
            @Valid @RequestBody UpdateDireccionRequest request) {
        return ResponseEntity.ok(personaService.updateDireccion(personaId, direccionId, request));
    }

    @DeleteMapping("/{personaId}/direcciones/{direccionId}")
    public ResponseEntity<Void> deleteDireccion(
            @PathVariable Long personaId,
            @PathVariable Long direccionId) {
        personaService.deleteDireccion(personaId, direccionId);
        return ResponseEntity.noContent().build();
    }

    // --- Cuentas Bancarias ---

    @GetMapping("/{personaId}/cuentas-bancarias")
    public ResponseEntity<List<CuentaBancariaDTO>> getPersonaCuentasBancarias(@PathVariable Long personaId) {
        return ResponseEntity.ok(personaService.getPersonaCuentasBancarias(personaId));
    }

    @PostMapping("/{personaId}/cuentas-bancarias")
    public ResponseEntity<CuentaBancariaDTO> addCuentaBancaria(
            @PathVariable Long personaId,
            @Valid @RequestBody CreateCuentaBancariaRequest request) {
        CuentaBancariaDTO cuenta = personaService.addCuentaBancaria(personaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuenta);
    }

    @PutMapping("/{personaId}/cuentas-bancarias/{cuentaId}")
    public ResponseEntity<CuentaBancariaDTO> updateCuentaBancaria(
            @PathVariable Long personaId,
            @PathVariable Long cuentaId,
            @Valid @RequestBody UpdateCuentaBancariaRequest request) {
        return ResponseEntity.ok(personaService.updateCuentaBancaria(personaId, cuentaId, request));
    }

    @DeleteMapping("/{personaId}/cuentas-bancarias/{cuentaId}")
    public ResponseEntity<Void> deleteCuentaBancaria(
            @PathVariable Long personaId,
            @PathVariable Long cuentaId) {
        personaService.deleteCuentaBancaria(personaId, cuentaId);
        return ResponseEntity.noContent().build();
    }
}
