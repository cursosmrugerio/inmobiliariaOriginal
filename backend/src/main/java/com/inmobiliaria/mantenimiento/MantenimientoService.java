package com.inmobiliaria.mantenimiento;

import com.inmobiliaria.mantenimiento.dto.*;
import com.inmobiliaria.propiedad.Propiedad;
import com.inmobiliaria.propiedad.PropiedadRepository;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MantenimientoService {

    private final ProveedorRepository proveedorRepository;
    private final OrdenMantenimientoRepository ordenRepository;
    private final SeguimientoOrdenRepository seguimientoRepository;
    private final PropiedadRepository propiedadRepository;

    // --- Proveedores ---

    public List<ProveedorDTO> getAllProveedores() {
        Long empresaId = TenantContext.getCurrentTenant();
        return proveedorRepository.findByEmpresaIdAndActivoTrue(empresaId)
                .stream()
                .map(this::toProveedorDTO)
                .collect(Collectors.toList());
    }

    public ProveedorDTO getProveedorById(Long id) {
        return toProveedorDTO(findProveedorById(id));
    }

    public ProveedorDTO createProveedor(CreateProveedorRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Proveedor proveedor = Proveedor.builder()
                .empresaId(empresaId)
                .nombre(request.getNombre())
                .razonSocial(request.getRazonSocial())
                .rfc(request.getRfc())
                .especialidad(request.getEspecialidad())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .notas(request.getNotas())
                .activo(true)
                .build();

        return toProveedorDTO(proveedorRepository.save(proveedor));
    }

    public ProveedorDTO updateProveedor(Long id, CreateProveedorRequest request) {
        Proveedor proveedor = findProveedorById(id);

        proveedor.setNombre(request.getNombre());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setRfc(request.getRfc());
        proveedor.setEspecialidad(request.getEspecialidad());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setNotas(request.getNotas());

        return toProveedorDTO(proveedorRepository.save(proveedor));
    }

    public void deleteProveedor(Long id) {
        Proveedor proveedor = findProveedorById(id);
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    // --- Órdenes de Mantenimiento ---

    public List<OrdenMantenimientoDTO> getAllOrdenes(EstadoOrden estado) {
        Long empresaId = TenantContext.getCurrentTenant();
        List<OrdenMantenimiento> ordenes = estado != null
                ? ordenRepository.findByEmpresaIdAndEstadoOrderByCreatedAtDesc(empresaId, estado)
                : ordenRepository.findByEmpresaIdOrderByCreatedAtDesc(empresaId);
        return ordenes.stream().map(this::toOrdenDTO).collect(Collectors.toList());
    }

    public OrdenMantenimientoDTO getOrdenById(Long id) {
        return toOrdenDTO(findOrdenById(id));
    }

    public OrdenMantenimientoDTO createOrden(CreateOrdenRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();

        Propiedad propiedad = propiedadRepository.findById(request.getPropiedadId())
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada"));

        OrdenMantenimiento orden = OrdenMantenimiento.builder()
                .empresaId(empresaId)
                .numeroOrden(generateNumeroOrden())
                .propiedad(propiedad)
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .prioridad(request.getPrioridad() != null ? request.getPrioridad() : PrioridadOrden.MEDIA)
                .estado(EstadoOrden.PENDIENTE)
                .fechaSolicitud(LocalDate.now())
                .fechaProgramada(request.getFechaProgramada())
                .costoEstimado(request.getCostoEstimado())
                .notas(request.getNotas())
                .build();

        if (request.getProveedorId() != null) {
            Proveedor proveedor = findProveedorById(request.getProveedorId());
            orden.setProveedor(proveedor);
        }

        return toOrdenDTO(ordenRepository.save(orden));
    }

    public OrdenMantenimientoDTO updateOrden(Long id, UpdateOrdenRequest request) {
        OrdenMantenimiento orden = findOrdenById(id);
        EstadoOrden estadoAnterior = orden.getEstado();

        if (request.getProveedorId() != null) {
            Proveedor proveedor = findProveedorById(request.getProveedorId());
            orden.setProveedor(proveedor);
        }
        if (request.getTitulo() != null) {
            orden.setTitulo(request.getTitulo());
        }
        if (request.getDescripcion() != null) {
            orden.setDescripcion(request.getDescripcion());
        }
        if (request.getPrioridad() != null) {
            orden.setPrioridad(request.getPrioridad());
        }
        if (request.getEstado() != null && request.getEstado() != estadoAnterior) {
            orden.setEstado(request.getEstado());
            if (request.getEstado() == EstadoOrden.COMPLETADA && orden.getFechaCompletada() == null) {
                orden.setFechaCompletada(LocalDate.now());
            }
        }
        if (request.getFechaProgramada() != null) {
            orden.setFechaProgramada(request.getFechaProgramada());
        }
        if (request.getFechaCompletada() != null) {
            orden.setFechaCompletada(request.getFechaCompletada());
        }
        if (request.getCostoEstimado() != null) {
            orden.setCostoEstimado(request.getCostoEstimado());
        }
        if (request.getCostoFinal() != null) {
            orden.setCostoFinal(request.getCostoFinal());
        }
        if (request.getNotas() != null) {
            orden.setNotas(request.getNotas());
        }

        return toOrdenDTO(ordenRepository.save(orden));
    }

    // --- Seguimiento ---

    public List<SeguimientoOrdenDTO> getSeguimientosByOrden(Long ordenId) {
        findOrdenById(ordenId); // Validate access
        return seguimientoRepository.findByOrdenMantenimientoIdOrderByCreatedAtDesc(ordenId)
                .stream()
                .map(this::toSeguimientoDTO)
                .collect(Collectors.toList());
    }

    public SeguimientoOrdenDTO addSeguimiento(Long ordenId, CreateSeguimientoRequest request) {
        Long empresaId = TenantContext.getCurrentTenant();
        OrdenMantenimiento orden = findOrdenById(ordenId);
        EstadoOrden estadoAnterior = orden.getEstado();

        SeguimientoOrden seguimiento = SeguimientoOrden.builder()
                .empresaId(empresaId)
                .ordenMantenimiento(orden)
                .comentario(request.getComentario())
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(request.getNuevoEstado() != null ? request.getNuevoEstado() : estadoAnterior)
                .build();

        if (request.getNuevoEstado() != null && request.getNuevoEstado() != estadoAnterior) {
            orden.setEstado(request.getNuevoEstado());
            if (request.getNuevoEstado() == EstadoOrden.COMPLETADA) {
                orden.setFechaCompletada(LocalDate.now());
            }
            ordenRepository.save(orden);
        }

        return toSeguimientoDTO(seguimientoRepository.save(seguimiento));
    }

    // --- Helper methods ---

    private Proveedor findProveedorById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        return proveedorRepository.findById(id)
                .filter(p -> p.getEmpresaId().equals(empresaId) && p.isActivo())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado: " + id));
    }

    private OrdenMantenimiento findOrdenById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        return ordenRepository.findById(id)
                .filter(o -> o.getEmpresaId().equals(empresaId))
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada: " + id));
    }

    private String generateNumeroOrden() {
        return "OM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ProveedorDTO toProveedorDTO(Proveedor proveedor) {
        return ProveedorDTO.builder()
                .id(proveedor.getId())
                .nombre(proveedor.getNombre())
                .razonSocial(proveedor.getRazonSocial())
                .rfc(proveedor.getRfc())
                .especialidad(proveedor.getEspecialidad())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .notas(proveedor.getNotas())
                .createdAt(proveedor.getCreatedAt())
                .build();
    }

    private OrdenMantenimientoDTO toOrdenDTO(OrdenMantenimiento orden) {
        return OrdenMantenimientoDTO.builder()
                .id(orden.getId())
                .numeroOrden(orden.getNumeroOrden())
                .propiedadId(orden.getPropiedad().getId())
                .propiedadNombre(orden.getPropiedad().getNombre())
                .proveedorId(orden.getProveedor() != null ? orden.getProveedor().getId() : null)
                .proveedorNombre(orden.getProveedor() != null ? orden.getProveedor().getNombre() : null)
                .titulo(orden.getTitulo())
                .descripcion(orden.getDescripcion())
                .prioridad(orden.getPrioridad())
                .estado(orden.getEstado())
                .fechaSolicitud(orden.getFechaSolicitud())
                .fechaProgramada(orden.getFechaProgramada())
                .fechaCompletada(orden.getFechaCompletada())
                .costoEstimado(orden.getCostoEstimado())
                .costoFinal(orden.getCostoFinal())
                .notas(orden.getNotas())
                .createdAt(orden.getCreatedAt())
                .build();
    }

    private SeguimientoOrdenDTO toSeguimientoDTO(SeguimientoOrden seguimiento) {
        return SeguimientoOrdenDTO.builder()
                .id(seguimiento.getId())
                .ordenId(seguimiento.getOrdenMantenimiento().getId())
                .comentario(seguimiento.getComentario())
                .estadoAnterior(seguimiento.getEstadoAnterior())
                .estadoNuevo(seguimiento.getEstadoNuevo())
                .createdAt(seguimiento.getCreatedAt())
                .build();
    }
}
