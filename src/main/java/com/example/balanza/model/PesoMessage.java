package com.example.balanza.model;

public class PesoMessage {
    private String peso;

    public PesoMessage() {}

    public PesoMessage(String peso) {
        this.peso = peso;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }
}
