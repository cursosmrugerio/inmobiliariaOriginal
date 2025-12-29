package com.inmobiliaria.empresa;

import com.inmobiliaria.empresa.dto.CreateEmpresaRequest;
import com.inmobiliaria.empresa.dto.EmpresaDTO;
import com.inmobiliaria.empresa.dto.UpdateEmpresaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> getAll(@RequestParam(defaultValue = "true") boolean activeOnly) {
        List<EmpresaDTO> empresas = activeOnly
                ? empresaService.getAllActive()
                : empresaService.getAll();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> getById(@PathVariable Long id) {
        try {
            EmpresaDTO empresa = empresaService.getById(id);
            return ResponseEntity.ok(empresa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO> create(@Valid @RequestBody CreateEmpresaRequest request) {
        try {
            EmpresaDTO empresa = empresaService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(empresa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmpresaRequest request) {
        try {
            EmpresaDTO empresa = empresaService.update(id, request);
            return ResponseEntity.ok(empresa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            empresaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
