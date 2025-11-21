package com.inmobiliaria.catalogo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoService {

    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final ColoniaRepository coloniaRepository;
    private final CodigoPostalRepository codigoPostalRepository;
    private final TipoAsentamientoRepository tipoAsentamientoRepository;
    private final RolRepository rolRepository;

    // Estados
    public List<Estado> getEstados() {
        return estadoRepository.findByActivoTrue();
    }

    public Optional<Estado> getEstadoById(Integer id) {
        return estadoRepository.findById(id);
    }

    // Municipios
    public List<Municipio> getMunicipios() {
        return municipioRepository.findByActivoTrue();
    }

    public List<Municipio> getMunicipiosByEstado(Integer estadoId) {
        return municipioRepository.findByEstadoIdAndActivoTrue(estadoId);
    }

    public Optional<Municipio> getMunicipioById(Integer id) {
        return municipioRepository.findById(id);
    }

    // Colonias
    public List<Colonia> getColonias() {
        return coloniaRepository.findByActivoTrue();
    }

    public List<Colonia> getColoniasByMunicipio(Integer municipioId) {
        return coloniaRepository.findByMunicipioIdAndActivoTrue(municipioId);
    }

    public List<Colonia> getColoniasByCodigoPostal(String codigoPostal) {
        return coloniaRepository.findByCodigoPostalAndActivoTrue(codigoPostal);
    }

    public Optional<Colonia> getColoniaById(Integer id) {
        return coloniaRepository.findById(id);
    }

    // Códigos Postales
    public List<CodigoPostal> getCodigosPostales() {
        return codigoPostalRepository.findByActivoTrue();
    }

    public List<CodigoPostal> getCodigosPostalesByCodigo(String codigo) {
        return codigoPostalRepository.findByCodigoAndActivoTrue(codigo);
    }

    public List<CodigoPostal> getCodigosPostalesByMunicipio(Integer municipioId) {
        return codigoPostalRepository.findByMunicipioIdAndActivoTrue(municipioId);
    }

    public Optional<CodigoPostal> getCodigoPostalById(Integer id) {
        return codigoPostalRepository.findById(id);
    }

    // Tipos de Asentamiento
    public List<TipoAsentamiento> getTiposAsentamiento() {
        return tipoAsentamientoRepository.findByActivoTrue();
    }

    public Optional<TipoAsentamiento> getTipoAsentamientoById(Integer id) {
        return tipoAsentamientoRepository.findById(id);
    }

    // Roles
    public List<Rol> getRoles() {
        return rolRepository.findByActivoTrue();
    }

    public Optional<Rol> getRolById(Integer id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> getRolByClave(String clave) {
        return rolRepository.findByClave(clave);
    }
}
