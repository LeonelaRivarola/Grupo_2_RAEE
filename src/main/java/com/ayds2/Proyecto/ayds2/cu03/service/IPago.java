package com.ayds2.Proyecto.ayds2.cu03.service;

import com.ayds2.Proyecto.ayds2.cu03.model.PagoResponse;
import com.ayds2.Proyecto.ayds2.cu03.model.PagoRequest;

public interface IPago {
    PagoResponse procesarPago(PagoRequest request) throws Exception;
    boolean validarPago(String preferenceId);
}