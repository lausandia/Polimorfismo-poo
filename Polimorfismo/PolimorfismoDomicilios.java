/*
 * ============================================================
 *  I.U. PASCUAL BRAVO - Programacion Orientada a Objetos
 *  Sesion 10: Polimorfismo Dinamico y Estatico
 *  Docente: Luis Fernando Londono Lopez
 * ============================================================
 *  EJERCICIO: Plataforma de domicilios "RapiMedellin"
 *
 *  Una app de domicilios usa distintos repartidores (moto,
 *  bicicleta, carro) y distintos medios de pago (Nequi,
 *  tarjeta, efectivo). El sistema debe calcular el costo de
 *  cada envio y cobrar SIN usar if/switch sobre el tipo.
 *
 *  Partes del ejercicio:
 *   1. Polimorfismo estatico  -> sobrecarga en CalculadoraTarifa
 *   2. Polimorfismo dinamico  -> sobreescritura en Repartidor
 *   3. Referencia padre / objeto hijo y casting seguro
 *   4. Coleccion polimorfica  -> un solo ciclo for-each
 *   5. Polimorfismo por interfaz -> MedioPago
 *   6. Antes vs despues: del condicional al polimorfismo
 *
 *  Como ejecutar en Visual Studio Code:
 *   - Funciona con Java 8 o superior. Instalar la extension
 *     "Extension Pack for Java".
 *   - Abrir este archivo y pulsar "Run" sobre el metodo main,
 *     o en la terminal:
 *        javac PolimorfismoDomicilios.java
 *        java PolimorfismoDomicilios
 * ============================================================
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ------------------------------------------------------------
// PARTE 1. POLIMORFISMO ESTATICO (sobrecarga)
// El compilador decide que version usar segun los argumentos.
// ------------------------------------------------------------
class CalculadoraTarifa {

    // Version 1: solo distancia
    double calcular(double km) {
        return 3000 + km * 1200;
    }

    // Version 2: distancia + recargo nocturno
    double calcular(double km, boolean nocturno) {
        double base = calcular(km);
        return nocturno ? base * 1.25 : base;
    }

    // Version 3: distancia + numero de paradas
    double calcular(double km, int paradas) {
        return calcular(km) + paradas * 1500;
    }

    //PUNTO 4 distancia, numero de paradas y recargo nocturno
    double calcular(double km, int paradas, boolean nocturno) {
        double base = calcular(km, paradas);
        return nocturno ? base * 1.25 : base;
    }

}

// ------------------------------------------------------------
// PARTE 2. POLIMORFISMO DINAMICO (sobreescritura)
// La JVM decide en tiempo de ejecucion segun el objeto real.
// ------------------------------------------------------------
abstract class Repartidor {
    protected String nombre;

    Repartidor(String nombre) {
        this.nombre = nombre;
    }

    // Contrato: cada subclase calcula su costo a su manera
    abstract double costoEnvio(double km);

    abstract String tipo();

    // Metodo comun que usa el metodo abstracto (enlace tardio)
    void describir(double km) {
        System.out.printf("  %-10s | %-9s | %5.1f km | $%,10.0f%n",
                nombre, tipo(), km, costoEnvio(km));
    }
}

class Moto extends Repartidor {
    Moto(String nombre) { super(nombre); }

    @Override
    double costoEnvio(double km) {
        return 4000 + km * 1000;
    }

    @Override
    String tipo() { return "Moto"; }

    // Metodo propio, NO esta en Repartidor
    String placa() { return "ABC-12F"; }
}

class Bicicleta extends Repartidor {
    Bicicleta(String nombre) { super(nombre); }

    @Override
    double costoEnvio(double km) {
        // Tarifa ecologica: mas barata, pero recargo si supera 5 km
        double costo = 2500 + km * 800;
        return km > 5 ? costo + 3000 : costo;
    }

    @Override
    String tipo() { return "Bicicleta"; }
}

class Carro extends Repartidor {
    Carro(String nombre) { super(nombre); }

    @Override
    double costoEnvio(double km) {
        return 8000 + km * 1500;
    }

    @Override
    String tipo() { return "Carro"; }
}

//PUNTO 1
class Patineta extends Repartidor {
    Patineta(String nombre) { super(nombre); }

    @Override
    double costoEnvio(double km) {
        
        return km > 6 ? 0 : 3000 + km * 900; // si supera 6 km, el costo es 0
    }

    @Override
    String tipo() { return "Patineta"; }
}
// ------------------------------------------------------------
// PARTE 5. POLIMORFISMO POR INTERFAZ
// Tipos sin relacion de herencia que cumplen el mismo contrato.
// ------------------------------------------------------------
interface MedioPago {
    boolean procesar(double monto);
    String getNombre();
}

class Nequi implements MedioPago {
    private double saldo;

    Nequi(double saldo) { this.saldo = saldo; }

    public boolean procesar(double monto) {
        if (monto <= saldo) {
            saldo -= monto;
            return true;
        }
        return false;
    }

    public String getNombre() { return "Nequi"; }
}

class TarjetaCredito implements MedioPago {
    public boolean procesar(double monto) { return true; }
    public String getNombre() { return "Tarjeta de credito"; }
}

class Daviplata implements MedioPago {
    private double saldo;

    Daviplata(double saldo) {
        this.saldo = saldo;
    }

    public boolean procesar(double monto) {
        if (monto <= saldo) {
            saldo -= monto;
            return true;
        }
        return false;
    }

    public String getNombre() { return "Daviplata"; }
}

class Efectivo implements MedioPago {
    // Regla del negocio: el repartidor no lleva mas de 100.000 de cambio
    public boolean procesar(double monto) { return monto <= 100000; }
    public String getNombre() { return "Efectivo"; }
}

// ------------------------------------------------------------
// PARTE 6. ANTES: version con condicional (mal diseno)
// ------------------------------------------------------------
class TarifaConCondicional {
    static double costo(String tipo, double km) {
        if (tipo.equals("MOTO")) {
            return 4000 + km * 1000;
        } else if (tipo.equals("BICI")) {
            double c = 2500 + km * 800;
            return km > 5 ? c + 3000 : c;
        } else if (tipo.equals("CARRO")) {
            return 8000 + km * 1500;
        }
        return 0; // Agregar un tipo nuevo obliga a modificar este metodo
    }
}

// ------------------------------------------------------------
// PROGRAMA PRINCIPAL
// ------------------------------------------------------------
public class PolimorfismoDomicilios {

    public static void main(String[] args) {

        titulo("PARTE 1. Polimorfismo estatico (sobrecarga)");
        CalculadoraTarifa calc = new CalculadoraTarifa();
        // El compilador elige la version por la firma:
        System.out.printf("  calcular(4.0)          -> $%,.0f%n", calc.calcular(4.0));
        System.out.printf("  calcular(4.0, true)    -> $%,.0f%n", calc.calcular(4.0, true));
        System.out.printf("  calcular(4.0, 2)       -> $%,.0f%n", calc.calcular(4.0, 2));
        System.out.println("  >> Resuelto en COMPILACION: el bytecode ya sabe que metodo llamar.");

        titulo("PARTE 2 y 3. Referencia padre, objeto hijo");
        Repartidor r = new Moto("Andres");   // upcasting implicito
        System.out.printf("  r.costoEnvio(3) = $%,.0f%n", r.costoEnvio(3));
        System.out.println("  >> La referencia es Repartidor, pero se ejecuta Moto.costoEnvio()");
        // r.placa();  // ERROR de compilacion: placa() no existe en Repartidor

        // Downcasting seguro: primero verificar con instanceof, luego hacer el casting
        if (r instanceof Moto) {
            Moto m = (Moto) r;
            System.out.println("  Downcasting seguro: placa de la moto = " + m.placa());
        }
        // En Java 16+ se puede abreviar asi: if (r instanceof Moto m) { ... }

        titulo("PARTE 4. Coleccion polimorfica: un solo ciclo");
        List<Repartidor> flota = new ArrayList<>();
        flota.add(new Moto("Andres"));
        flota.add(new Bicicleta("Valentina"));
        flota.add(new Carro("Jhon"));
        flota.add(new Bicicleta("Sara"));
        flota.add(new Patineta("Luis")); //PUNTO 2 agregar patineta a la lista flota

        double[] distancias = {3.5, 7.0, 12.0, 2.0, 6.5}; //PUNTO 2 agregar nueva distancia al arreglo
        double totalDia = 0;

        System.out.println("  Nombre     | Vehiculo  | Distancia | Costo");
        System.out.println("  -----------+-----------+-----------+------------");
        for (int i = 0; i < flota.size(); i++) {
            Repartidor rep = flota.get(i);
            rep.describir(distancias[i]);          // sin if, sin switch
            totalDia += rep.costoEnvio(distancias[i]);
        }
        System.out.printf("  TOTAL DEL DIA: $%,.0f%n", totalDia);

        titulo("PARTE 5. Polimorfismo por interfaz: medios de pago");
        List<MedioPago> pagos = Arrays.asList(
                new Nequi(20000),
                new Daviplata(50000), //PUNTO 3 Agregar daviplata a pagos
                new TarjetaCredito(),
                new Efectivo());

        double pedido = 35000;
        for (MedioPago p : pagos) {
            String resultado = p.procesar(pedido) ? "APROBADO" : "RECHAZADO";
            System.out.printf("  Pedido de $%,.0f con %-18s -> %s%n",
                    pedido, p.getNombre(), resultado);
        }

        titulo("PARTE 6. Antes vs despues");
        System.out.printf("  Con condicional : $%,.0f%n", TarifaConCondicional.costo("MOTO", 3.5));
        System.out.printf("  Con polimorfismo: $%,.0f%n", new Moto("X").costoEnvio(3.5));
        System.out.println("  >> Mismo resultado. Pero para agregar 'Patineta':");
        System.out.println("     - Version condicional: modificar el metodo existente.");
        System.out.println("     - Version polimorfica: crear una clase nueva. El ciclo no cambia (OCP).");

        /*
         * ====================================================
         *  RETO PARA LOS ESTUDIANTES (en parejas, 30 min)
         * ====================================================
         *  1. Crear la clase Patineta que extienda Repartidor.
         *     Tarifa: 3000 + km * 900, y solo hasta 6 km
         *     (si supera 6 km, el costo es 0 = no disponible).
         *  2. Agregar una Patineta a la lista "flota" y una
         *     distancia nueva al arreglo "distancias".
         *  3. Crear la clase Daviplata que implemente MedioPago
         *     con un saldo inicial y agregarla a "pagos".
         *  4. Agregar a CalculadoraTarifa una cuarta sobrecarga:
         *     calcular(double km, int paradas, boolean nocturno).
         *
         *  RESTRICCIONES:
         *   - No modificar el ciclo for de la PARTE 4.
         *   - Sin instanceof, casting, if o switch sobre el tipo
         *     dentro de los ciclos.
         *
         *  NOTA ESCRITA (como comentario al final del archivo):
         *   a) Cuales llamadas se resuelven en compilacion y
         *      cuales en ejecucion.
         *   b) Que condicional se elimino con el diseno.
         *   c) Por que el ciclo no necesita conocer el tipo real.
         * ====================================================
         */
    }

    static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }
}

/*
 * NOTA ESCRITA:
 * a) Compilación: Métodos sobrecargados de CalculadoraTarifa. Ejecución: Métodos polimórficos (costoEnvio, procesar) por enlace tardío de la JVM.
 * b) Se eliminaron los if-else y switch basados en el tipo de objeto.
 * c) Porque opera sobre la abstracción (Repartidor/MedioPago), delegando la lógica al objeto real.
 */




//TRABAJO REALIZADO CON SARA RIOS VELEZ