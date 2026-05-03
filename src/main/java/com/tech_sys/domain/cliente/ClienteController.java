package com.tech_sys.domain.cliente;

import com.tech_sys.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar e buscar clientes")
    public ResponseEntity<ApiResponse<Page<ClienteResponse>>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String telefone,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.ok(clienteService.buscar(nome, cpf, telefone, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo cliente")
    public ResponseEntity<ApiResponse<ClienteResponse>> criar(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Cliente cadastrado com sucesso", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    public ResponseEntity<ApiResponse<ClienteResponse>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente atualizado com sucesso", clienteService.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar cliente")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        clienteService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente inativado com sucesso", null));
    }
}
