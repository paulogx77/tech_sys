package com.tech_sys.domain.estoque;

import com.tech_sys.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pecas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peca extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "codigo_interno", length = 50, unique = true)
    private String codigoInterno;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 0;

    @Column(name = "estoque_minimo", nullable = false)
    @Builder.Default
    private Integer estoqueMinimo = 1;

    @Column(name = "preco_custo", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precoVenda = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    public boolean estaBaixoEstoqueMinimo() {
        return this.quantidade <= this.estoqueMinimo;
    }

    public void darEntrada(int qtd) {
        this.quantidade += qtd;
    }

    public void darSaida(int qtd) {
        if (qtd > this.quantidade) {
            throw new com.tech_sys.infra.exception.BusinessException(
                String.format("Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                    this.nome, this.quantidade, qtd)
            );
        }
        this.quantidade -= qtd;
    }
}
