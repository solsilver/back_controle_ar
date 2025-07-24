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

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ControleArCondicionado> aresCondicionados;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ReservaSala> reservas;
}
