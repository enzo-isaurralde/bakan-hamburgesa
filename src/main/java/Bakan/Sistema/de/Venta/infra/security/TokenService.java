package Bakan.Sistema.de.Venta.infra.security;

import Bakan.Sistema.de.Venta.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String secret;

    public String generarToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("bakan-api")
                .withSubject(usuario.getUsername())
                .withClaim("rol", usuario.getRol().name())
                .withExpiresAt(fechaExpiracion())
                .sign(algorithm);
    }

    public String getSubject(String tokenJWT) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("bakan-api")
                .build()
                .verify(tokenJWT)
                .getSubject();
    }

    private Instant fechaExpiracion() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}