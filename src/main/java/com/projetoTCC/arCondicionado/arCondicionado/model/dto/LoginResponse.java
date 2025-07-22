package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import com.projetoTCC.arCondicionado.arCondicionado.enums.TipoUsuarioEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private UserDetail user;


    public LoginResponse(Long id, String nome, String matricula, String token, TipoUsuarioEnum tipo) {
        this.user = new UserDetail(id,nome,matricula,tipo);
        this.token = token;

    }


}