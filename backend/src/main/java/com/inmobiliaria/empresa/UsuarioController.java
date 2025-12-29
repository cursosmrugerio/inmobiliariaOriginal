package com.inmobiliaria.empresa;

import com.inmobiliaria.empresa.dto.CreateUsuarioRequest;
import com.inmobiliaria.empresa.dto.UpdateUsuarioRequest;
import com.inmobiliaria.empresa.dto.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll(
            @RequestParam(required = false) Boolean activo) {
        List<UsuarioDTO> usuarios;
        if (activo != null) {
            usuarios = usuarioService.getUsuariosByActivo(activo);
        } else {
            usuarios = usuarioService.getAllUsuarios();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.getUsuarioById(id);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUsuarioRequest request) {
        try {
            UsuarioDTO usuario = usuarioService.createUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsuarioRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UsuarioDTO usuario = usuarioService.updateUsuario(id, request, userDetails.getUsername());
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            usuarioService.deleteUsuario(id, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String rolStr = body.get("rol");
            if (rolStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El rol es requerido"));
            }
            Usuario.Rol nuevoRol = Usuario.Rol.valueOf(rolStr);
            UsuarioDTO usuario = usuarioService.cambiarRol(id, nuevoRol, userDetails.getUsername());
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<?> toggleActivo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UsuarioDTO usuario = usuarioService.toggleActivo(id, userDetails.getUsername());
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
