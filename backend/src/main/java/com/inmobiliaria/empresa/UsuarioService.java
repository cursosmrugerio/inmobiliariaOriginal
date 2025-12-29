package com.inmobiliaria.empresa;

import com.inmobiliaria.empresa.dto.CreateUsuarioRequest;
import com.inmobiliaria.empresa.dto.UpdateUsuarioRequest;
import com.inmobiliaria.empresa.dto.UsuarioDTO;
import com.inmobiliaria.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    private Long getCurrentEmpresaId() {
        Long empresaId = TenantContext.getCurrentTenant();
        if (empresaId == null) {
            throw new IllegalStateException("No hay empresa en el contexto actual");
        }
        return empresaId;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> getAllUsuarios() {
        Long empresaId = getCurrentEmpresaId();
        return usuarioRepository.findByEmpresaId(empresaId).stream()
                .map(UsuarioDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> getUsuariosByActivo(boolean activo) {
        Long empresaId = getCurrentEmpresaId();
        return usuarioRepository.findByEmpresaIdAndActivo(empresaId, activo).stream()
                .map(UsuarioDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getUsuarioById(Long id) {
        Long empresaId = getCurrentEmpresaId();
        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDTO createUsuario(CreateUsuarioRequest request) {
        Long empresaId = getCurrentEmpresaId();

        // Verificar que la empresa existe y está activa
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        if (!empresa.isActivo()) {
            throw new IllegalArgumentException("La empresa no está activa");
        }

        // Verificar que el email no existe en esta empresa
        if (usuarioRepository.existsByEmailAndEmpresaId(request.getEmail(), empresaId)) {
            throw new IllegalArgumentException("Ya existe un usuario con este email en la empresa");
        }

        // Verificar que el email no existe globalmente (email es unique en la tabla)
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado en el sistema");
        }

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .rol(request.getRol())
                .empresaId(empresaId)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDTO updateUsuario(Long id, UpdateUsuarioRequest request, String currentUserEmail) {
        Long empresaId = getCurrentEmpresaId();

        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar email único si se está cambiando
        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmailAndEmpresaIdAndIdNot(request.getEmail(), empresaId, id)) {
                throw new IllegalArgumentException("Ya existe un usuario con este email en la empresa");
            }
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado en el sistema");
            }
            usuario.setEmail(request.getEmail());
        }

        // No permitir que un usuario cambie su propio rol
        if (request.getRol() != null && usuario.getEmail().equals(currentUserEmail)) {
            throw new IllegalArgumentException("No puede cambiar su propio rol");
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }

        if (request.getApellido() != null) {
            usuario.setApellido(request.getApellido());
        }

        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }

        if (request.getActivo() != null) {
            // No permitir que un usuario se desactive a sí mismo
            if (!request.getActivo() && usuario.getEmail().equals(currentUserEmail)) {
                throw new IllegalArgumentException("No puede desactivarse a sí mismo");
            }
            usuario.setActivo(request.getActivo());
        }

        usuario = usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDTO cambiarRol(Long id, Usuario.Rol nuevoRol, String currentUserEmail) {
        Long empresaId = getCurrentEmpresaId();

        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // No permitir que un usuario cambie su propio rol
        if (usuario.getEmail().equals(currentUserEmail)) {
            throw new IllegalArgumentException("No puede cambiar su propio rol");
        }

        usuario.setRol(nuevoRol);
        usuario = usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public void deleteUsuario(Long id, String currentUserEmail) {
        Long empresaId = getCurrentEmpresaId();

        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // No permitir que un usuario se elimine a sí mismo
        if (usuario.getEmail().equals(currentUserEmail)) {
            throw new IllegalArgumentException("No puede eliminarse a sí mismo");
        }

        // Soft delete - desactivar en lugar de eliminar
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioDTO toggleActivo(Long id, String currentUserEmail) {
        Long empresaId = getCurrentEmpresaId();

        Usuario usuario = usuarioRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // No permitir que un usuario se desactive a sí mismo
        if (usuario.getEmail().equals(currentUserEmail)) {
            throw new IllegalArgumentException("No puede cambiar su propio estado");
        }

        usuario.setActivo(!usuario.isActivo());
        usuario = usuarioRepository.save(usuario);
        return UsuarioDTO.fromEntity(usuario);
    }
}
