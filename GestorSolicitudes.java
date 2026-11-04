public class GestorSolicitudes extends Thread {
    public GestorSolicitudes(
        int id,
        BuzonEntrada buzon,
        ZonaEspera zonaEspera,
        ControladorProcesadores ctrlCPU,
        ControladorMemoria ctrlMemoria,
        ControladorAlmacenamiento ctrlDisco
    ) { ... }

    @Override
    public void run() {
        // Bucle mientras !buzon.finDeTransmision():
        //   1. Si zonaEspera.superaUmbral() -> zonaEspera.esperarVaciadoTotal()
        //   2. solicitud = buzon.retirarNoBloqueante()
        //   3. Si solicitud == null -> Thread.yield() (espera semi-activa)
        //   4. Si solicitud != null -> new MV(solicitud, ...).start()
    }
}