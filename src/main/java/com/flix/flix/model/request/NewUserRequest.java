package com.flix.flix.model.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "username is required")
    @Size(min = 5, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotBlank(message = "email is required")
    @Email
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;
    @NotEmpty(message = "role is required")
    private List<String> role;
}
