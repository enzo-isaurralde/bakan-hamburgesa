package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.dto.DatosAutenticacion;
import Bakan.Sistema.de.Venta.infra.security.TokenService;
import Bakan.Sistema.de.Venta.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody DatosAutenticacion datos) {
        var authToken = new UsernamePasswordAuthenticationToken(
                datos.username(), datos.password()
        );
        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var tokenJWT = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        return ResponseEntity.ok(tokenJWT);
    }
}