public class Solicitud {
    private final String id;            // Formato: "<idUsuario>_<consecutivo>"
    private final int numProcesadores;  // Cantidad entera de CPUs
    private final int memoriaGiB;       // Cantidad en GiB (potencia de 2)
    private final int almacenamientoGiB;// Cantidad en GiB (múltiplo de 2)

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