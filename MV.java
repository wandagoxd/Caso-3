import java.util.Random;

// aca corre cada maquina virtual
public class MV extends Thread {

    private final Solicitud solicitud;
    private final ControladorProcesadores ctrlCPU;
    private final ControladorMemoria ctrlMemoria;
    private final ControladorAlmacenamiento ctrlDisco;
    private final ZonaEspera zonaEspera;

    public MV(
        Solicitud solicitud,
        ControladorProcesadores ctrlCPU,
        ControladorMemoria ctrlMemoria,
        ControladorAlmacenamiento ctrlDisco,
        ZonaEspera zonaEspera
    ) {
        this.solicitud = solicitud;
        this.ctrlCPU = ctrlCPU;
        this.ctrlMemoria = ctrlMemoria;
        this.ctrlDisco = ctrlDisco;
        this.zonaEspera = zonaEspera;
    }

    @Override
    public void run() {
        boolean recursosObtenidos = false;
        boolean enZonaEspera = false;
        Random rand = new Random();

        // toca pedir cpu luego ram y luego disco en ese orden
        while (!recursosObtenidos) {

            // primero procesador
            boolean tieneCPU = ctrlCPU.intentarReservar(solicitud.getId(), solicitud.getNumProcesadores());

            if (tieneCPU) {
                // si le dieron cpu pide ram
                boolean tieneRAM = ctrlMemoria.intentarReservar(solicitud.getId(), solicitud.getMemoriaGiB());

                if (tieneRAM) {
                    // si le dieron ram pide disco
                    boolean tieneDisco = ctrlDisco.intentarReservar(solicitud.getId(), solicitud.getAlmacenamientoGiB());

                    if (tieneDisco) {
                        // consiguio los tres recursos
                        recursosObtenidos = true;
                    } else {
                        // si no hay disco soltamos ram y cpu
                        ctrlMemoria.liberar(solicitud.getId());
                        ctrlCPU.liberar(solicitud.getId());
                    }
                } else {
                    // si no hay ram soltamos cpu
                    ctrlCPU.liberar(solicitud.getId());
                }
            }

            // si no pudo conseguir todo le toca ir a dormir a la zona de espera
            if (!recursosObtenidos) {
                if (!enZonaEspera) {
                    // solo sumamos la primera vez que entra a la sala
                    zonaEspera.registrarIngresoEspera();
                    enZonaEspera = true;
                }

                try {
                    // se queda dormida esperando que otra termine
                    zonaEspera.dormirEnEspera();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // si estuvo durmiendo y por fin logro reservar sale de la zona
        if (enZonaEspera) {
            zonaEspera.salirDeEspera();
        }

        System.out.println("[MV " + solicitud.getId() + "] Asignada con exito (" +
                solicitud.getNumProcesadores() + " CPUs, " +
                solicitud.getMemoriaGiB() + " GiB RAM, " +
                solicitud.getAlmacenamientoGiB() + " GiB Disco). Ejecutando...");

        // simula el tiempo de ejecucion entre 4 y 40 segundos
        try {
            int tiempoEjecucion = 4000 + rand.nextInt(36001);
            Thread.sleep(tiempoEjecucion);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // cuando acaba suelta todo
        ctrlDisco.liberar(solicitud.getId());
        ctrlMemoria.liberar(solicitud.getId());
        ctrlCPU.liberar(solicitud.getId());

        System.out.println("[MV " + solicitud.getId() + "] Termino su ejecucion y libero todos los recursos.");

        // despierta a las otras que estaban esperando para que intenten
        zonaEspera.notificarDisponibilidad();
    }
}
