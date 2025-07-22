package com.projetoTCC.arCondicionado.arCondicionado.model;

import com.projetoTCC.arCondicionado.arCondicionado.enums.TipoUsuarioEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private Long matricula; // apenas números

    @Column(nullable = false)
    private String senha; // será criptografada

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuarioEnum tipo;
}
