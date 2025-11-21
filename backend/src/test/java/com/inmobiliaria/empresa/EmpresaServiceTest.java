package com.inmobiliaria.empresa;

import com.inmobiliaria.empresa.dto.CreateEmpresaRequest;
import com.inmobiliaria.empresa.dto.EmpresaDTO;
import com.inmobiliaria.empresa.dto.UpdateEmpresaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = Empresa.builder()
                .id(1L)
                .nombre("Test Inmobiliaria")
                .rfc("TEST123456ABC")
                .direccion("Test Address 123")
                .telefono("5551234567")
                .email("test@inmobiliaria.com")
                .activo(true)
                .build();
    }

    @Test
    void getAllActive_shouldReturnActiveEmpresas() {
        when(empresaRepository.findByActivoTrue()).thenReturn(Arrays.asList(empresa));

        List<EmpresaDTO> result = empresaService.getAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Test Inmobiliaria");
        verify(empresaRepository).findByActivoTrue();
    }

    @Test
    void getAll_shouldReturnAllEmpresas() {
        when(empresaRepository.findAll()).thenReturn(Arrays.asList(empresa));

        List<EmpresaDTO> result = empresaService.getAll();

        assertThat(result).hasSize(1);
        verify(empresaRepository).findAll();
    }

    @Test
    void getById_shouldReturnEmpresa_whenExists() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        EmpresaDTO result = empresaService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Test Inmobiliaria");
    }

    @Test
    void getById_shouldThrowException_whenNotExists() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empresa no encontrada");
    }

    @Test
    void getActiveById_shouldReturnActiveEmpresa() {
        when(empresaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(empresa));

        EmpresaDTO result = empresaService.getActiveById(1L);

        assertThat(result.getActivo()).isTrue();
    }

    @Test
    void create_shouldCreateEmpresa_whenRfcIsUnique() {
        CreateEmpresaRequest request = new CreateEmpresaRequest();
        request.setNombre("Nueva Empresa");
        request.setRfc("NEW123456ABC");
        request.setEmail("nueva@empresa.com");

        when(empresaRepository.existsByRfc("NEW123456ABC")).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        EmpresaDTO result = empresaService.create(request);

        assertThat(result).isNotNull();
        verify(empresaRepository).save(any(Empresa.class));
    }

    @Test
    void create_shouldThrowException_whenRfcExists() {
        CreateEmpresaRequest request = new CreateEmpresaRequest();
        request.setNombre("Nueva Empresa");
        request.setRfc("EXISTING123");

        when(empresaRepository.existsByRfc("EXISTING123")).thenReturn(true);

        assertThatThrownBy(() -> empresaService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una empresa con el RFC");
    }

    @Test
    void update_shouldUpdateEmpresa_whenExists() {
        UpdateEmpresaRequest request = new UpdateEmpresaRequest();
        request.setNombre("Updated Name");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        EmpresaDTO result = empresaService.update(1L, request);

        assertThat(result).isNotNull();
        verify(empresaRepository).save(any(Empresa.class));
    }

    @Test
    void update_shouldThrowException_whenDuplicateRfc() {
        UpdateEmpresaRequest request = new UpdateEmpresaRequest();
        request.setRfc("DUPLICATE123");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.existsByRfc("DUPLICATE123")).thenReturn(true);

        assertThatThrownBy(() -> empresaService.update(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe una empresa con el RFC");
    }

    @Test
    void delete_shouldSoftDeleteEmpresa() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        empresaService.delete(1L);

        verify(empresaRepository).save(argThat(e -> !e.getActivo()));
    }

    @Test
    void hardDelete_shouldDeleteEmpresa_whenExists() {
        when(empresaRepository.existsById(1L)).thenReturn(true);

        empresaService.hardDelete(1L);

        verify(empresaRepository).deleteById(1L);
    }

    @Test
    void hardDelete_shouldThrowException_whenNotExists() {
        when(empresaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> empresaService.hardDelete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empresa no encontrada");
    }
}
