package com.tech_sys.domain.aparelho;

import com.tech_sys.domain.cliente.Cliente;
import com.tech_sys.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aparelhos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aparelho extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAparelho tipo;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(length = 20)
    private String imei;

    @Column(length = 30)
    private String cor;

    @Column(length = 50)
    private String senha;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
