package org.arthas.junit5app.ejemplos.models;

import org.arthas.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    /**
     * se ejecuta antes de inicializar antes de cada @Test
     * se agrega TestInfo y TestReporter
     */
    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("NOMBRE1", new BigDecimal("1000.982"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el metodo");
        testReporter.publishEntry("Ejecutanto " + testInfo.getDisplayName() + " " +
                testInfo.getTestMethod().orElse(null).getName() + " con el TAG: " +
                testInfo.getTags());
    }

    /**
     * se ejecuta una vez que termina cada @Test
     */
    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finalizando el test");
    }

    /**
     * Una clase anidada de Sistema Operativov
     *
     * @Nested se indica que tipo de clase es
     * @Tag("cuenta") para utilizar se tiene que configurar y en vez de ejecutar la clase se tiene que seleccionar el tag
     */

    @Nested
    @Tag("cuenta")
    @DisplayName("Probando atributos de la cuenta")
    class CuentaTestNombreSaldo {
        /**
         * Por cada test se instancia un objeto
         */
        @Test
        @DisplayName("El nombre")
        void testNombreCuenta() {

            if (testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("tiene el TAG: "+testInfo.getTags()+ " y se podria hacer algo");
            }
            String esperado = "NOMBRE1";
            String real = cuenta.getPersona();
            assertNotNull(real, "La cuenta no puede ser nula");
            assertEquals(esperado, real, "El nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals("NOMBRE1"), "Nombre de la cuenta debe ser esperado al actual");
        }

        @Test
        @DisplayName("El saldo")
        void testSaldoCuenta() {
            //Cuenta cuenta = new Cuenta("nombre1", new BigDecimal("1000.982"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.982, cuenta.getSaldo().doubleValue());
            //menor que cero
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Referencias sean iguales son iguales")
        void testReferenciaCuenta() {
            // Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
            assertNotNull(cuenta.getSaldo());
            Cuenta cuenta2 = new Cuenta("NOMBRE1", new BigDecimal("1000.982"));
            assertNotNull(cuenta2.getSaldo());

            //assertNotEquals(cuenta2,cuenta);
            assertEquals(cuenta2, cuenta);

        }
    }

    @Nested
    class OperacionesTest {

        @Test
        @Tag("cuenta")
        void testDebitoCuenta() {
            //Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
            cuenta.debito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.982", cuenta.getSaldo().toPlainString());
        }


        @Test
        @Tag("cuenta")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.982", cuenta.getSaldo().toPlainString());
        }

        /**
         * se agregar fail() para que falle intencionalmente y
         * se agrega @Disabled para que no ejecute el Test, pero si va a salir en el reporte
         */

        @Test
        @Tag("banco")
        @Tag("cuenta")
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
    }


    /**
     * para manejo de Exception, aca arroja la excepcion porque hay un error
     * y lo toma correctamte
     */

    @Test
    @Tag("cuenta")
    @Tag("error")
    void testDineroInsuficienteException() {

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1100"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }


    /**
     * se agregar el assertAll para ver que pasa con todos los assert
     * se agrupan
     */
    @Test
    @Tag("banco")
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


    @Nested
    @Tag("SO")
    class SistemaOperativoTest {
        /**
         * se ejecuta en cualquier Sistema Operativo (OS.WINDOWS, OS.LINUX, etc)
         */
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }


        @Test
        @EnabledOnOs(OS.LINUX)
        void testSoloLinux() {
        }

        @Test
        @EnabledOnOs(OS.MAC)
        void testSoloMac() {
        }

        /**
         * No se ejecuta en Sistema Operativo que se selecciona (OS.WINDOWS)
         */
        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    /**
     * Una clase anidada de versiones de Java
     */
    @Nested
    @Tag("jdk")
    class JavaVersionTest {
        /**
         * se ejecuta en cualquier JDK (JRE.JAVA_18,JRE.JAVA_8, etc)
         */
        @Test
        @EnabledOnJre(JRE.JAVA_18)
        void onlyJdk18() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void onlyJdk8() {
        }
    }


    @Nested
    @Tag("property")
    class SistemaPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + "->" + v));
        }

        @Test
        // @EnabledIfSystemProperty(named = "java.version", matches = "18.0.1.1")
        @EnabledIfSystemProperty(named = "java.version", matches = "18.0.*")
        void testJavaVersionProperty() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = "amd64")
        void testArch64() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32*")
        void testNoArch64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "Arthas")
        void testUserName() {
        }

        /**
         * configurar una variable de entorno
         * agregando "-DENV=dev" en los properties del sistema
         */
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testEnvSystemProperty() {
        }
    }


    @Nested
    @Tag("environment")
    class VariableAmbienteTest {
        /**
         * Se imprime todas las variables de las variables de entorno  del ambiente (Environment)
         */
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + "->" + v));
        }

        @Test
        // @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\Program Files\\OpenJDK\\openjdk-8u302-b08")
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*openjdk-8u302-b08")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnvironmentVariable() {
        }
    }


    /**
     * Assumptions.assumeTrue para validar si sigue con la ejecucion o no
     */
    @Test
    @DisplayName("assumeTrue se ejecuta todo o se detiene")
    void testSaldoCuentaDev() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        Assumptions.assumeTrue(isDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.982, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        System.out.println("Si es correcto assumeTrue ejecuta todo");

    }

    /**
     * Assumptions.assumingThat para ejecutar solo el blocke de codigo
     */
    @Test
    @DisplayName("assumingThat se ejecuta solo  el block de codigo")
    void testSaldoCuentaDev2() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        Assumptions.assumingThat(isDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.982, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
            System.out.println("Si es correcto assumeTrue ejecuta todo lo que se encuentra adentro");
        });
        System.out.println("se ejecuta esto si o si");

    }


    /**
     * Test que se repite 5 para este ejemplo= @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
     */
    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    @DisplayName("Repeticion")
    void testRepeticion(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("Hacer algo con el " + info.getCurrentRepetition());
        }
        //Cuenta cuenta = new Cuenta("John", new BigDecimal("999.009"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.982", cuenta.getSaldo().toPlainString());
    }


    @Nested
    @Tag("Parameterized")
    @DisplayName("ParameterizedTest - Pruebas Parametrizadas ")
    class PruebasParametrizadas {
        /**
         * Se utiliza @ParameterizedTest similar a la repeticion pero este va a depender de cuantos parametros tenga @ValueSource
         * en este caso una cadena de String
         *
         * @param monto
         */
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @DisplayName("ParameterizedTestValueSource")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.0001"})
        void testValueSource(String monto) {
            System.out.println("Monto del string:" + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        /**
         * Se utiliza @ParameterizedTest similar a la repeticion pero este va a depender de cuantos parametros tenga @CsvSource
         * en este caso un csvSource
         *
         * @param monto
         */
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @DisplayName("ParameterizedTestCsvSource")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.0001"})
        void testCsvSource(String index, String monto) {
            System.out.println("Monto del string:" + monto + " |con el index:" + index);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        /**
         * Se utiliza @ParameterizedTest similar a la repeticion pero este va a depender de cuantos parametros tenga @ValueSource
         * en este caso un CsvFileSource, dejando el archivo data.csv en la carpeta resources
         *
         * @param monto
         */
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @DisplayName("ParameterizedTestCsvFileSource")
        @CsvFileSource(resources = "/data.csv")
        void testCsvFileSource(String monto, String secondColumn) {
            System.out.println("Monto del string:" + monto + " |segunda columna:" + secondColumn);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


    }

    /**
     * Se utiliza @ParameterizedTest similar a la repeticion pero este va a depender de cuantos parametros tenga @ValueSource
     * en este caso un MethodSource...utiliza el metodo "montoList", como es un metodo estatico tiene que estar en la raiz
     *
     * @param monto
     */
    @Tag("Parameterized")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @DisplayName("ParameterizedTestMethodSource")
    @MethodSource("montoList")
    void testCsvFileSource(String monto) {
        System.out.println("Monto del string:" + monto);
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.0001");
    }

}