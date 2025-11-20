package com.inmobiliaria.empresa;

import com.inmobiliaria.empresa.dto.CreateEmpresaRequest;
import com.inmobiliaria.empresa.dto.EmpresaDTO;
import com.inmobiliaria.empresa.dto.UpdateEmpresaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public List<EmpresaDTO> getAllActive() {
        return empresaRepository.findByActivoTrue()
                .stream()
                .map(EmpresaDTO::fromEntity)
                .toList();
    }

    public List<EmpresaDTO> getAll() {
        return empresaRepository.findAll()
                .stream()
                .map(EmpresaDTO::fromEntity)
                .toList();
    }

    public EmpresaDTO getById(Long id) {
        return empresaRepository.findById(id)
                .map(EmpresaDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));
    }

    public EmpresaDTO getActiveById(Long id) {
        return empresaRepository.findByIdAndActivoTrue(id)
                .map(EmpresaDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Empresa activa no encontrada con id: " + id));
    }

    @Transactional
    public EmpresaDTO create(CreateEmpresaRequest request) {
        if (request.getRfc() != null && empresaRepository.existsByRfc(request.getRfc())) {
            throw new RuntimeException("Ya existe una empresa con el RFC: " + request.getRfc());
        }

        Empresa empresa = Empresa.builder()
                .nombre(request.getNombre())
                .rfc(request.getRfc())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .activo(true)
                .build();

        empresa = empresaRepository.save(empresa);
        return EmpresaDTO.fromEntity(empresa);
    }

    @Transactional
    public EmpresaDTO update(Long id, UpdateEmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));

        if (request.getNombre() != null) {
            empresa.setNombre(request.getNombre());
        }
        if (request.getRfc() != null) {
            if (!request.getRfc().equals(empresa.getRfc()) && empresaRepository.existsByRfc(request.getRfc())) {
                throw new RuntimeException("Ya existe una empresa con el RFC: " + request.getRfc());
            }
            empresa.setRfc(request.getRfc());
        }
        if (request.getDireccion() != null) {
            empresa.setDireccion(request.getDireccion());
        }
        if (request.getTelefono() != null) {
            empresa.setTelefono(request.getTelefono());
        }
        if (request.getEmail() != null) {
            empresa.setEmail(request.getEmail());
        }
        if (request.getActivo() != null) {
            empresa.setActivo(request.getActivo());
        }

        empresa = empresaRepository.save(empresa);
        return EmpresaDTO.fromEntity(empresa);
    }

    @Transactional
    public void delete(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));
        empresa.setActivo(false);
        empresaRepository.save(empresa);
    }

    @Transactional
    public void hardDelete(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new RuntimeException("Empresa no encontrada con id: " + id);
        }
        empresaRepository.deleteById(id);
    }
}
