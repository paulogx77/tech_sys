package com.tech_sys.domain.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    @Query("""
        SELECT c FROM Cliente c
        WHERE c.ativo = true
        AND (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
        AND (:cpf IS NULL OR c.cpf = :cpf)
        AND (:telefone IS NULL OR c.telefone LIKE CONCAT('%', :telefone, '%'))
        ORDER BY c.nome
    """)
    Page<Cliente> buscar(
        @Param("nome") String nome,
        @Param("cpf") String cpf,
        @Param("telefone") String telefone,
        Pageable pageable
    );
}
