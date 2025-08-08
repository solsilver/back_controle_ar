package com.projetoTCC.arCondicionado.arCondicionado.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projetoTCC.arCondicionado.arCondicionado.model.enums.EnderecoEnum;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "LOCAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private EnderecoEnum endereco;

    @JsonIgnore
    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Sala> salas;
}