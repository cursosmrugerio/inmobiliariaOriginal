package com.inmobiliaria.empresa;

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
class EmpresaRepositoryIntegrationTest {

    @Autowired
    private EmpresaRepository empresaRepository;

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresaRepository.deleteAll();

        empresa = Empresa.builder()
                .nombre("Test Inmobiliaria")
                .rfc("TEST123456ABC")
                .direccion("Test Address 123")
                .telefono("5551234567")
                .email("test@inmobiliaria.com")
                .activo(true)
                .build();
    }

    @Test
    void save_shouldPersistEmpresa() {
        Empresa saved = empresaRepository.save(empresa);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("Test Inmobiliaria");
    }

    @Test
    void findById_shouldReturnEmpresa() {
        Empresa saved = empresaRepository.save(empresa);

        Optional<Empresa> found = empresaRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getRfc()).isEqualTo("TEST123456ABC");
    }

    @Test
    void findByActivoTrue_shouldReturnOnlyActiveEmpresas() {
        empresaRepository.save(empresa);

        Empresa inactiva = Empresa.builder()
                .nombre("Inactiva")
                .rfc("INACT123456")
                .activo(false)
                .build();
        empresaRepository.save(inactiva);

        List<Empresa> activas = empresaRepository.findByActivoTrue();

        assertThat(activas).hasSize(1);
        assertThat(activas.get(0).getNombre()).isEqualTo("Test Inmobiliaria");
    }

    @Test
    void findByIdAndActivoTrue_shouldReturnActiveEmpresa() {
        Empresa saved = empresaRepository.save(empresa);

        Optional<Empresa> found = empresaRepository.findByIdAndActivoTrue(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getActivo()).isTrue();
    }

    @Test
    void existsByRfc_shouldReturnTrue_whenRfcExists() {
        empresaRepository.save(empresa);

        boolean exists = empresaRepository.existsByRfc("TEST123456ABC");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByRfc_shouldReturnFalse_whenRfcNotExists() {
        boolean exists = empresaRepository.existsByRfc("NONEXISTENT");

        assertThat(exists).isFalse();
    }

    @Test
    void delete_shouldRemoveEmpresa() {
        Empresa saved = empresaRepository.save(empresa);

        empresaRepository.deleteById(saved.getId());

        Optional<Empresa> found = empresaRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void update_shouldModifyEmpresa() {
        Empresa saved = empresaRepository.save(empresa);
        saved.setNombre("Updated Name");

        Empresa updated = empresaRepository.save(saved);

        assertThat(updated.getNombre()).isEqualTo("Updated Name");
    }
}
