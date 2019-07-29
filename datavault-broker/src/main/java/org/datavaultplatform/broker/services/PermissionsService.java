package org.datavaultplatform.broker.services;

import org.datavaultplatform.common.model.PermissionModel;
import org.datavaultplatform.common.model.RoleModel;
import org.datavaultplatform.common.model.RoleType;
import org.datavaultplatform.common.model.dao.PermissionDAO;
import org.datavaultplatform.common.model.dao.RoleDAO;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PermissionsService implements ApplicationListener<ContextRefreshedEvent> {

    private RoleDAO roleDao;

    private PermissionDAO permissionDao;

    public void setRoleDao(RoleDAO roleDao) {
        this.roleDao = roleDao;
    }

    public void setPermissionDao(PermissionDAO permissionDao) {
        this.permissionDao = permissionDao;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        permissionDao.synchronisePermissions();
    }

    public RoleModel createRole(RoleModel role) {
        if (roleExists(role.getId())) {
            throw new IllegalStateException("Cannot create a role that already exists");
        }
        validateRolePermissions(role);
        roleDao.store(role);
        return role;
    }

    private boolean roleExists(Long roleId) {
        return roleId != null && roleDao.find(roleId) != null;
    }

    private void validateRolePermissions(RoleModel role) {
        for (PermissionModel appliedPermission : role.getPermissions()) {
            RoleType roleType = role.getType();
            if (!appliedPermission.isAllowedForRoleType(roleType)) {
                String permissionType = appliedPermission.getType().name();
                String msg = "Unable to apply permission with type " + permissionType + " to role of type " + roleType;
                throw new IllegalStateException(msg);
            }
        }
    }

    public List<PermissionModel> getSchoolPermissions() {
        return getFilteredPermissions(PermissionModel::isSchoolPermission);
    }

    public List<PermissionModel> getVaultPermissions() {
        return getFilteredPermissions(PermissionModel::isVaultPermission);
    }

    private List<PermissionModel> getFilteredPermissions(Predicate<PermissionModel> filter) {
        return permissionDao.findAll().stream().filter(filter).collect(Collectors.toList());
    }

    public RoleModel getRole(long id) {
        return roleDao.find(id);
    }

    public List<RoleModel> getEditableRoles() {
        return roleDao.findAll().stream()
                .filter(role -> role.getType().isCustomCreatable())
                .collect(Collectors.toList());
    }

    public RoleModel updateRole(RoleModel role) {
        if (!roleExists(role.getId())) {
            throw new IllegalStateException("Cannot update a role that does not exist");
        }
        validateRolePermissions(role);
        roleDao.update(role);
        return role;
    }

    public void deleteRole(Long roleId) {
        if (!roleExists(roleId)) {
            throw new IllegalStateException("Cannot delete a role that does not exist");
        }
        roleDao.delete(roleId);
    }
}
