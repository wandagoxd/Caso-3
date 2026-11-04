public class ControladorAlmacenamiento {
    // Recibe capacidad en GiB y tamaño de bloque en KiB
    public ControladorAlmacenamiento(int totalDiscoGiB, int tamanoBloqueKiB) { ... }

    // Traduce almacenamientoGiB a bloques y retorna true si logra asignarlos
    public synchronized boolean intentarReservar(String idMV, int almacenamientoGiB) { ... }

    // Libera los bloques asignados a esa MV
    public synchronized void liberar(String idMV) { ... }

    public synchronized boolean estanTodosLibres() { ... }
}