package com.inmobiliaria.contrato;

import com.inmobiliaria.empresa.Empresa;
import com.inmobiliaria.empresa.EmpresaRepository;
import com.inmobiliaria.persona.Persona;
import com.inmobiliaria.persona.PersonaRepository;
import com.inmobiliaria.persona.TipoPersona;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.propiedad.PropiedadRepository;
import com.inmobiliaria.propiedad.TipoPropiedad;
import com.inmobiliaria.propiedad.TipoPropiedadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Testcontainers
class ContratoRepositoryIntegrationTest {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private TipoPropiedadRepository tipoPropiedadRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private Contrato contrato;
    private Propiedad propiedad;
    private Persona arrendatario;
    private final Long EMPRESA_ID = 1L;

    @BeforeEach
    void setUp() {
        contratoRepository.deleteAll();
        propiedadRepository.deleteAll();
        personaRepository.deleteAll();

        // Create empresa
        Empresa empresa = Empresa.builder()
                .nombre("Test Empresa")
                .activo(true)
                .build();
        empresa = empresaRepository.save(empresa);

        TipoPropiedad tipoPropiedad = new TipoPropiedad();
        tipoPropiedad.setNombre("DEPARTAMENTO");
        tipoPropiedad.setDescripcion("Departamento");
        tipoPropiedad = tipoPropiedadRepository.save(tipoPropiedad);

        propiedad = Propiedad.builder()
                .empresaId(empresa.getId())
                .tipoPropiedad(tipoPropiedad)
                .nombre("Test Property")
                .direccionCompleta("Test Address")
                .superficie(BigDecimal.valueOf(100))
                .precioRenta(BigDecimal.valueOf(12000))
                .disponible(true)
                .activo(true)
                .build();
        propiedad = propiedadRepository.save(propiedad);

        arrendatario = Persona.builder()
                .empresaId(empresa.getId())
                .tipoPersona(TipoPersona.FISICA)
                .nombre("Juan")
                .apellidoPaterno("Perez")
                .activo(true)
                .build();
        arrendatario = personaRepository.save(arrendatario);

        contrato = Contrato.builder()
                .empresaId(empresa.getId())
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

    @Test
    void save_shouldPersistContrato() {
        Contrato saved = contratoRepository.save(contrato);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMontoRenta()).isEqualTo(BigDecimal.valueOf(12000));
    }

    @Test
    void findByEmpresaIdAndActivoTrue_shouldReturnActiveContratos() {
        contratoRepository.save(contrato);

        List<Contrato> contratos = contratoRepository.findByEmpresaIdAndActivoTrue(contrato.getEmpresaId());

        assertThat(contratos).hasSize(1);
    }

    @Test
    void findByIdAndEmpresaId_shouldReturnContrato_whenExists() {
        Contrato saved = contratoRepository.save(contrato);

        Optional<Contrato> found = contratoRepository.findByIdAndEmpresaId(saved.getId(), contrato.getEmpresaId());

        assertThat(found).isPresent();
    }

    @Test
    void findByPropiedadIdAndEmpresaId_shouldReturnContratosByProperty() {
        contratoRepository.save(contrato);

        List<Contrato> contratos = contratoRepository.findByPropiedadIdAndEmpresaId(propiedad.getId(), contrato.getEmpresaId());

        assertThat(contratos).hasSize(1);
    }

    @Test
    void findByArrendatarioIdAndEmpresaId_shouldReturnContratosByTenant() {
        contratoRepository.save(contrato);

        List<Contrato> contratos = contratoRepository.findByArrendatarioIdAndEmpresaId(arrendatario.getId(), contrato.getEmpresaId());

        assertThat(contratos).hasSize(1);
    }

    @Test
    void findContratosProximosAVencer_shouldReturnExpiringContratos() {
        contrato.setFechaFin(LocalDate.now().plusDays(15));
        contratoRepository.save(contrato);

        List<Contrato> contratos = contratoRepository.findContratosProximosAVencer(
                contrato.getEmpresaId(),
                LocalDate.now().plusDays(30)
        );

        assertThat(contratos).hasSize(1);
    }

    @Test
    void findContratosProximosAVencer_shouldNotReturnFarExpiringContratos() {
        contrato.setFechaFin(LocalDate.now().plusDays(60));
        contratoRepository.save(contrato);

        List<Contrato> contratos = contratoRepository.findContratosProximosAVencer(
                contrato.getEmpresaId(),
                LocalDate.now().plusDays(30)
        );

        assertThat(contratos).isEmpty();
    }

    @Test
    void findByEstadoAndEmpresaId_shouldReturnContratosByStatus() {
        contratoRepository.save(contrato);

        List<Contrato> contratos = contratoRepository.findByEstadoAndEmpresaId(EstadoContrato.ACTIVO, contrato.getEmpresaId());

        assertThat(contratos).hasSize(1);
    }
}
