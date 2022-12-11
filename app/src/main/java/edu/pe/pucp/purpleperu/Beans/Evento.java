package edu.pe.pucp.purpleperu.Beans;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Evento {
    String id;
    String tipo;
    String nombre;
    String caracteristicas;
    String lugar;
    String cantidadInvitados;
    String costo;
    String url;
    LocalDateTime FechayHora;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getCantidadInvitados() {
        return cantidadInvitados;
    }

    public void setCantidadInvitados(String cantidadInvitados) {
        this.cantidadInvitados = cantidadInvitados;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getFechayHora() {
        return FechayHora;
    }

    public void setFechayHora(LocalDateTime fechayHora) {
        FechayHora = fechayHora;
    }
}
