public class ControladorMemoria {
    // Recibe memoria total en GiB y tamaño de página en KiB
    public ControladorMemoria(int totalMemoriaGiB, int tamanoPaginaKiB) { ... }

    // Traduce memoriaGiB a páginas y retorna true si logra asignarlas
    public synchronized boolean intentarReservar(String idMV, int memoriaGiB) { ... }

    // Libera las páginas asignadas a esa MV
    public synchronized void liberar(String idMV) { ... }

    public synchronized boolean estanTodasLibres() { ... }
}