package com.justintime.jit.repository;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    List<Permissions> findAllByRole(Role role);
    Optional<Permissions> findByPermissionCode(String code);
    Optional<Permissions> findByTitle(String title);
    Set<Permissions> findAllByPermissionCodeIn(Set<String> permissionCodes);
}
