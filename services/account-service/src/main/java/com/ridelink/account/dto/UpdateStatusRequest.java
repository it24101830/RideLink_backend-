package com.ridelink.account.dto;

import com.ridelink.account.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {
    @NotNull
    private AccountStatus status;

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
}
