package com.inmobiliaria.persona;

import com.inmobiliaria.catalogo.*;
import com.inmobiliaria.persona.dto.*;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private PersonaRolRepository personaRolRepository;
    @Mock
    private DireccionRepository direccionRepository;
    @Mock
    private CuentaBancariaRepository cuentaBancariaRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private EstadoRepository estadoRepository;
    @Mock
    private MunicipioRepository municipioRepository;
    @Mock
    private ColoniaRepository coloniaRepository;

    @InjectMocks
    private PersonaService personaService;

    private MockedStatic<TenantContext> tenantContextMock;
    private Persona persona;
    private final Long EMPRESA_ID = 1L;

    @BeforeEach
    void setUp() {
        tenantContextMock = mockStatic(TenantContext.class);
        tenantContextMock.when(TenantContext::getCurrentTenant).thenReturn(EMPRESA_ID);

        persona = Persona.builder()
                .id(1L)
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

    @AfterEach
    void tearDown() {
        tenantContextMock.close();
    }

    @Test
    void getAllPersonas_shouldReturnActivePersonas() {
        when(personaRepository.findByEmpresaIdAndActivoTrue(EMPRESA_ID))
                .thenReturn(Arrays.asList(persona));

        List<PersonaDTO> result = personaService.getAllPersonas(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Juan");
        verify(personaRepository).findByEmpresaIdAndActivoTrue(EMPRESA_ID);
    }

    @Test
    void getAllPersonas_shouldReturnAllPersonas_whenActiveOnlyIsFalse() {
        when(personaRepository.findByEmpresaId(EMPRESA_ID))
                .thenReturn(Arrays.asList(persona));

        List<PersonaDTO> result = personaService.getAllPersonas(false);

        assertThat(result).hasSize(1);
        verify(personaRepository).findByEmpresaId(EMPRESA_ID);
    }

    @Test
    void getPersonaById_shouldReturnPersona_whenExists() {
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));

        PersonaDTO result = personaService.getPersonaById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Juan");
    }

    @Test
    void getPersonaById_shouldThrowException_whenNotExists() {
        when(personaRepository.findByIdAndEmpresaId(99L, EMPRESA_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> personaService.getPersonaById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Persona no encontrada");
    }

    @Test
    void createPersona_shouldCreatePersona_whenRfcIsUnique() {
        CreatePersonaRequest request = new CreatePersonaRequest();
        request.setTipoPersona(TipoPersona.FISICA);
        request.setNombre("Maria");
        request.setApellidoPaterno("Lopez");
        request.setRfc("LOMA900101ABC");
        request.setEmail("maria@test.com");

        when(personaRepository.existsByRfcAndEmpresaId("LOMA900101ABC", EMPRESA_ID))
                .thenReturn(false);
        when(personaRepository.save(any(Persona.class))).thenReturn(persona);

        PersonaDTO result = personaService.createPersona(request);

        assertThat(result).isNotNull();
        verify(personaRepository).save(any(Persona.class));
    }

    @Test
    void createPersona_shouldThrowException_whenRfcExists() {
        CreatePersonaRequest request = new CreatePersonaRequest();
        request.setRfc("EXISTING123");

        when(personaRepository.existsByRfcAndEmpresaId("EXISTING123", EMPRESA_ID))
                .thenReturn(true);

        assertThatThrownBy(() -> personaService.createPersona(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ya existe una persona con este RFC");
    }

    @Test
    void updatePersona_shouldUpdatePersona_whenExists() {
        UpdatePersonaRequest request = new UpdatePersonaRequest();
        request.setNombre("Juan Carlos");

        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(personaRepository.save(any(Persona.class))).thenReturn(persona);

        PersonaDTO result = personaService.updatePersona(1L, request);

        assertThat(result).isNotNull();
        verify(personaRepository).save(any(Persona.class));
    }

    @Test
    void deletePersona_shouldDeletePersona_whenExists() {
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));

        personaService.deletePersona(1L);

        verify(personaRepository).delete(persona);
    }

    @Test
    void addRolToPersona_shouldAddRol_whenNotExists() {
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("ARRENDATARIO");

        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(personaRolRepository.existsByPersonaIdAndRolId(1L, 1))
                .thenReturn(false);
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(personaRolRepository.save(any(PersonaRol.class)))
                .thenAnswer(inv -> {
                    PersonaRol pr = inv.getArgument(0);
                    pr.setId(1L);
                    return pr;
                });

        PersonaRolDTO result = personaService.addRolToPersona(1L, 1);

        assertThat(result).isNotNull();
        verify(personaRolRepository).save(any(PersonaRol.class));
    }

    @Test
    void addRolToPersona_shouldThrowException_whenRolAlreadyExists() {
        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(personaRolRepository.existsByPersonaIdAndRolId(1L, 1))
                .thenReturn(true);

        assertThatThrownBy(() -> personaService.addRolToPersona(1L, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La persona ya tiene este rol asignado");
    }

    @Test
    void addDireccion_shouldAddDireccion() {
        CreateDireccionRequest request = new CreateDireccionRequest();
        request.setTipoDireccion(TipoDireccion.FISCAL);
        request.setCalle("Av. Principal");
        request.setNumeroExterior("123");
        request.setCodigoPostal("01000");
        request.setEsPrincipal(true);

        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(direccionRepository.findByPersonaIdAndEsPrincipalTrue(1L))
                .thenReturn(Optional.empty());
        when(direccionRepository.save(any(Direccion.class)))
                .thenAnswer(inv -> {
                    Direccion dir = inv.getArgument(0);
                    dir.setId(1L);
                    return dir;
                });

        DireccionDTO result = personaService.addDireccion(1L, request);

        assertThat(result).isNotNull();
        verify(direccionRepository).save(any(Direccion.class));
    }

    @Test
    void addCuentaBancaria_shouldAddCuenta() {
        CreateCuentaBancariaRequest request = new CreateCuentaBancariaRequest();
        request.setBanco("BBVA");
        request.setNumeroCuenta("1234567890");
        request.setClabe("012180012345678901");
        request.setTitular("Juan Perez");
        request.setEsPrincipal(true);

        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(cuentaBancariaRepository.findByPersonaIdAndEsPrincipalTrue(1L))
                .thenReturn(Optional.empty());
        when(cuentaBancariaRepository.save(any(CuentaBancaria.class)))
                .thenAnswer(inv -> {
                    CuentaBancaria cb = inv.getArgument(0);
                    cb.setId(1L);
                    return cb;
                });

        CuentaBancariaDTO result = personaService.addCuentaBancaria(1L, request);

        assertThat(result).isNotNull();
        verify(cuentaBancariaRepository).save(any(CuentaBancaria.class));
    }

    @Test
    void getPersonaDirecciones_shouldReturnDirecciones() {
        Direccion direccion = Direccion.builder()
                .id(1L)
                .persona(persona)
                .tipoDireccion(TipoDireccion.FISCAL)
                .calle("Test Street")
                .activo(true)
                .build();

        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(direccionRepository.findByPersonaIdAndActivoTrue(1L))
                .thenReturn(Arrays.asList(direccion));

        List<DireccionDTO> result = personaService.getPersonaDirecciones(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCalle()).isEqualTo("Test Street");
    }

    @Test
    void getPersonaCuentasBancarias_shouldReturnCuentas() {
        CuentaBancaria cuenta = CuentaBancaria.builder()
                .id(1L)
                .persona(persona)
                .banco("BBVA")
                .numeroCuenta("1234567890")
                .activo(true)
                .build();

        when(personaRepository.findByIdAndEmpresaId(1L, EMPRESA_ID))
                .thenReturn(Optional.of(persona));
        when(cuentaBancariaRepository.findByPersonaIdAndActivoTrue(1L))
                .thenReturn(Arrays.asList(cuenta));

        List<CuentaBancariaDTO> result = personaService.getPersonaCuentasBancarias(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBanco()).isEqualTo("BBVA");
    }
}
