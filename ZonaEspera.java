// sala donde se duermen las maquinas que no consiguieron todos sus recursos
public class ZonaEspera {

    private final int umbral;
    private int mvsEnEspera;
    private boolean bloqueadoPorUmbral;

    public ZonaEspera(int umbral) {
        this.umbral = umbral;
        this.mvsEnEspera = 0;
        this.bloqueadoPorUmbral = false;
    }

    // el gestor mira si se paso del umbral y si si se queda quieto hasta que se vacie del todo
    public synchronized void verificarYEsperarUmbral() throws InterruptedException {
        if (mvsEnEspera >= umbral) {
            bloqueadoPorUmbral = true;
        }
        while (bloqueadoPorUmbral) {
            wait(); // gestor esperando a que se desocupe la sala
        }
    }

    // anotamos que entro una mv a esperar
    public synchronized void registrarIngresoEspera() {
        mvsEnEspera++;
        if (mvsEnEspera >= umbral) {
            bloqueadoPorUmbral = true;
        }
    }

    // aca la mv se duerme de forma pasiva
    public synchronized void dormirEnEspera() throws InterruptedException {
        wait();
    }

    // cuando una mv que estaba aca por fin consigue todo, sale de la cuenta
    public synchronized void salirDeEspera() {
        mvsEnEspera--;
        // si ya no queda nadie esperando y estabamos frenados, desbloqueamos a los gestores
        if (mvsEnEspera == 0 && bloqueadoPorUmbral) {
            bloqueadoPorUmbral = false;
            notifyAll(); // se despiertan los gestores
        }
    }

    // cuando alguna mv termina despierta a todas las que estan aca durmiendo
    public synchronized void notificarDisponibilidad() {
        notifyAll();
    }

    // pa chequear si la zona quedo limpia
    public synchronized boolean estaVacia() {
        return mvsEnEspera == 0;
    }

    public synchronized int getMvsEnEspera() {
        return mvsEnEspera;
    }
}
