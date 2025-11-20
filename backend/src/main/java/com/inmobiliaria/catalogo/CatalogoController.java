package com.inmobiliaria.catalogo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final ColoniaRepository coloniaRepository;
    private final CodigoPostalRepository codigoPostalRepository;
    private final TipoAsentamientoRepository tipoAsentamientoRepository;
    private final RolRepository rolRepository;

    // Estados
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> getEstados() {
        return ResponseEntity.ok(estadoRepository.findByActivoTrue());
    }

    @GetMapping("/estados/{id}")
    public ResponseEntity<Estado> getEstado(@PathVariable Integer id) {
        return estadoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Municipios
    @GetMapping("/municipios")
    public ResponseEntity<List<Municipio>> getMunicipios(
            @RequestParam(required = false) Integer estadoId) {
        if (estadoId != null) {
            return ResponseEntity.ok(municipioRepository.findByEstadoIdAndActivoTrue(estadoId));
        }
        return ResponseEntity.ok(municipioRepository.findByActivoTrue());
    }

    @GetMapping("/municipios/{id}")
    public ResponseEntity<Municipio> getMunicipio(@PathVariable Integer id) {
        return municipioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Colonias
    @GetMapping("/colonias")
    public ResponseEntity<List<Colonia>> getColonias(
            @RequestParam(required = false) Integer municipioId,
            @RequestParam(required = false) String codigoPostal) {
        if (municipioId != null) {
            return ResponseEntity.ok(coloniaRepository.findByMunicipioIdAndActivoTrue(municipioId));
        }
        if (codigoPostal != null) {
            return ResponseEntity.ok(coloniaRepository.findByCodigoPostalAndActivoTrue(codigoPostal));
        }
        return ResponseEntity.ok(coloniaRepository.findByActivoTrue());
    }

    @GetMapping("/colonias/{id}")
    public ResponseEntity<Colonia> getColonia(@PathVariable Integer id) {
        return coloniaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Códigos Postales
    @GetMapping("/codigos-postales")
    public ResponseEntity<List<CodigoPostal>> getCodigosPostales(
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Integer municipioId) {
        if (codigo != null) {
            return ResponseEntity.ok(codigoPostalRepository.findByCodigoAndActivoTrue(codigo));
        }
        if (municipioId != null) {
            return ResponseEntity.ok(codigoPostalRepository.findByMunicipioIdAndActivoTrue(municipioId));
        }
        return ResponseEntity.ok(codigoPostalRepository.findByActivoTrue());
    }

    @GetMapping("/codigos-postales/{id}")
    public ResponseEntity<CodigoPostal> getCodigoPostal(@PathVariable Integer id) {
        return codigoPostalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tipos de Asentamiento
    @GetMapping("/tipos-asentamiento")
    public ResponseEntity<List<TipoAsentamiento>> getTiposAsentamiento() {
        return ResponseEntity.ok(tipoAsentamientoRepository.findByActivoTrue());
    }

    @GetMapping("/tipos-asentamiento/{id}")
    public ResponseEntity<TipoAsentamiento> getTipoAsentamiento(@PathVariable Integer id) {
        return tipoAsentamientoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Roles
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> getRoles() {
        return ResponseEntity.ok(rolRepository.findByActivoTrue());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Rol> getRol(@PathVariable Integer id) {
        return rolRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/roles/clave/{clave}")
    public ResponseEntity<Rol> getRolByClave(@PathVariable String clave) {
        return rolRepository.findByClave(clave)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
