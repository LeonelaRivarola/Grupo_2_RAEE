package com.ayds2.Proyecto.ayds2.iface;

import com.ayds2.Proyecto.ayds2.model.PagoRequest;
import com.ayds2.Proyecto.ayds2.model.PagoResponse;

public interface IPago {
    PagoResponse procesarPago(PagoRequest request) throws Exception;
    boolean validarPago(String preferenceId);
}