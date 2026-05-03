package com.tech_sys.domain.os;

import com.tech_sys.infra.exception.BusinessException;

import java.util.Set;

public enum StatusOS {
    RECEBIDO,
    EM_ANALISE,
    AGUARDANDO_APROVACAO,
    APROVADO,
    EM_ANDAMENTO,
    AGUARDANDO_PECA,
    CONCLUIDO,
    ENTREGUE,
    CANCELADO;

    // Máquina de estados: define quais transições são válidas
    public void validarTransicao(StatusOS novoStatus) {
        Set<StatusOS> permitidos = switch (this) {
            case RECEBIDO             -> Set.of(EM_ANALISE, CANCELADO);
            case EM_ANALISE           -> Set.of(AGUARDANDO_APROVACAO, EM_ANDAMENTO, CANCELADO);
            case AGUARDANDO_APROVACAO -> Set.of(APROVADO, CANCELADO);
            case APROVADO             -> Set.of(EM_ANDAMENTO, CANCELADO);
            case EM_ANDAMENTO         -> Set.of(AGUARDANDO_PECA, CONCLUIDO, CANCELADO);
            case AGUARDANDO_PECA      -> Set.of(EM_ANDAMENTO, CANCELADO);
            case CONCLUIDO            -> Set.of(ENTREGUE);
            case ENTREGUE, CANCELADO  -> Set.of(); // estados finais
        };

        if (!permitidos.contains(novoStatus)) {
            throw new BusinessException(
                String.format("Transição inválida: %s → %s. Transições permitidas: %s",
                    this.name(), novoStatus.name(), permitidos)
            );
        }
    }
}
