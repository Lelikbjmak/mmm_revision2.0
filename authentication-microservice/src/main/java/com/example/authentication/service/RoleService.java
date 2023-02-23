package com.example.authentication.service;

import com.example.authentication.model.EnumRole;
import com.example.authentication.model.Role;

public interface RoleService {
    Role findByRole(EnumRole role);
}
