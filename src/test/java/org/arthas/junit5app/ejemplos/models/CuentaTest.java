package org.arthas.junit5app.ejemplos.models;

import org.arthas.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    /**
     * se ejecuta antes de inicializar antes de cada @Test
     */
    @BeforeEach
    void initMetodoTest() {
        this.cuenta = new Cuenta("NOMBRE1", new BigDecimal("1000.982"));
        System.out.println("Iniciando el metodo");
    }

    /**
     * se ejecuta una vez que termina cada @Test
     */
    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba");
    }

    @BeforeAll
    void beforeAll() {
        System.out.println("inicializando el test");
    }

    @AfterAll
    void afterAll() {
        System.out.println("finalizando el test");
    }

    /**
     * Por cada test se instancia un objeto
     */
    @Test
    @DisplayName("Probando el nombre de la cuenta corriente")
    void testNombreCuenta() {

        String esperado = "NOMBRE1";
        String real = this.cuenta.getPersona();
        assertNotNull(real, "La cuenta no puede ser nula");
        assertEquals(esperado, real, "El nombre de la cuenta no es el que se esperaba");
        assertTrue(real.equals("NOMBRE1"), "Nombre de la cuenta debe ser esperado al actual");
    }

    @Test
    @DisplayName("probando  el saldo de la cuenta corriente")
    void testSaldoCuenta() {
        //Cuenta cuenta = new Cuenta("nombre1", new BigDecimal("1000.982"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.982, cuenta.getSaldo().doubleValue());
        //menor que cero
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("si las referencias sean iguales son iguales")
    void testReferenciaCuenta() {
        // Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
        assertNotNull(cuenta.getSaldo());
        Cuenta cuenta2 = new Cuenta("NOMBRE1", new BigDecimal("1000.982"));
        assertNotNull(cuenta2.getSaldo());

        //assertNotEquals(cuenta2,cuenta);
        assertEquals(cuenta2, cuenta);

    }

    @Test
    void testDebitoCuenta() {
        //Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.982", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.982", cuenta.getSaldo().toPlainString());
    }

    /**
     * para manejo de Exception, aca arroja la excepcion porque hay un error
     * y lo toma correctamte
     */
    @Test
    void testDineroInsuficienteException() {

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1100"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }

    /**
     * se agregar fail() para que falle intencionalmente y
     * se agrega @Disabled para que no ejecute el Test, pero si va a salir en el reporte
     */
    @Test
    @DisplayName("@Test de TransferirDineroCuentas que esta @Disabled")
    @Disabled
    void testTransferirDineroCuentas() {
        fail();
        Cuenta origen = new Cuenta("Gigio", new BigDecimal("2500"));
        Cuenta destino = new Cuenta("Memo", new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.setNombre("bancoTest1");
        banco.transferir(origen, destino, new BigDecimal("500"));
        assertEquals("1500", destino.getSaldo().toPlainString());
        assertEquals("2000", origen.getSaldo().toPlainString());

    }

    /**
     * se agregar el assertAll para ver que pasa con todos los assert
     * se agrupan
     */
    @Test
    @DisplayName("Probando relaciones entre cuenta y el banco con assertAll")
    void testRelacionBancoCuenta() {
        Cuenta origen = new Cuenta("Gigio", new BigDecimal("2500"));
        Cuenta destino = new Cuenta("Memo", new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.addCuenta(origen);
        banco.addCuenta(destino);

        banco.setNombre("Banco del estado");
        banco.transferir(origen, destino, new BigDecimal("500"));
        assertAll(
                () -> {
                    assertEquals("1500", destino.getSaldo().toPlainString(), () -> "el valor no correponde");
                },
                () -> {
                    assertEquals("2000", origen.getSaldo().toPlainString(), () -> "el valor no correponde");
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size(), () -> "La cantidad no es correcta");
                },
                () -> {
                    assertEquals("Banco del estado", origen.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Gigio", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Gigio"))
                            .findFirst()
                            .get().getPersona()
                    );
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Memo"))
                    );
                }
        );


    }
}