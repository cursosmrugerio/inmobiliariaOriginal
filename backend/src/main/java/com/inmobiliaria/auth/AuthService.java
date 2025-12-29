package com.inmobiliaria.auth;

import com.inmobiliaria.auth.dto.*;
import com.inmobiliaria.empresa.Empresa;
import com.inmobiliaria.empresa.EmpresaRepository;
import com.inmobiliaria.empresa.Usuario;
import com.inmobiliaria.empresa.UsuarioRepository;
import com.inmobiliaria.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new BadCredentialsException("Usuario desactivado");
        }

        Empresa empresa = empresaRepository.findById(usuario.getEmpresaId())
                .orElseThrow(() -> new BadCredentialsException("Empresa no encontrada"));

        String token = jwtService.generateToken(usuario, usuario.getEmpresaId());

        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .empresaId(usuario.getEmpresaId())
                .empresaNombre(empresa.getNombre())
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verificar que el email no existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Verificar que la empresa existe
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        if (!empresa.isActivo()) {
            throw new IllegalArgumentException("La empresa no está activa");
        }

        // Crear usuario con rol AGENTE por defecto
        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .empresaId(request.getEmpresaId())
                .rol(Usuario.Rol.AGENTE)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario, usuario.getEmpresaId());

        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .empresaId(usuario.getEmpresaId())
                .empresaNombre(empresa.getNombre())
                .build();
    }

    public UserDTO getCurrentUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Empresa empresa = empresaRepository.findById(usuario.getEmpresaId())
                .orElse(null);

        return UserDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .empresaId(usuario.getEmpresaId())
                .empresaNombre(empresa != null ? empresa.getNombre() : null)
                .activo(usuario.isActivo())
                .build();
    }
}
