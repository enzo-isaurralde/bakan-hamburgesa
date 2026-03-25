package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.dto.DatosAutenticacion;
import Bakan.Sistema.de.Venta.dto.DatosTokenJWT;
import Bakan.Sistema.de.Venta.infra.security.TokenService;
import Bakan.Sistema.de.Venta.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para autenticación de usuarios (admin y cocina).
 * Gestiona el login y generación de tokens JWT.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para login y gestión de tokens JWT")
public class AutenticacionController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param authenticationManager Manager de autenticación de Spring Security
     * @param tokenService Servicio para generación de tokens JWT
     */
    @Autowired
    public AutenticacionController(
            AuthenticationManager authenticationManager,
            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Endpoint para iniciar sesión.
     * Recibe credenciales y devuelve un token JWT válido por 2 horas.
     *
     * @param datos Credenciales de autenticación (username y password)
     * @return Token JWT con tipo y tiempo de expiración
     */
    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario y devuelve un token JWT para acceder a los endpoints protegidos. " +
                    "El token tiene una validez de 2 horas y debe incluirse en el header Authorization como 'Bearer {token}'.",
            tags = {"Autenticación"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa. Devuelve el token JWT.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DatosTokenJWT.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (username o password vacíos)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales incorrectas (usuario no existe o contraseña inválida)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuario deshabilitado o sin permisos",
                    content = @Content
            )
    })
    public ResponseEntity<DatosTokenJWT> login(
            @RequestBody @Valid DatosAutenticacion datos
    ) {
        var authToken = new UsernamePasswordAuthenticationToken(
                datos.username(),
                datos.password()
        );

        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var tokenJWT = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());

        return ResponseEntity.ok(new DatosTokenJWT(tokenJWT, "Bearer", 7200L));
    }
}