package com.ridelink.account.dto;

import com.ridelink.account.enums.AccountStatus;
import com.ridelink.account.enums.Role;

public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private AccountStatus status;

    public UserResponse(String id, String email, String fullName, String phone,
                         Role role, AccountStatus status) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public AccountStatus getStatus() { return status; }
}
