package com.ayds2.Proyecto.ayds2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.MercadoPagoConfig;

@Configuration
public class MercadoPagoInit {

    // Spring inyecta el valor del token 
    @Value("${mp.access.token}")
    private String accessToken;

     public MercadoPagoInit() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}
