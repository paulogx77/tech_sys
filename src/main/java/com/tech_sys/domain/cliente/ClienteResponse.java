package com.tech_sys.domain.cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
    Long id,
    String nome,
    String cpf,
    String telefone,
    String email,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep,
    String observacoes,
    Boolean ativo,
    LocalDateTime criadoEm
) {
    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(
            c.getId(), c.getNome(), c.getCpf(), c.getTelefone(),
            c.getEmail(), c.getLogradouro(), c.getNumero(), c.getComplemento(),
            c.getBairro(), c.getCidade(), c.getEstado(), c.getCep(),
            c.getObservacoes(), c.getAtivo(), c.getCriadoEm()
        );
    }
}
