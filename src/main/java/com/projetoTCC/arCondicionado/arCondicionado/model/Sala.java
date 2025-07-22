package com.projetoTCC.arCondicionado.arCondicionado.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL)
    private List<ControleArCondicionado> aresCondicionados;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL)
    private List<ReservaSala> reservas;
}
