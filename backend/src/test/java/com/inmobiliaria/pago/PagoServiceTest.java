package com.inmobiliaria.pago;

import com.inmobiliaria.contrato.Contrato;
import com.inmobiliaria.contrato.ContratoRepository;
import com.inmobiliaria.contrato.EstadoContrato;
import com.inmobiliaria.pago.dto.CargoDTO;
import com.inmobiliaria.pago.dto.CreateCargoRequest;
import com.inmobiliaria.pago.dto.CreatePagoRequest;
import com.inmobiliaria.pago.dto.PagoDTO;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.persona.TipoPersona;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.catalogo.TipoPropiedad;
import com.inmobiliaria.shared.multitenancy.TenantContext;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private PagoAplicacionRepository pagoAplicacionRepository;
    @Mock
    private ContratoRepository contratoRepository;
    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PagoService pagoService;

    private MockedStatic<TenantContext> tenantContextMock;
    private Contrato contrato;
    private Cargo cargo;
    private Pago pago;
    private Persona arrendatario;
    private final Long EMPRESA_ID = 1L;

    @BeforeEach
    void setUp() {
        tenantContextMock = mockStatic(TenantContext.class);
        tenantContextMock.when(TenantContext::getCurrentTenant).thenReturn(EMPRESA_ID);

        TipoPropiedad tipoPropiedad = new TipoPropiedad();
        tipoPropiedad.setId(1);
        tipoPropiedad.setNombre("DEPARTAMENTO");

        Propiedad propiedad = Propiedad.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPropiedad(tipoPropiedad)
                .nombre("Test Property")
                .calle("Test Street")
                .build();

        arrendatario = Persona.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Juan")
                .apellidoPaterno("Perez")
                .build();

        contrato = Contrato.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .numeroContrato("CTR-001")
                .propiedad(propiedad)
                .arrendatario(arrendatario)
                .montoRenta(BigDecimal.valueOf(12000))
                .diaPago(5)
                .estado(EstadoContrato.ACTIVO)
                .build();

        cargo = Cargo.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .contrato(contrato)
                .tipoCargo(TipoCargo.RENTA)
                .concepto("Renta Enero 2025")
                .montoOriginal(BigDecimal.valueOf(12000))
                .montoPendiente(BigDecimal.valueOf(12000))
                .montoPagado(BigDecimal.ZERO)
                .fechaCargo(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusDays(5))
                .estado(EstadoCargo.PENDIENTE)
                .build();

        pago = Pago.builder()
                .id(1L)
                .empresaId(EMPRESA_ID)
                .contrato(contrato)
                .persona(arrendatario)
                .numeroRecibo("REC-000001")
                .monto(BigDecimal.valueOf(12000))
                .montoAplicado(BigDecimal.valueOf(12000))
                .fechaPago(LocalDate.now())
                .tipoPago(TipoPago.TRANSFERENCIA)
                .referencia("REF123")
                .estado(EstadoPago.APLICADO)
                .build();
    }

    @AfterEach
    void tearDown() {
        tenantContextMock.close();
    }

    @Test
    void getAllCargos_shouldReturnCargos() {
        when(cargoRepository.findByEmpresaId(EMPRESA_ID))
                .thenReturn(Arrays.asList(cargo));

        List<CargoDTO> result = pagoService.getAllCargos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConcepto()).isEqualTo("Renta Enero 2025");
    }

    @Test
    void getCargosByContrato_shouldReturnCargosByContract() {
        when(cargoRepository.findByContratoIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Arrays.asList(cargo));

        List<CargoDTO> result = pagoService.getCargosByContrato(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void createCargo_shouldCreateCargo() {
        CreateCargoRequest request = new CreateCargoRequest();
        request.setContratoId(1L);
        request.setTipoCargo(TipoCargo.RENTA);
        request.setConcepto("Renta Febrero");
        request.setMontoOriginal(BigDecimal.valueOf(12000));
        request.setFechaCargo(LocalDate.now());
        request.setFechaVencimiento(LocalDate.now().plusMonths(1));

        when(contratoRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(contrato));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);

        CargoDTO result = pagoService.createCargo(request);

        assertThat(result).isNotNull();
        verify(cargoRepository).save(any(Cargo.class));
    }

    @Test
    void getAllPagos_shouldReturnPagos() {
        when(pagoRepository.findByEmpresaId(EMPRESA_ID))
                .thenReturn(Arrays.asList(pago));
        when(pagoAplicacionRepository.findByPagoId(1L))
                .thenReturn(Arrays.asList());

        List<PagoDTO> result = pagoService.getAllPagos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMonto()).isEqualTo(BigDecimal.valueOf(12000));
    }

    @Test
    void getPagosByContrato_shouldReturnPagosByContract() {
        when(pagoRepository.findPagosByContratoOrdenados(EMPRESA_ID, 1L))
                .thenReturn(Arrays.asList(pago));
        when(pagoAplicacionRepository.findByPagoId(1L))
                .thenReturn(Arrays.asList());

        List<PagoDTO> result = pagoService.getPagosByContrato(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void createPago_shouldCreatePago() {
        CreatePagoRequest request = new CreatePagoRequest();
        request.setContratoId(1L);
        request.setPersonaId(1L);
        request.setMonto(BigDecimal.valueOf(12000));
        request.setTipoPago(TipoPago.TRANSFERENCIA);
        request.setFechaPago(LocalDate.now());
        request.setReferencia("REF456");

        when(contratoRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(contrato));
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(arrendatario));
        when(pagoRepository.findUltimoNumeroRecibo(EMPRESA_ID)).thenReturn(null);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(pagoAplicacionRepository.findByPagoId(1L))
                .thenReturn(Arrays.asList());

        PagoDTO result = pagoService.createPago(request);

        assertThat(result).isNotNull();
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void getCargosPendientes_shouldReturnPendingCargos() {
        when(cargoRepository.findByEmpresaIdAndEstadoIn(eq(EMPRESA_ID), any()))
                .thenReturn(Arrays.asList(cargo));

        List<CargoDTO> result = pagoService.getCargosPendientes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(EstadoCargo.PENDIENTE);
    }

    @Test
    void getCargosVencidos_shouldReturnOverdueCargos() {
        when(cargoRepository.findCargosVencidos(eq(EMPRESA_ID), any(LocalDate.class)))
                .thenReturn(Arrays.asList(cargo));

        List<CargoDTO> result = pagoService.getCargosVencidos();

        assertThat(result).hasSize(1);
    }
}
