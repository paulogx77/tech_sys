package com.tech_sys.domain.usuario;

import com.tech_sys.infra.security.JwtService;
import com.tech_sys.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String senha
    ) {}

    public record LoginResponse(
        String token,
        String nome,
        String email,
        String role
    ) {}

    @PostMapping("/login")
    @Operation(summary = "Login e geração de token JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        String token = jwtService.gerarToken(usuario);

        return ResponseEntity.ok(ApiResponse.ok(new LoginResponse(
            token,
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().name()
        )));
    }
}
