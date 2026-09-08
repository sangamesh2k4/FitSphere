package com.sangamesh.Fitsphere.dto.admin;

import com.sangamesh.Fitsphere.enums.Role;
import lombok.Data;

@Data
public class AdminUserSummaryDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private Boolean enabled;
}