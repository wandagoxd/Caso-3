public class ControladorProcesadores {
    public ControladorProcesadores(int totalProcesadores) { ... }

    // Retorna true si pudo reservar todas las CPUs solicitadas; false si no alcanza
    public synchronized boolean intentarReservar(String idMV, int cantidadCpus) { ... }

    // Libera las CPUs asignadas a esa MV
    public synchronized void liberar(String idMV) { ... }

    // Verifica que todos los recursos volvieron al estado inicial libre
    public synchronized boolean estanTodosLibres() { ... }
}