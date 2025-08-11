package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.enums.EnderecoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SalaDTO {
    private Long id;
    private String nome;
    private String localizacao;
    private String reservadoPor;
    private Long matricula;

    public SalaDTO(Long id,String nome,String localizacao,Long matricula, String reservadoPor ) {
        this.matricula = matricula;
        this.reservadoPor = reservadoPor;
        this.localizacao = EnderecoEnum.getDescricaoPorNome(localizacao);
        this.nome = nome;
        this.id = id;
    }


}