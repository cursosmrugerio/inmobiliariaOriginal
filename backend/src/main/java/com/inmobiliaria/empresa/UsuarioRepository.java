package com.inmobiliaria.empresa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    // Methods for user management (multi-tenant)
    List<Usuario> findByEmpresaId(Long empresaId);
    List<Usuario> findByEmpresaIdAndActivo(Long empresaId, boolean activo);
    Optional<Usuario> findByIdAndEmpresaId(Long id, Long empresaId);
    boolean existsByEmailAndEmpresaId(String email, Long empresaId);
    boolean existsByEmailAndEmpresaIdAndIdNot(String email, Long empresaId, Long id);
}
