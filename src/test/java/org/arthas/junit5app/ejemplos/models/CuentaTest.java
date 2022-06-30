package org.arthas.junit5app.ejemplos.models;

import org.arthas.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("nombre1", new BigDecimal("1000.982"));
        String esperado = "NOMBRE1";
        String real = cuenta.getPersona();

        assertEquals(esperado, real);
        // assertTrue(real.equals("nombre1"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("nombre1", new BigDecimal("1000.982"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.982, cuenta.getSaldo().doubleValue());
        //menor que cero
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
        assertNotNull(cuenta.getSaldo());
        Cuenta cuenta2 = new Cuenta("John", new BigDecimal("999.009"));
        assertNotNull(cuenta2.getSaldo());

        //assertNotEquals(cuenta2,cuenta);
        assertEquals(cuenta2, cuenta);

    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(899, cuenta.getSaldo().intValue());
        assertEquals("899.009", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1099, cuenta.getSaldo().intValue());
        assertEquals("1099.009", cuenta.getSaldo().toPlainString());
    }

    /**
     * para manejo de Exception, aca arroja la excepcion porque hay un error
     * y lo toma correctamte
     */
    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Gigio", new BigDecimal("999.009"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1000"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }
}