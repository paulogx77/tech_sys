package com.tech_sys.domain.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO de criação/atualização
public record ClienteRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    String nome,

    @Size(max = 14)
    String cpf,

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    String telefone,

    String email,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep,
    String observacoes
) {}
