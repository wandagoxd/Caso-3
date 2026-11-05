import java.util.LinkedList;
import java.util.Queue;

// buzon donde los usuarios van dejando solicitudes y los gestores las sacan
public class BuzonEntrada {

    private final int capacidad;
    private final Queue<Solicitud> cola;
    private int usuariosActivos;

    public BuzonEntrada(int capacidad, int usuariosIniciales) {
        this.capacidad = capacidad;
        this.cola = new LinkedList<>();
        this.usuariosActivos = usuariosIniciales;
    }

    // mete la solicitud pero si esta lleno toca esperar pasivamente
    public synchronized void depositar(Solicitud s) throws InterruptedException {
        while (cola.size() >= capacidad) {
            wait(); // toca esperar a que hagan espacio
        }
        cola.add(s);
        // avisamos que entro algo
        notifyAll();
    }

    // el gestor saca una solicitud si hay, o devuelve null para su yield
    public synchronized Solicitud retirar() {
        if (cola.isEmpty()) {
            return null;
        }
        Solicitud s = cola.poll();
        // despertamos a algun usuario que estuviera bloqueado por buzon lleno
        notifyAll();
        return s;
    }

    // aca descuenta cuando un usuario termina de mandar todo
    public synchronized void reportarTerminacionUsuario() {
        usuariosActivos--;
        notifyAll();
    }

    // pa saber si todavia hay usuarios mandando cosas
    public synchronized boolean hayUsuariosActivos() {
        return usuariosActivos > 0;
    }

    // pa revisar si no queda nada en cola
    public synchronized boolean estaVacio() {
        return cola.isEmpty();
    }

    public synchronized int getTamanoActual() {
        return cola.size();
    }
}
