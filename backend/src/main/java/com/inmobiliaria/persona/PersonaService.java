package com.inmobiliaria.persona;

import com.inmobiliaria.catalogo.Colonia;
import com.inmobiliaria.catalogo.ColoniaRepository;
import com.inmobiliaria.catalogo.Estado;
import com.inmobiliaria.catalogo.EstadoRepository;
import com.inmobiliaria.catalogo.Municipio;
import com.inmobiliaria.catalogo.MunicipioRepository;
import com.inmobiliaria.catalogo.Rol;
import com.inmobiliaria.catalogo.RolRepository;
import com.inmobiliaria.persona.dto.*;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaRolRepository personaRolRepository;
    private final DireccionRepository direccionRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final RolRepository rolRepository;
    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final ColoniaRepository coloniaRepository;

    // --- Persona CRUD ---

    @Transactional(readOnly = true)
    public List<PersonaDTO> getAllPersonas(boolean activeOnly) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<Persona> personas = activeOnly
                ? personaRepository.findByEmpresaIdAndActivoTrue(empresaId)
                : personaRepository.findByEmpresaId(empresaId);
        return personas.stream().map(PersonaDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public List<PersonaDTO> getPersonasByRol(Integer rolId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return personaRepository.findByEmpresaIdAndRol(empresaId, rolId)
                .stream().map(PersonaDTO::fromEntityBasic).toList();
    }

    @Transactional(readOnly = true)
    public PersonaDTO getPersonaById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Persona persona = personaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));
        return PersonaDTO.fromEntity(persona);
    }

    @Transactional
    public PersonaDTO createPersona(CreatePersonaRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        // Validate RFC uniqueness
        if (request.getRfc() != null && personaRepository.existsByRfcAndEmpresaId(request.getRfc(), empresaId)) {
            throw new IllegalArgumentException("Ya existe una persona con este RFC");
        }

        Persona persona = Persona.builder()
                .empresaId(empresaId)
                .tipoPersona(request.getTipoPersona())
                .nombre(request.getNombre())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .fechaNacimiento(request.getFechaNacimiento())
                .curp(request.getCurp())
                .razonSocial(request.getRazonSocial())
                .nombreComercial(request.getNombreComercial())
                .rfc(request.getRfc())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .telefonoMovil(request.getTelefonoMovil())
                .activo(true)
                .build();

        persona = personaRepository.save(persona);

        // Assign roles if provided
        if (request.getRolesIds() != null && !request.getRolesIds().isEmpty()) {
            for (Integer rolId : request.getRolesIds()) {
                addRolToPersona(persona.getId(), rolId);
            }
            persona = personaRepository.findById(persona.getId()).orElseThrow();
        }

        return PersonaDTO.fromEntity(persona);
    }

    @Transactional
    public PersonaDTO updatePersona(Long id, UpdatePersonaRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Persona persona = personaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        if (request.getTipoPersona() != null) persona.setTipoPersona(request.getTipoPersona());
        if (request.getNombre() != null) persona.setNombre(request.getNombre());
        if (request.getApellidoPaterno() != null) persona.setApellidoPaterno(request.getApellidoPaterno());
        if (request.getApellidoMaterno() != null) persona.setApellidoMaterno(request.getApellidoMaterno());
        if (request.getFechaNacimiento() != null) persona.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getCurp() != null) persona.setCurp(request.getCurp());
        if (request.getRazonSocial() != null) persona.setRazonSocial(request.getRazonSocial());
        if (request.getNombreComercial() != null) persona.setNombreComercial(request.getNombreComercial());
        if (request.getRfc() != null) persona.setRfc(request.getRfc());
        if (request.getEmail() != null) persona.setEmail(request.getEmail());
        if (request.getTelefono() != null) persona.setTelefono(request.getTelefono());
        if (request.getTelefonoMovil() != null) persona.setTelefonoMovil(request.getTelefonoMovil());
        if (request.getActivo() != null) persona.setActivo(request.getActivo());

        persona = personaRepository.save(persona);
        return PersonaDTO.fromEntity(persona);
    }

    @Transactional
    public void deletePersona(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Persona persona = personaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));
        personaRepository.delete(persona);
    }

    // --- PersonaRol ---

    @Transactional
    public PersonaRolDTO addRolToPersona(Long personaId, Integer rolId) {
        Long empresaId = TenantContext.getCurrentTenant();
        Persona persona = personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        if (personaRolRepository.existsByPersonaIdAndRolId(personaId, rolId)) {
            throw new IllegalArgumentException("La persona ya tiene este rol asignado");
        }

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));

        PersonaRol personaRol = PersonaRol.builder()
                .persona(persona)
                .rol(rol)
                .activo(true)
                .build();

        personaRol = personaRolRepository.save(personaRol);
        return PersonaRolDTO.fromEntity(personaRol);
    }

    @Transactional
    public void removeRolFromPersona(Long personaId, Integer rolId) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        personaRolRepository.deleteByPersonaIdAndRolId(personaId, rolId);
    }

    @Transactional(readOnly = true)
    public List<PersonaRolDTO> getPersonaRoles(Long personaId) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        return personaRolRepository.findByPersonaIdAndActivoTrue(personaId)
                .stream().map(PersonaRolDTO::fromEntity).toList();
    }

    // --- Direccion ---

    @Transactional
    public DireccionDTO addDireccion(Long personaId, CreateDireccionRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Persona persona = personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        // If this is set as principal, unset other principal addresses
        if (request.isEsPrincipal()) {
            direccionRepository.findByPersonaIdAndEsPrincipalTrue(personaId)
                    .ifPresent(dir -> {
                        dir.setEsPrincipal(false);
                        direccionRepository.save(dir);
                    });
        }

        Direccion direccion = Direccion.builder()
                .persona(persona)
                .tipoDireccion(request.getTipoDireccion())
                .calle(request.getCalle())
                .numeroExterior(request.getNumeroExterior())
                .numeroInterior(request.getNumeroInterior())
                .codigoPostal(request.getCodigoPostal())
                .referencias(request.getReferencias())
                .esPrincipal(request.isEsPrincipal())
                .activo(true)
                .build();

        // Set catalog references
        if (request.getEstadoId() != null) {
            Estado estado = estadoRepository.findById(request.getEstadoId()).orElse(null);
            direccion.setEstado(estado);
        }
        if (request.getMunicipioId() != null) {
            Municipio municipio = municipioRepository.findById(request.getMunicipioId()).orElse(null);
            direccion.setMunicipio(municipio);
        }
        if (request.getColoniaId() != null) {
            Colonia colonia = coloniaRepository.findById(request.getColoniaId()).orElse(null);
            direccion.setColonia(colonia);
        }

        direccion = direccionRepository.save(direccion);
        return DireccionDTO.fromEntity(direccion);
    }

    @Transactional
    public DireccionDTO updateDireccion(Long personaId, Long direccionId, UpdateDireccionRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        Direccion direccion = direccionRepository.findByIdAndPersonaId(direccionId, personaId)
                .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));

        if (request.getTipoDireccion() != null) direccion.setTipoDireccion(request.getTipoDireccion());
        if (request.getCalle() != null) direccion.setCalle(request.getCalle());
        if (request.getNumeroExterior() != null) direccion.setNumeroExterior(request.getNumeroExterior());
        if (request.getNumeroInterior() != null) direccion.setNumeroInterior(request.getNumeroInterior());
        if (request.getCodigoPostal() != null) direccion.setCodigoPostal(request.getCodigoPostal());
        if (request.getReferencias() != null) direccion.setReferencias(request.getReferencias());
        if (request.getActivo() != null) direccion.setActivo(request.getActivo());

        if (request.getEsPrincipal() != null && request.getEsPrincipal() && !direccion.isEsPrincipal()) {
            direccionRepository.findByPersonaIdAndEsPrincipalTrue(personaId)
                    .ifPresent(dir -> {
                        dir.setEsPrincipal(false);
                        direccionRepository.save(dir);
                    });
            direccion.setEsPrincipal(true);
        }

        if (request.getEstadoId() != null) {
            Estado estado = estadoRepository.findById(request.getEstadoId()).orElse(null);
            direccion.setEstado(estado);
        }
        if (request.getMunicipioId() != null) {
            Municipio municipio = municipioRepository.findById(request.getMunicipioId()).orElse(null);
            direccion.setMunicipio(municipio);
        }
        if (request.getColoniaId() != null) {
            Colonia colonia = coloniaRepository.findById(request.getColoniaId()).orElse(null);
            direccion.setColonia(colonia);
        }

        direccion = direccionRepository.save(direccion);
        return DireccionDTO.fromEntity(direccion);
    }

    @Transactional
    public void deleteDireccion(Long personaId, Long direccionId) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        Direccion direccion = direccionRepository.findByIdAndPersonaId(direccionId, personaId)
                .orElseThrow(() -> new EntityNotFoundException("Dirección no encontrada"));

        direccionRepository.delete(direccion);
    }

    @Transactional(readOnly = true)
    public List<DireccionDTO> getPersonaDirecciones(Long personaId) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        return direccionRepository.findByPersonaIdAndActivoTrue(personaId)
                .stream().map(DireccionDTO::fromEntity).toList();
    }

    // --- CuentaBancaria ---

    @Transactional
    public CuentaBancariaDTO addCuentaBancaria(Long personaId, CreateCuentaBancariaRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Persona persona = personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        // If this is set as principal, unset other principal accounts
        if (request.isEsPrincipal()) {
            cuentaBancariaRepository.findByPersonaIdAndEsPrincipalTrue(personaId)
                    .ifPresent(cuenta -> {
                        cuenta.setEsPrincipal(false);
                        cuentaBancariaRepository.save(cuenta);
                    });
        }

        CuentaBancaria cuenta = CuentaBancaria.builder()
                .persona(persona)
                .banco(request.getBanco())
                .numeroCuenta(request.getNumeroCuenta())
                .clabe(request.getClabe())
                .titular(request.getTitular())
                .esPrincipal(request.isEsPrincipal())
                .activo(true)
                .build();

        cuenta = cuentaBancariaRepository.save(cuenta);
        return CuentaBancariaDTO.fromEntity(cuenta);
    }

    @Transactional
    public CuentaBancariaDTO updateCuentaBancaria(Long personaId, Long cuentaId, UpdateCuentaBancariaRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        CuentaBancaria cuenta = cuentaBancariaRepository.findByIdAndPersonaId(cuentaId, personaId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta bancaria no encontrada"));

        if (request.getBanco() != null) cuenta.setBanco(request.getBanco());
        if (request.getNumeroCuenta() != null) cuenta.setNumeroCuenta(request.getNumeroCuenta());
        if (request.getClabe() != null) cuenta.setClabe(request.getClabe());
        if (request.getTitular() != null) cuenta.setTitular(request.getTitular());
        if (request.getActivo() != null) cuenta.setActivo(request.getActivo());

        if (request.getEsPrincipal() != null && request.getEsPrincipal() && !cuenta.isEsPrincipal()) {
            cuentaBancariaRepository.findByPersonaIdAndEsPrincipalTrue(personaId)
                    .ifPresent(c -> {
                        c.setEsPrincipal(false);
                        cuentaBancariaRepository.save(c);
                    });
            cuenta.setEsPrincipal(true);
        }

        cuenta = cuentaBancariaRepository.save(cuenta);
        return CuentaBancariaDTO.fromEntity(cuenta);
    }

    @Transactional
    public void deleteCuentaBancaria(Long personaId, Long cuentaId) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        CuentaBancaria cuenta = cuentaBancariaRepository.findByIdAndPersonaId(cuentaId, personaId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta bancaria no encontrada"));

        cuentaBancariaRepository.delete(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaBancariaDTO> getPersonaCuentasBancarias(Long personaId) {
        Long empresaId = TenantContext.getCurrentTenant();
        personaRepository.findByIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada"));

        return cuentaBancariaRepository.findByPersonaIdAndActivoTrue(personaId)
                .stream().map(CuentaBancariaDTO::fromEntity).toList();
    }
}
