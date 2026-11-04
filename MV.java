public class MV extends Thread {
    public MV(
        Solicitud solicitud,
        ControladorProcesadores ctrlCPU,
        ControladorMemoria ctrlMemoria,
        ControladorAlmacenamiento ctrlDisco,
        ZonaEspera zonaEspera
    ) { ... }

    @Override
    public void run() {
        // 1. Bucle de intento: reserva en orden estricto (CPU -> RAM -> Disco)
        // 2. Si falla en algún punto: rollback inmediato y zonaEspera.entrarEnEspera(this)
        // 3. Si consigue los tres: Thread.sleep(aleatorio entre 4000 y 40000 ms)
        // 4. Al terminar: liberar los 3 recursos y zonaEspera.notificarDisponibilidad()
    }
}