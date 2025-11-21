package com.inmobiliaria.propiedad;

import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.persona.TipoPersona;
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
                .direccionCompleta("Av. Reforma 123")
                .superficie(BigDecimal.valueOf(85.5))
                .numeroHabitaciones(2)
                .numeroBanos(1)
                .estacionamientos(1)
                .precioRenta(BigDecimal.valueOf(12000))
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

        List<PropiedadDTO> result = propiedadService.getAllPropiedades(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Depto Centro");
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
        request.setDireccionCompleta("Test Address");
        request.setSuperficie(BigDecimal.valueOf(100));
        request.setPrecioRenta(BigDecimal.valueOf(15000));

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
    void deletePropiedad_shouldSoftDeletePropiedad() {
        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedad);

        propiedadService.deletePropiedad(1L);

        verify(propiedadRepository).save(argThat(p -> !p.getActivo()));
    }

    @Test
    void getDisponibles_shouldReturnAvailableProperties() {
        when(propiedadRepository.findByEmpresaIdAndDisponibleTrueAndActivoTrue(EMPRESA_ID))
                .thenReturn(Arrays.asList(propiedad));

        List<PropiedadDTO> result = propiedadService.getDisponibles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDisponible()).isTrue();
    }

    @Test
    void assignPropietario_shouldAssignOwner() {
        Persona propietario = Persona.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Propietario")
                .build();

        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propietario));
        when(propiedadPropietarioRepository.existsByPropiedadIdAndPersonaId(1L, 1L))
                .thenReturn(false);
        when(propiedadPropietarioRepository.save(any(PropiedadPropietario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        propiedadService.assignPropietario(1L, 1L, BigDecimal.valueOf(100));

        verify(propiedadPropietarioRepository).save(any(PropiedadPropietario.class));
    }
}
