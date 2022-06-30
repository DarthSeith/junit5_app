package org.arthas.junit5app.ejemplos.models;

import org.arthas.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;

public class Cuenta {

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;
    }

    private String persona;
    private BigDecimal saldo;

    private Banco banco;

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuenta)) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(getPersona(), cuenta.getPersona()) && Objects.equals(getSaldo(), cuenta.getSaldo());
    }


    public void debito(BigDecimal monto) {
        BigDecimal saldo = this.saldo.subtract(monto);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero Insuficiente");
        }
        this.saldo = saldo;
    }


    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }

}
