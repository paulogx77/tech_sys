package com.tech_sys.domain.os;

import com.tech_sys.domain.aparelho.Aparelho;
import com.tech_sys.domain.cliente.Cliente;
import com.tech_sys.domain.usuario.Usuario;
import com.tech_sys.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aparelho_id", nullable = false)
    private Aparelho aparelho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatusOS status = StatusOS.RECEBIDO;

    @Column(name = "defeito_relatado", nullable = false, columnDefinition = "TEXT")
    private String defeitoRelatado;

    @Column(name = "laudo_tecnico", columnDefinition = "TEXT")
    private String laudoTecnico;

    @Column(name = "observacoes_internas", columnDefinition = "TEXT")
    private String observacoesInternas;

    @Column(name = "valor_mao_obra", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorMaoObra = BigDecimal.ZERO;

    @Column(name = "valor_pecas", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorPecas = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "forma_pagamento", length = 30)
    private String formaPagamento;

    @Column(name = "data_previsao")
    private LocalDate dataPrevisao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemOS> itens = new ArrayList<>();

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HistoricoOS> historico = new ArrayList<>();

    // Recalcula totais com base nos itens
    public void recalcularTotais() {
        this.valorPecas = itens.stream()
                .filter(i -> i.getTipo() == TipoItemOS.PECA)
                .map(ItemOS::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalServicos = itens.stream()
                .filter(i -> i.getTipo() == TipoItemOS.SERVICO)
                .map(ItemOS::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.valorTotal = this.valorPecas
                .add(totalServicos)
                .add(this.valorMaoObra)
                .subtract(this.desconto);
    }
}
