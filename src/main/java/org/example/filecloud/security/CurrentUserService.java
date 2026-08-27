package org.example.filecloud.security;

import org.example.filecloud.dao.User;
import org.example.filecloud.service.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CustomUserDetail details =
                (CustomUserDetail) authentication.getPrincipal();

        return details.getUser();
    }
}