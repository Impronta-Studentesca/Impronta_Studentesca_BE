package it.impronta_studentesca_be.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {

    private String email;
    private String password;
}
