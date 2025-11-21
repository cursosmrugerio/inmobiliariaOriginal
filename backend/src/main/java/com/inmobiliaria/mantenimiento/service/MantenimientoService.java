package com.inmobiliaria.mantenimiento.service;

import com.inmobiliaria.mantenimiento.domain.*;
import com.inmobiliaria.mantenimiento.dto.*;
import com.inmobiliaria.mantenimiento.repository.*;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MantenimientoService {

    private final ProveedorRepository proveedorRepository;
    private final OrdenMantenimientoRepository ordenRepository;
    private final SeguimientoOrdenRepository seguimientoRepository;

    // ==================== PROVEEDORES ====================

    @Transactional(readOnly = true)
    public List<ProveedorDTO> findAllProveedores() {
        Long empresaId = TenantContext.getCurrentTenant();
        return proveedorRepository.findByEmpresaIdOrderByNombreAsc(empresaId)
                .stream()
                .map(ProveedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProveedorDTO> findProveedoresActivos() {
        Long empresaId = TenantContext.getCurrentTenant();
        return proveedorRepository.findByEmpresaIdAndActivoTrueOrderByNombreAsc(empresaId)
                .stream()
                .map(ProveedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProveedorDTO findProveedorById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Proveedor proveedor = proveedorRepository.findById(id)
                .filter(p -> p.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));
        return ProveedorDTO.fromEntity(proveedor);
    }

    @Transactional(readOnly = true)
    public List<ProveedorDTO> findProveedoresByCategoria(CategoriaMantenimiento categoria) {
        Long empresaId = TenantContext.getCurrentTenant();
        return proveedorRepository.findActivosByEmpresaIdAndCategoria(empresaId, categoria)
                .stream()
                .map(ProveedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProveedorDTO createProveedor(CreateProveedorRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Proveedor proveedor = Proveedor.builder()
                .empresaId(empresaId)
                .nombre(request.getNombre())
                .razonSocial(request.getRazonSocial())
                .rfc(request.getRfc())
                .telefonoPrincipal(request.getTelefonoPrincipal())
                .telefonoSecundario(request.getTelefonoSecundario())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .codigoPostal(request.getCodigoPostal())
                .ciudad(request.getCiudad())
                .estado(request.getEstado())
                .nombreContacto(request.getNombreContacto())
                .categorias(request.getCategorias())
                .notas(request.getNotas())
                .activo(true)
                .build();

        proveedor = proveedorRepository.save(proveedor);
        log.info("Proveedor creado: {}", proveedor.getId());
        return ProveedorDTO.fromEntity(proveedor);
    }

    @Transactional
    public ProveedorDTO updateProveedor(Long id, UpdateProveedorRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        Proveedor proveedor = proveedorRepository.findById(id)
                .filter(p -> p.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        proveedor.setNombre(request.getNombre());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setRfc(request.getRfc());
        proveedor.setTelefonoPrincipal(request.getTelefonoPrincipal());
        proveedor.setTelefonoSecundario(request.getTelefonoSecundario());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setCodigoPostal(request.getCodigoPostal());
        proveedor.setCiudad(request.getCiudad());
        proveedor.setEstado(request.getEstado());
        proveedor.setNombreContacto(request.getNombreContacto());
        if (request.getCategorias() != null) {
            proveedor.setCategorias(request.getCategorias());
        }
        proveedor.setNotas(request.getNotas());
        if (request.getActivo() != null) {
            proveedor.setActivo(request.getActivo());
        }

        proveedor = proveedorRepository.save(proveedor);
        log.info("Proveedor actualizado: {}", proveedor.getId());
        return ProveedorDTO.fromEntity(proveedor);
    }

    @Transactional
    public void deleteProveedor(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Proveedor proveedor = proveedorRepository.findById(id)
                .filter(p -> p.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        proveedorRepository.delete(proveedor);
        log.info("Proveedor eliminado: {}", id);
    }

    // ==================== ÓRDENES DE MANTENIMIENTO ====================

    @Transactional(readOnly = true)
    public List<OrdenMantenimientoDTO> findAllOrdenes() {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findByEmpresaIdOrderByFechaCreacionDesc(empresaId)
                .stream()
                .map(OrdenMantenimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenMantenimientoDTO> findOrdenesActivas() {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findOrdenesActivas(empresaId)
                .stream()
                .map(OrdenMantenimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenMantenimientoDTO findOrdenById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        OrdenMantenimiento orden = ordenRepository.findById(id)
                .filter(o -> o.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Orden de mantenimiento no encontrada"));
        return OrdenMantenimientoDTO.fromEntity(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenMantenimientoDTO> findOrdenesByPropiedad(Long propiedadId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findByEmpresaIdAndPropiedadIdOrderByFechaCreacionDesc(empresaId, propiedadId)
                .stream()
                .map(OrdenMantenimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenMantenimientoDTO> findOrdenesByProveedor(Long proveedorId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findByEmpresaIdAndProveedorIdOrderByFechaCreacionDesc(empresaId, proveedorId)
                .stream()
                .map(OrdenMantenimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenMantenimientoDTO> findOrdenesByEstado(EstadoOrden estado) {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findByEmpresaIdAndEstadoOrderByPrioridadDescFechaSolicitudAsc(empresaId, estado)
                .stream()
                .map(OrdenMantenimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenMantenimientoDTO> findOrdenesProgramadas(LocalDate inicio, LocalDate fin) {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findProgramadasEnRango(empresaId, inicio, fin)
                .stream()
                .map(OrdenMantenimientoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdenMantenimientoDTO createOrden(CreateOrdenRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        OrdenMantenimiento orden = OrdenMantenimiento.builder()
                .empresaId(empresaId)
                .propiedadId(request.getPropiedadId())
                .proveedorId(request.getProveedorId())
                .solicitanteId(request.getSolicitanteId())
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .prioridad(request.getPrioridad())
                .estado(EstadoOrden.PENDIENTE)
                .fechaSolicitud(LocalDate.now())
                .fechaProgramada(request.getFechaProgramada())
                .costoEstimado(request.getCostoEstimado())
                .notasTecnicas(request.getNotasTecnicas())
                .build();

        orden = ordenRepository.save(orden);

        // Registrar seguimiento inicial
        registrarSeguimiento(orden.getId(), null, EstadoOrden.PENDIENTE, "Orden creada");

        log.info("Orden de mantenimiento creada: {}", orden.getId());
        return OrdenMantenimientoDTO.fromEntity(orden);
    }

    @Transactional
    public OrdenMantenimientoDTO updateOrden(Long id, UpdateOrdenRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        OrdenMantenimiento orden = ordenRepository.findById(id)
                .filter(o -> o.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Orden de mantenimiento no encontrada"));

        EstadoOrden estadoAnterior = orden.getEstado();

        orden.setProveedorId(request.getProveedorId());
        orden.setSolicitanteId(request.getSolicitanteId());
        orden.setTitulo(request.getTitulo());
        orden.setDescripcion(request.getDescripcion());
        orden.setCategoria(request.getCategoria());
        orden.setPrioridad(request.getPrioridad());
        if (request.getEstado() != null) {
            orden.setEstado(request.getEstado());
        }
        orden.setFechaProgramada(request.getFechaProgramada());
        orden.setFechaInicio(request.getFechaInicio());
        orden.setFechaCompletada(request.getFechaCompletada());
        orden.setCostoEstimado(request.getCostoEstimado());
        orden.setCostoFinal(request.getCostoFinal());
        orden.setNotasTecnicas(request.getNotasTecnicas());
        orden.setNotasCierre(request.getNotasCierre());

        // Registrar cambio de estado si aplica
        if (request.getEstado() != null && !request.getEstado().equals(estadoAnterior)) {
            registrarSeguimiento(orden.getId(), estadoAnterior, request.getEstado(), "Estado actualizado");
        }

        orden = ordenRepository.save(orden);
        log.info("Orden de mantenimiento actualizada: {}", orden.getId());
        return OrdenMantenimientoDTO.fromEntity(orden);
    }

    @Transactional
    public OrdenMantenimientoDTO cambiarEstadoOrden(Long id, CambiarEstadoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        OrdenMantenimiento orden = ordenRepository.findById(id)
                .filter(o -> o.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Orden de mantenimiento no encontrada"));

        EstadoOrden estadoAnterior = orden.getEstado();
        orden.setEstado(request.getEstado());

        // Actualizar fechas según el estado
        if (request.getEstado() == EstadoOrden.EN_PROCESO && orden.getFechaInicio() == null) {
            orden.setFechaInicio(LocalDate.now());
        } else if (request.getEstado() == EstadoOrden.COMPLETADA) {
            orden.setFechaCompletada(LocalDate.now());
            if (request.getCostoFinal() != null) {
                orden.setCostoFinal(request.getCostoFinal());
            }
            if (request.getNotasCierre() != null) {
                orden.setNotasCierre(request.getNotasCierre());
            }
        }

        // Registrar seguimiento
        registrarSeguimiento(orden.getId(), estadoAnterior, request.getEstado(), request.getComentario());

        orden = ordenRepository.save(orden);
        log.info("Estado de orden {} cambiado de {} a {}", orden.getId(), estadoAnterior, request.getEstado());
        return OrdenMantenimientoDTO.fromEntity(orden);
    }

    @Transactional
    public void deleteOrden(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        OrdenMantenimiento orden = ordenRepository.findById(id)
                .filter(o -> o.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Orden de mantenimiento no encontrada"));

        seguimientoRepository.deleteByOrdenId(id);
        ordenRepository.delete(orden);
        log.info("Orden de mantenimiento eliminada: {}", id);
    }

    // ==================== SEGUIMIENTO ====================

    @Transactional(readOnly = true)
    public List<SeguimientoOrdenDTO> findSeguimientoByOrden(Long ordenId) {
        return seguimientoRepository.findByOrdenIdOrderByFechaRegistroDesc(ordenId)
                .stream()
                .map(SeguimientoOrdenDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void registrarSeguimiento(Long ordenId, EstadoOrden estadoAnterior, EstadoOrden estadoNuevo, String comentario) {
        SeguimientoOrden seguimiento = SeguimientoOrden.builder()
                .ordenId(ordenId)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .comentario(comentario)
                .build();
        seguimientoRepository.save(seguimiento);
    }

    // ==================== ESTADÍSTICAS ====================

    @Transactional(readOnly = true)
    public Map<String, Object> getEstadisticas() {
        Long empresaId = TenantContext.getCurrentTenant();
        Map<String, Object> stats = new HashMap<>();

        stats.put("proveedoresActivos", proveedorRepository.countActivosByEmpresaId(empresaId));
        stats.put("ordenesPendientes", ordenRepository.countByEmpresaIdAndEstado(empresaId, EstadoOrden.PENDIENTE));
        stats.put("ordenesEnProceso", ordenRepository.countByEmpresaIdAndEstado(empresaId, EstadoOrden.EN_PROCESO));
        stats.put("ordenesCompletadas", ordenRepository.countByEmpresaIdAndEstado(empresaId, EstadoOrden.COMPLETADA));

        // Costos del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = inicioMes.plusMonths(1).minusDays(1);
        BigDecimal costosMes = ordenRepository.sumCostosCompletadosEnRango(empresaId, inicioMes, finMes);
        stats.put("costosMesActual", costosMes != null ? costosMes : BigDecimal.ZERO);

        return stats;
    }
}
