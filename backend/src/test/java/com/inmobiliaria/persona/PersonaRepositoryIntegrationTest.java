package com.inmobiliaria.persona;

import com.inmobiliaria.catalogo.Rol;
import com.inmobiliaria.catalogo.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Testcontainers
class PersonaRepositoryIntegrationTest {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaRolRepository personaRolRepository;

    @Autowired
    private RolRepository rolRepository;

    private Persona persona;
    private final Long EMPRESA_ID = 1L;

    @BeforeEach
    void setUp() {
        personaRolRepository.deleteAll();
        personaRepository.deleteAll();

        persona = Persona.builder()
                .empresaId(EMPRESA_ID)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Juan")
                .apellidoPaterno("Perez")
                .apellidoMaterno("Garcia")
                .rfc("PEGJ800101ABC")
                .email("juan@test.com")
                .telefono("5551234567")
                .activo(true)
                .build();
    }

    @Test
    void save_shouldPersistPersona() {
        Persona saved = personaRepository.save(persona);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("Juan");
    }

    @Test
    void findByEmpresaId_shouldReturnPersonasForTenant() {
        personaRepository.save(persona);

        Persona otroTenant = Persona.builder()
                .empresaId(2L)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Maria")
                .activo(true)
                .build();
        personaRepository.save(otroTenant);

        List<Persona> personas = personaRepository.findByEmpresaId(EMPRESA_ID);

        assertThat(personas).hasSize(1);
        assertThat(personas.get(0).getNombre()).isEqualTo("Juan");
    }

    @Test
    void findByEmpresaIdAndActivoTrue_shouldReturnOnlyActivePersonas() {
        personaRepository.save(persona);

        Persona inactiva = Persona.builder()
                .empresaId(EMPRESA_ID)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Inactivo")
                .activo(false)
                .build();
        personaRepository.save(inactiva);

        List<Persona> activas = personaRepository.findByEmpresaIdAndActivoTrue(EMPRESA_ID);

        assertThat(activas).hasSize(1);
        assertThat(activas.get(0).getNombre()).isEqualTo("Juan");
    }

    @Test
    void findByIdAndEmpresaId_shouldReturnPersona_whenBelongsToTenant() {
        Persona saved = personaRepository.save(persona);

        Optional<Persona> found = personaRepository.findByIdAndEmpresaId(saved.getId(), EMPRESA_ID);

        assertThat(found).isPresent();
        assertThat(found.get().getRfc()).isEqualTo("PEGJ800101ABC");
    }

    @Test
    void findByIdAndEmpresaId_shouldReturnEmpty_whenDifferentTenant() {
        Persona saved = personaRepository.save(persona);

        Optional<Persona> found = personaRepository.findByIdAndEmpresaId(saved.getId(), 999L);

        assertThat(found).isEmpty();
    }

    @Test
    void existsByRfcAndEmpresaId_shouldReturnTrue_whenExists() {
        personaRepository.save(persona);

        boolean exists = personaRepository.existsByRfcAndEmpresaId("PEGJ800101ABC", EMPRESA_ID);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByRfcAndEmpresaId_shouldReturnFalse_whenDifferentTenant() {
        personaRepository.save(persona);

        boolean exists = personaRepository.existsByRfcAndEmpresaId("PEGJ800101ABC", 999L);

        assertThat(exists).isFalse();
    }

    @Test
    void findByEmpresaIdAndRol_shouldReturnPersonasWithSpecificRol() {
        Persona saved = personaRepository.save(persona);

        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("ARRENDATARIO");
        rol.setDescripcion("Arrendatario");
        rolRepository.save(rol);

        PersonaRol personaRol = PersonaRol.builder()
                .persona(saved)
                .rol(rol)
                .activo(true)
                .build();
        personaRolRepository.save(personaRol);

        List<Persona> personas = personaRepository.findByEmpresaIdAndRol(EMPRESA_ID, 1);

        assertThat(personas).hasSize(1);
        assertThat(personas.get(0).getNombre()).isEqualTo("Juan");
    }
}
