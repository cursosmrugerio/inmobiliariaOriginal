package com.inmobiliaria.notificacion.controller;

import com.inmobiliaria.notificacion.domain.CategoriaNotificacion;
import com.inmobiliaria.notificacion.domain.EstadoNotificacion;
import com.inmobiliaria.notificacion.dto.*;
import com.inmobiliaria.notificacion.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> findAll() {
        return ResponseEntity.ok(notificacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.findById(id));
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<NotificacionDTO>> findByPersona(@PathVariable Long personaId) {
        return ResponseEntity.ok(notificacionService.findByPersona(personaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionDTO>> findByEstado(@PathVariable EstadoNotificacion estado) {
        return ResponseEntity.ok(notificacionService.findByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> create(@Valid @RequestBody CreateNotificacionRequest request) {
        return ResponseEntity.ok(notificacionService.create(request));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<NotificacionDTO> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.enviar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        notificacionService.cancelar(id);
        return ResponseEntity.ok().build();
    }

    // Configuración endpoints
    @GetMapping("/configuracion")
    public ResponseEntity<List<ConfiguracionNotificacionDTO>> findAllConfiguraciones() {
        return ResponseEntity.ok(notificacionService.findAllConfiguraciones());
    }

    @GetMapping("/configuracion/{categoria}")
    public ResponseEntity<ConfiguracionNotificacionDTO> getConfiguracion(
            @PathVariable CategoriaNotificacion categoria) {
        ConfiguracionNotificacionDTO config = notificacionService.getConfiguracion(categoria);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }

    @PutMapping("/configuracion")
    public ResponseEntity<ConfiguracionNotificacionDTO> updateConfiguracion(
            @Valid @RequestBody UpdateConfiguracionRequest request) {
        return ResponseEntity.ok(notificacionService.updateConfiguracion(request));
    }
}
