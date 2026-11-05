// acá guardamos lo que pide cada solicitud
public class Solicitud {
    private final String id;            // id del usuario y consecutivo tipo "1_0"
    private final int numProcesadores;  // cpus que pide
    private final int memoriaGiB;       // memoria en gib
    private final int almacenamientoGiB;// disco en gib

    public Solicitud(String id, int numProcesadores, int memoriaGiB, int almacenamientoGiB) {
        this.id = id;
        this.numProcesadores = numProcesadores;
        this.memoriaGiB = memoriaGiB;
        this.almacenamientoGiB = almacenamientoGiB;
    }

    public String getId() { return id; }
    public int getNumProcesadores() { return numProcesadores; }
    public int getMemoriaGiB() { return memoriaGiB; }
    public int getAlmacenamientoGiB() { return almacenamientoGiB; }
}
