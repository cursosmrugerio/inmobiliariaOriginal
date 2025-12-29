package com.inmobiliaria.propiedad;

import com.inmobiliaria.catalogo.*;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.persona.TipoPersona;
import com.inmobiliaria.propiedad.dto.AddPropietarioRequest;
import com.inmobiliaria.propiedad.dto.CreatePropiedadRequest;
import com.inmobiliaria.propiedad.dto.PropiedadDTO;
import com.inmobiliaria.propiedad.dto.UpdatePropiedadRequest;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;
    @Mock
    private TipoPropiedadRepository tipoPropiedadRepository;
    @Mock
    private PropiedadPropietarioRepository propiedadPropietarioRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private EstadoRepository estadoRepository;
    @Mock
    private MunicipioRepository municipioRepository;
    @Mock
    private ColoniaRepository coloniaRepository;

    @InjectMocks
    private PropiedadService propiedadService;

    private MockedStatic<TenantContext> tenantContextMock;
    private Propiedad propiedad;
    private TipoPropiedad tipoPropiedad;
    private final Long EMPRESA_ID = 1L;

    @BeforeEach
    void setUp() {
        tenantContextMock = mockStatic(TenantContext.class);
        tenantContextMock.when(TenantContext::getCurrentTenant).thenReturn(EMPRESA_ID);

        tipoPropiedad = new TipoPropiedad();
        tipoPropiedad.setId(1);
        tipoPropiedad.setNombre("DEPARTAMENTO");

        propiedad = Propiedad.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPropiedad(tipoPropiedad)
                .nombre("Depto Centro")
                .calle("Av. Reforma 123")
                .superficieConstruccion(BigDecimal.valueOf(85.5))
                .numRecamaras(2)
                .numBanos(BigDecimal.valueOf(1))
                .numEstacionamientos(1)
                .rentaMensual(BigDecimal.valueOf(12000))
                .disponible(true)
                .activo(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        tenantContextMock.close();
    }

    @Test
    void getAllPropiedades_shouldReturnActivePropiedades() {
        when(propiedadRepository.findByEmpresaIdAndActivoTrue(EMPRESA_ID))
                .thenReturn(Arrays.asList(propiedad));

        List<PropiedadDTO> result = propiedadService.getAllPropiedades(true, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Depto Centro");
    }

    @Test
    void getAllPropiedades_shouldReturnDisponiblePropiedades() {
        when(propiedadRepository.findByEmpresaIdAndDisponibleTrue(EMPRESA_ID))
                .thenReturn(Arrays.asList(propiedad));

        List<PropiedadDTO> result = propiedadService.getAllPropiedades(true, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isDisponible()).isTrue();
    }

    @Test
    void getPropiedadById_shouldReturnPropiedad_whenExists() {
        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));

        PropiedadDTO result = propiedadService.getPropiedadById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Depto Centro");
    }

    @Test
    void getPropiedadById_shouldThrowException_whenNotExists() {
        when(propiedadRepository.findByIdAndEmpresaId(99L, EMPRESA_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> propiedadService.getPropiedadById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Propiedad no encontrada");
    }

    @Test
    void createPropiedad_shouldCreatePropiedad() {
        CreatePropiedadRequest request = new CreatePropiedadRequest();
        request.setNombre("Nueva Propiedad");
        request.setTipoPropiedadId(1);
        request.setCalle("Test Address");
        request.setSuperficieConstruccion(BigDecimal.valueOf(100));
        request.setRentaMensual(BigDecimal.valueOf(15000));

        when(tipoPropiedadRepository.findById(1)).thenReturn(Optional.of(tipoPropiedad));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedad);

        PropiedadDTO result = propiedadService.createPropiedad(request);

        assertThat(result).isNotNull();
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void updatePropiedad_shouldUpdatePropiedad_whenExists() {
        UpdatePropiedadRequest request = new UpdatePropiedadRequest();
        request.setNombre("Updated Name");

        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedad);

        PropiedadDTO result = propiedadService.updatePropiedad(1L, request);

        assertThat(result).isNotNull();
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void deletePropiedad_shouldDeletePropiedad() {
        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));

        propiedadService.deletePropiedad(1L);

        verify(propiedadRepository).delete(propiedad);
    }

    @Test
    void addPropietarioToPropiedad_shouldAssignOwner() {
        Persona propietario = Persona.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Propietario")
                .build();

        AddPropietarioRequest request = AddPropietarioRequest.builder()
                .propietarioId(1L)
                .porcentajePropiedad(BigDecimal.valueOf(100))
                .esPrincipal(true)
                .build();

        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propietario));
        when(propiedadPropietarioRepository.existsByEmpresaIdAndPropiedadIdAndPropietarioId(EMPRESA_ID, 1L, 1L))
                .thenReturn(false);
        when(propiedadPropietarioRepository.findByEmpresaIdAndPropiedadIdAndEsPrincipalTrue(EMPRESA_ID, 1L))
                .thenReturn(Optional.empty());
        when(propiedadPropietarioRepository.save(any(PropiedadPropietario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        propiedadService.addPropietarioToPropiedad(1L, request);

        verify(propiedadPropietarioRepository).save(any(PropiedadPropietario.class));
    }
}
