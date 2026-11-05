import java.util.Random;

// hilo del usuario que manda peticiones al sistema
public class Usuario extends Thread {

    private final int id;
    private final int base;
    private final BuzonEntrada buzon;
    private final int totalCPUs;
    private final int totalRAM;
    private final int totalDisco;

    public Usuario(int id, int base, BuzonEntrada buzon, int totalCPUs, int totalRAM, int totalDisco) {
        this.id = id;
        this.base = base;
        this.buzon = buzon;
        this.totalCPUs = totalCPUs;
        this.totalRAM = totalRAM;
        this.totalDisco = totalDisco;
    }

    @Override
    public void run() {
        // cuantas solicitudes le tocan a este usuario en particular
        int totalSolicitudes = id * base;
        Random rand = new Random();

        for (int i = 0; i < totalSolicitudes; i++) {
            // armamos el id unico tipo 1_0, 1_1 etc
            String idSolicitud = id + "_" + i;

            // calculamos cpus al azar respetando el 75%
            int maxCPUs = Math.max(1, (int) Math.round(totalCPUs * 0.75));
            int cpus = 1 + rand.nextInt(maxCPUs);

            // ram al azar redondeada a potencia de dos
            int maxRAM = Math.max(1, (int) Math.round(totalRAM * 0.75));
            int ramGen = 1 + rand.nextInt(maxRAM);
            int memoriaGiB = Integer.highestOneBit(ramGen);
            if (memoriaGiB < 1) {
                memoriaGiB = 1;
            }

            // disco al azar redondeado a multiplo de 2
            int maxDisco = Math.max(2, (int) Math.round(totalDisco * 0.75));
            int discoGen = 1 + rand.nextInt(maxDisco);
            int almacenamientoGiB = (discoGen / 2) * 2;
            if (almacenamientoGiB < 2) {
                almacenamientoGiB = 2; // pa que no quede en 0
            }

            Solicitud solicitud = new Solicitud(idSolicitud, cpus, memoriaGiB, almacenamientoGiB);

            // metemos la solicitud al buzon compartido
            try {
                buzon.depositar(solicitud);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // le avisamos al buzon que este usuario ya acabo
        buzon.reportarTerminacionUsuario();
    }
}
