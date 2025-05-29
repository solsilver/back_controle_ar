package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import com.projetoTCC.arCondicionado.arCondicionado.enums.ModoArCondicionadoEnum;
import com.projetoTCC.arCondicionado.arCondicionado.enums.VelocidadeVentiladorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControleArCondicionadoUpdateDTO {
    private Boolean ligado;
    private Integer temperatura;
    private ModoArCondicionadoEnum modo;
    private VelocidadeVentiladorEnum velocidade;
    private Boolean swingAtivo;
}
