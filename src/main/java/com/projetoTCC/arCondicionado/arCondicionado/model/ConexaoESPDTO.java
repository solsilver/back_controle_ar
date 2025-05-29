package com.projetoTCC.arCondicionado.arCondicionado.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConexaoESPDTO {
    private String ssid;
    private String senha;
}
