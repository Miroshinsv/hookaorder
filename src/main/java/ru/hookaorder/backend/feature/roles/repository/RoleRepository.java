package ru.hookaorder.backend.feature.roles.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hookaorder.backend.feature.roles.entity.RoleEntity;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
}
