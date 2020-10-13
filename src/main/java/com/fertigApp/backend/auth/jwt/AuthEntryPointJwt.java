package com.fertigApp.backend.auth.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/*
 * Clase responsable de manejar la respuesta cuando se intenta acceder a un recurso no autorizado
 * */
@Component
public class AuthEntryPointJwt  implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        //para los clientes que acceden via proxy X-FORWARDED-FOR para obtener ip
        //log de intento de acceso sin credeciales a recurso protegido
        logger.error("Unauthorized error: {} in ip: {}", authException.getMessage(),request.getRemoteAddr());
        //respuesta de no autorizado al cliente
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}
