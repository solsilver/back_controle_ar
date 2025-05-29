package com.projetoTCC.arCondicionado.arCondicionado.model;

import com.projetoTCC.arCondicionado.arCondicionado.enums.ModoArCondicionadoEnum;
import com.projetoTCC.arCondicionado.arCondicionado.enums.VelocidadeVentiladorEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONTROLE_AR_CONDICIONADO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ControleArCondicionado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private boolean ligado;

    private int temperatura;

    @Enumerated(EnumType.STRING)
    private ModoArCondicionadoEnum modo;

    @Enumerated(EnumType.STRING)
    private VelocidadeVentiladorEnum velocidade;

    private boolean swingAtivo;

    private String ssid;

    private String senhaWifi;

    private LocalDateTime ultimaAtualizacao;
}
