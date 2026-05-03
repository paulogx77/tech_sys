package com.tech_sys.domain.cliente;

import com.tech_sys.infra.exception.BusinessException;
import com.tech_sys.infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Page<ClienteResponse> buscar(String nome, String cpf, String telefone, Pageable pageable) {
        return clienteRepository.buscar(nome, cpf, telefone, pageable)
                .map(ClienteResponse::from);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.from(encontrarOuLancar(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        if (request.cpf() != null && clienteRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF já cadastrado: " + request.cpf());
        }

        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .cpf(request.cpf())
                .telefone(request.telefone())
                .email(request.email())
                .logradouro(request.logradouro())
                .numero(request.numero())
                .complemento(request.complemento())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .estado(request.estado())
                .cep(request.cep())
                .observacoes(request.observacoes())
                .build();

        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = encontrarOuLancar(id);

        // Verifica CPF duplicado se estiver alterando
        if (request.cpf() != null && !request.cpf().equals(cliente.getCpf())) {
            if (clienteRepository.existsByCpf(request.cpf())) {
                throw new BusinessException("CPF já cadastrado: " + request.cpf());
            }
        }

        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setLogradouro(request.logradouro());
        cliente.setNumero(request.numero());
        cliente.setComplemento(request.complemento());
        cliente.setBairro(request.bairro());
        cliente.setCidade(request.cidade());
        cliente.setEstado(request.estado());
        cliente.setCep(request.cep());
        cliente.setObservacoes(request.observacoes());

        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public void inativar(Long id) {
        Cliente cliente = encontrarOuLancar(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    private Cliente encontrarOuLancar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }
}
