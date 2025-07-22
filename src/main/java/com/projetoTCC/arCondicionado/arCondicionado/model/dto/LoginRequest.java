package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private Long matricula;
    private String senha;
}