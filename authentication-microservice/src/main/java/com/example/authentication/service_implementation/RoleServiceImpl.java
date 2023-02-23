package com.example.authentication.service_implementation;

import com.example.authentication.model.EnumRole;
import com.example.authentication.model.Role;
import com.example.authentication.repository.RoleRepository;
import com.example.authentication.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findByRole(EnumRole enumRole) {
        return roleRepository.findByRole(enumRole).orElseThrow(RuntimeException::new);
    }
}
