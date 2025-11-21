package com.inmobiliaria.contrato;

import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.persona.TipoPersona;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.propiedad.PropiedadRepository;
import com.inmobiliaria.catalogo.TipoPropiedad;
import com.inmobiliaria.contrato.dto.ContratoDTO;
import com.inmobiliaria.contrato.dto.CreateContratoRequest;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock
    private ContratoRepository contratoRepository;
    @Mock
    private PropiedadRepository propiedadRepository;
    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private ContratoService contratoService;

    private MockedStatic<TenantContext> tenantContextMock;
    private Contrato contrato;
    private Propiedad propiedad;
    private Persona arrendatario;
    private final Long EMPRESA_ID = 1L;

    @BeforeEach
    void setUp() {
        tenantContextMock = mockStatic(TenantContext.class);
        tenantContextMock.when(TenantContext::getCurrentTenant).thenReturn(EMPRESA_ID);

        TipoPropiedad tipoPropiedad = new TipoPropiedad();
        tipoPropiedad.setId(1);
        tipoPropiedad.setNombre("DEPARTAMENTO");

        propiedad = Propiedad.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPropiedad(tipoPropiedad)
                .nombre("Test Property")
                .disponible(true)
                .activo(true)
                .build();

        arrendatario = Persona.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Juan")
                .apellidoPaterno("Perez")
                .activo(true)
                .build();

        contrato = Contrato.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .propiedad(propiedad)
                .arrendatario(arrendatario)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusYears(1))
                .montoRenta(BigDecimal.valueOf(12000))
                .diaVencimiento(5)
                .deposito(BigDecimal.valueOf(24000))
                .estado(EstadoContrato.ACTIVO)
                .activo(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        tenantContextMock.close();
    }

    @Test
    void getAllContratos_shouldReturnActiveContratos() {
        when(contratoRepository.findByEmpresaIdAndActivoTrue(EMPRESA_ID))
                .thenReturn(Arrays.asList(contrato));

        List<ContratoDTO> result = contratoService.getAllContratos(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMontoRenta()).isEqualTo(BigDecimal.valueOf(12000));
    }

    @Test
    void getContratoById_shouldReturnContrato_whenExists() {
        when(contratoRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(contrato));

        ContratoDTO result = contratoService.getContratoById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getContratoById_shouldThrowException_whenNotExists() {
        when(contratoRepository.findByIdAndEmpresaId(99L, EMPRESA_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> contratoService.getContratoById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Contrato no encontrado");
    }

    @Test
    void createContrato_shouldCreateContrato() {
        CreateContratoRequest request = new CreateContratoRequest();
        request.setPropiedadId(1L);
        request.setArrendatarioId(1L);
        request.setFechaInicio(LocalDate.now());
        request.setFechaFin(LocalDate.now().plusYears(1));
        request.setMontoRenta(BigDecimal.valueOf(15000));
        request.setDiaVencimiento(5);
        request.setDeposito(BigDecimal.valueOf(30000));

        when(propiedadRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(propiedad));
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(arrendatario));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedad);

        ContratoDTO result = contratoService.createContrato(request);

        assertThat(result).isNotNull();
        verify(contratoRepository).save(any(Contrato.class));
        verify(propiedadRepository).save(argThat(p -> !p.getDisponible()));
    }

    @Test
    void getContratosByPropiedad_shouldReturnContratosForProperty() {
        when(contratoRepository.findByPropiedadIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Arrays.asList(contrato));

        List<ContratoDTO> result = contratoService.getContratosByPropiedad(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getContratosByArrendatario_shouldReturnContratosForTenant() {
        when(contratoRepository.findByArrendatarioIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Arrays.asList(contrato));

        List<ContratoDTO> result = contratoService.getContratosByArrendatario(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getContratosProximosAVencer_shouldReturnExpiringContracts() {
        when(contratoRepository.findContratosProximosAVencer(eq(EMPRESA_ID), any(LocalDate.class)))
                .thenReturn(Arrays.asList(contrato));

        List<ContratoDTO> result = contratoService.getContratosProximosAVencer(30);

        assertThat(result).hasSize(1);
    }

    @Test
    void terminarContrato_shouldTerminateContract() {
        when(contratoRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenReturn(contrato);
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedad);

        contratoService.terminarContrato(1L);

        verify(contratoRepository).save(argThat(c -> c.getEstado() == EstadoContrato.TERMINADO));
        verify(propiedadRepository).save(argThat(Propiedad::getDisponible));
    }
}
