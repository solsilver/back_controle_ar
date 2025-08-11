package com.projetoTCC.arCondicionado.arCondicionado.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "salas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String localizacao;

    private Integer posicao;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.EAGER,  orphanRemoval = true)
    private List<ControleArCondicionado> aresCondicionados;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.EAGER,  orphanRemoval = true)
    private List<ReservaSala> reservas;
}
