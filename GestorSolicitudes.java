import java.util.List;

// saca peticiones del buzon y va prendiendo los hilos de las mv
public class GestorSolicitudes extends Thread {

    private final int id;
    private final BuzonEntrada buzon;
    private final ZonaEspera zonaEspera;
    private final ControladorProcesadores ctrlCPU;
    private final ControladorMemoria ctrlMemoria;
    private final ControladorAlmacenamiento ctrlDisco;
    private final List<MV> todasLasMVs;

    public GestorSolicitudes(
        int id,
        BuzonEntrada buzon,
        ZonaEspera zonaEspera,
        ControladorProcesadores ctrlCPU,
        ControladorMemoria ctrlMemoria,
        ControladorAlmacenamiento ctrlDisco
    ) {
        this(id, buzon, zonaEspera, ctrlCPU, ctrlMemoria, ctrlDisco, null);
    }

    public GestorSolicitudes(
        int id,
        BuzonEntrada buzon,
        ZonaEspera zonaEspera,
        ControladorProcesadores ctrlCPU,
        ControladorMemoria ctrlMemoria,
        ControladorAlmacenamiento ctrlDisco,
        List<MV> todasLasMVs
    ) {
        this.id = id;
        this.buzon = buzon;
        this.zonaEspera = zonaEspera;
        this.ctrlCPU = ctrlCPU;
        this.ctrlMemoria = ctrlMemoria;
        this.ctrlDisco = ctrlDisco;
        this.todasLasMVs = todasLasMVs;
    }

    @Override
    public void run() {
        // mientras los usuarios no hayan acabado o quede algo en el buzon
        while (buzon.hayUsuariosActivos() || !buzon.estaVacio()) {

            // miramos si la zona de espera paso el umbral, si si toca esperar pasivamente
            try {
                zonaEspera.verificarYEsperarUmbral();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // sacamos la solicitud
            Solicitud solicitud = buzon.retirar();

            if (solicitud != null) {
                // creamos el hilo de la mv y lo arrancamos
                MV mv = new MV(solicitud, ctrlCPU, ctrlMemoria, ctrlDisco, zonaEspera);
                if (todasLasMVs != null) {
                    todasLasMVs.add(mv);
                }
                mv.start();
            } else {
                // si el buzon esta vacio pero faltan usuarios hacemos yield (espera semi-activa)
                Thread.yield();
            }
        }
    }
}
