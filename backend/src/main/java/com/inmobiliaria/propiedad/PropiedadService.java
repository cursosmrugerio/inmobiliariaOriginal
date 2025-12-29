package com.inmobiliaria.propiedad;

import com.inmobiliaria.catalogo.*;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.propiedad.dto.*;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final PropiedadPropietarioRepository propiedadPropietarioRepository;
    private final TipoPropiedadRepository tipoPropiedadRepository;
    private final PersonaRepository personaRepository;
    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final ColoniaRepository coloniaRepository;

    // --- Propiedad CRUD ---

    @Transactional(readOnly = true)
    public List<PropiedadDTO> getAllPropiedades(boolean activeOnly, Boolean disponible) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<Propiedad> propiedades;

        if (disponible != null && disponible) {
            propiedades = propiedadRepository.findByEmpresaIdAndDisponibleTrue(empresaId);
        } else if (activeOnly) {
            propiedades = propiedadRepository.findByEmpresaIdAndActivoTrue(empresaId);
        } else {
            propiedades = propiedadRepository.findByEmpresaId(empresaId);
        }

        return propiedades.stream().map(PropiedadDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public List<PropiedadDTO> getPropiedadesByTipo(Integer tipoId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return propiedadRepository.findByEmpresaIdAndTipoPropiedad(empresaId, tipoId)
                .stream().map(PropiedadDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public List<PropiedadDTO> getPropiedadesByPropietario(Long propietarioId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return propiedadRepository.findByEmpresaIdAndPropietarioId(empresaId, propietarioId)
                .stream().map(PropiedadDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public PropiedadDTO getPropiedadById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));
        return PropiedadDTO.fromEntity(propiedad);
    }

    @Transactional
    public PropiedadDTO createPropiedad(CreatePropiedadRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        // Validate clave catastral uniqueness
        if (request.getClaveCatastral() != null &&
                propiedadRepository.existsByClaveCatastralAndEmpresaId(request.getClaveCatastral(), empresaId)) {
            throw new IllegalArgumentException("Ya existe una propiedad con esta clave catastral");
        }

        TipoPropiedad tipoPropiedad = tipoPropiedadRepository.findById(request.getTipoPropiedadId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de propiedad no encontrado"));

        Propiedad propiedad = Propiedad.builder()
                .empresaId(empresaId)
                .tipoPropiedad(tipoPropiedad)
                .nombre(request.getNombre())
                .claveCatastral(request.getClaveCatastral())
                .calle(request.getCalle())
                .numeroExterior(request.getNumeroExterior())
                .numeroInterior(request.getNumeroInterior())
                .codigoPostal(request.getCodigoPostal())
                .referencias(request.getReferencias())
                .superficieTerreno(request.getSuperficieTerreno())
                .superficieConstruccion(request.getSuperficieConstruccion())
                .numRecamaras(request.getNumRecamaras())
                .numBanos(request.getNumBanos())
                .numEstacionamientos(request.getNumEstacionamientos())
                .numPisos(request.getNumPisos())
                .anioConstruccion(request.getAnioConstruccion())
                .valorComercial(request.getValorComercial())
                .valorCatastral(request.getValorCatastral())
                .rentaMensual(request.getRentaMensual())
                .notas(request.getNotas())
                .disponible(true)
                .activo(true)
                .build();

        // Set catalog references
        if (request.getEstadoId() != null) {
            Estado estado = estadoRepository.findById(request.getEstadoId()).orElse(null);
            propiedad.setEstado(estado);
        }
        if (request.getMunicipioId() != null) {
            Municipio municipio = municipioRepository.findById(request.getMunicipioId()).orElse(null);
            propiedad.setMunicipio(municipio);
        }
        if (request.getColoniaId() != null) {
            Colonia colonia = coloniaRepository.findById(request.getColoniaId()).orElse(null);
            propiedad.setColonia(colonia);
        }

        propiedad = propiedadRepository.save(propiedad);

        // Add propietarios if provided
        if (request.getPropietariosIds() != null && !request.getPropietariosIds().isEmpty()) {
            for (Long propietarioId : request.getPropietariosIds()) {
                addPropietarioToPropiedad(propiedad.getId(), AddPropietarioRequest.builder()
                        .propietarioId(propietarioId)
                        .porcentajePropiedad(new BigDecimal("100.00"))
                        .esPrincipal(request.getPropietariosIds().indexOf(propietarioId) == 0)
                        .build());
            }
            propiedad = propiedadRepository.findById(propiedad.getId()).orElseThrow();
        }

        return PropiedadDTO.fromEntity(propiedad);
    }

    @Transactional
    public PropiedadDTO updatePropiedad(Long id, UpdatePropiedadRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        if (request.getTipoPropiedadId() != null) {
            TipoPropiedad tipo = tipoPropiedadRepository.findById(request.getTipoPropiedadId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de propiedad no encontrado"));
            propiedad.setTipoPropiedad(tipo);
        }

        if (request.getNombre() != null) propiedad.setNombre(request.getNombre());
        if (request.getClaveCatastral() != null) propiedad.setClaveCatastral(request.getClaveCatastral());
        if (request.getCalle() != null) propiedad.setCalle(request.getCalle());
        if (request.getNumeroExterior() != null) propiedad.setNumeroExterior(request.getNumeroExterior());
        if (request.getNumeroInterior() != null) propiedad.setNumeroInterior(request.getNumeroInterior());
        if (request.getCodigoPostal() != null) propiedad.setCodigoPostal(request.getCodigoPostal());
        if (request.getReferencias() != null) propiedad.setReferencias(request.getReferencias());
        if (request.getSuperficieTerreno() != null) propiedad.setSuperficieTerreno(request.getSuperficieTerreno());
        if (request.getSuperficieConstruccion() != null) propiedad.setSuperficieConstruccion(request.getSuperficieConstruccion());
        if (request.getNumRecamaras() != null) propiedad.setNumRecamaras(request.getNumRecamaras());
        if (request.getNumBanos() != null) propiedad.setNumBanos(request.getNumBanos());
        if (request.getNumEstacionamientos() != null) propiedad.setNumEstacionamientos(request.getNumEstacionamientos());
        if (request.getNumPisos() != null) propiedad.setNumPisos(request.getNumPisos());
        if (request.getAnioConstruccion() != null) propiedad.setAnioConstruccion(request.getAnioConstruccion());
        if (request.getValorComercial() != null) propiedad.setValorComercial(request.getValorComercial());
        if (request.getValorCatastral() != null) propiedad.setValorCatastral(request.getValorCatastral());
        if (request.getRentaMensual() != null) propiedad.setRentaMensual(request.getRentaMensual());
        if (request.getDisponible() != null) propiedad.setDisponible(request.getDisponible());
        if (request.getNotas() != null) propiedad.setNotas(request.getNotas());
        if (request.getActivo() != null) propiedad.setActivo(request.getActivo());

        if (request.getEstadoId() != null) {
            Estado estado = estadoRepository.findById(request.getEstadoId()).orElse(null);
            propiedad.setEstado(estado);
        }
        if (request.getMunicipioId() != null) {
            Municipio municipio = municipioRepository.findById(request.getMunicipioId()).orElse(null);
            propiedad.setMunicipio(municipio);
        }
        if (request.getColoniaId() != null) {
            Colonia colonia = coloniaRepository.findById(request.getColoniaId()).orElse(null);
            propiedad.setColonia(colonia);
        }

        propiedad = propiedadRepository.save(propiedad);
        return PropiedadDTO.fromEntity(propiedad);
    }

    @Transactional
    public void deletePropiedad(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));
        propiedadRepository.delete(propiedad);
    }

    // --- Propietarios ---

    @Transactional
    public PropiedadPropietarioDTO addPropietarioToPropiedad(Long propiedadId, AddPropietarioRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Propiedad propiedad = propiedadRepository.findByIdAndEmpresaId(propiedadId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        if (propiedadPropietarioRepository.existsByPropiedadIdAndPropietarioId(propiedadId, request.getPropietarioId())) {
            throw new IllegalArgumentException("Este propietario ya está asignado a la propiedad");
        }

        Persona propietario = personaRepository.findByIdAndEmpresaId(request.getPropietarioId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propietario no encontrado"));

        // If this is set as principal, unset other principals
        if (request.isEsPrincipal()) {
            propiedadPropietarioRepository.findByPropiedadIdAndEsPrincipalTrue(propiedadId)
                    .ifPresent(pp -> {
                        pp.setEsPrincipal(false);
                        propiedadPropietarioRepository.save(pp);
                    });
        }

        PropiedadPropietario pp = PropiedadPropietario.builder()
                .propiedad(propiedad)
                .propietario(propietario)
                .porcentajePropiedad(request.getPorcentajePropiedad() != null ?
                        request.getPorcentajePropiedad() : new BigDecimal("100.00"))
                .fechaAdquisicion(request.getFechaAdquisicion())
                .esPrincipal(request.isEsPrincipal())
                .activo(true)
                .build();

        pp = propiedadPropietarioRepository.save(pp);
        return PropiedadPropietarioDTO.fromEntity(pp);
    }

    @Transactional
    public void removePropietarioFromPropiedad(Long propiedadId, Long propietarioId) {
        Long empresaId = TenantContext.getCurrentTenant();
        propiedadRepository.findByIdAndEmpresaId(propiedadId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        propiedadPropietarioRepository.deleteByPropiedadIdAndPropietarioId(propiedadId, propietarioId);
    }

    @Transactional(readOnly = true)
    public List<PropiedadPropietarioDTO> getPropiedadPropietarios(Long propiedadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        propiedadRepository.findByIdAndEmpresaId(propiedadId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        return propiedadPropietarioRepository.findByPropiedadIdAndActivoTrue(propiedadId)
                .stream().map(PropiedadPropietarioDTO::fromEntity).toList();
    }

    // --- Catálogos ---

    @Transactional(readOnly = true)
    public List<TipoPropiedad> getTiposPropiedad() {
        return tipoPropiedadRepository.findByActivoTrue();
    }
}
