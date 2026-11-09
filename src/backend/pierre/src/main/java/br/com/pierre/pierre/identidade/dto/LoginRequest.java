package br.com.pierre.pierre.identidade.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "É necessário informar um email!")
    @Email(message = "O campo 'email' está no formato incorreto.")
    String email,

    @NotBlank(message = "É necessário informar uma senha!")
    @Size(min = 8, max = 255, message = "A senha deve ter entre 8 e 255 caracteres.")
    String senha
){}