
import java.util.Random;
import java.util.random.*;

public class Usuario extends Thread {

    // Necesitamos crear atributos para definir cada usuario. Luego, como es thread,
    // crearemos el método run.;

    private int id;
    private int base;
    private BuzonEntrada buzon;
    private int totalCPUs;
    private int totalRam;

    public Usuario(int id_usuario, int base, BuzonEntrada buzon, int totalCPUs, int totalRam) {

        this.buzon = buzon;
        this.id = id_usuario;
        this.base = base;
        this.buzon = buzon;
        this.totalCPUs = totalCPUs;
        this.totalRam = totalRam;

    }

    @Override 
    public void run() {

        //Realizamos el cálculo de número de solicitudes.
        int K = 5
        int numSolicitudes = id * K
        Random rand = new Random();
        
        
        for (int i = 0; i < numSolicitudes; i ++) {
            //Randomizamos para los parámetros de solicitud

            // Cálculo del total de solicitudes para este usuario
            int numSolicitudes = id * base;
        
        
            // A. Construir el ID único tipo "1_0", "1_1"...
            String idSolicitud = id + "_" + i;

            // B. Generar recursos aleatorios respetando los máximos (75%)
            int cpus = 1 + rand.nextInt((int) Math.round(totalCPUs * 0.75));
            
            int ramGen = 1 + rand.nextInt((int) Math.round(totalRam * 0.75));
            int memoriaGiB = Integer.highestOneBit(ramGen); // Ajusta a potencia de 2

            int discoGen = 1 + rand.nextInt((int) Math.round(totalDisco * 0.75));
            int almacenamientoGiB = (discoGen / 2) * 2;   // Ajusta a múltiplo de 2

            // C. Instanciar la Solicitud
            Solicitud solicitud = new Solicitud(idSolicitud, cpus, memoriaGiB, almacenamientoGiB);
            
            // D. Depositar en el buzón compartido (el buzón se encarga del wait si está lleno)
            buzon.depositar(solicitud); 
}


        buzon.reportarFinUsuario();
    }

}
