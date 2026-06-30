package com.grayunited.payments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChannelRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Work email is required")
    @Email(message = "Invalid email format")
    private String workEmail;

    private String company;

    @NotBlank(message = "Contract range is required")
    private String contractSize;

    @NotBlank(message = "Brief cannot be empty")
    @Size(min = 20, message = "Brief must be at least 20 characters")
    private String brief;
}