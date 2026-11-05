// maneja las cpus del sistema
public class ControladorProcesadores {

    private final int totalProcesadores;
    // guardamos cual mv tiene cual cpu especifica
    private final String[] asignaciones;

    public ControladorProcesadores(int totalProcesadores) {
        this.totalProcesadores = totalProcesadores;
        this.asignaciones = new String[totalProcesadores];
    }

    // intenta apartar las cpus que pide la mv
    public synchronized boolean intentarReservar(String idMV, int cantidadCpus) {
        // contamos cuantas estan libres primero
        int libres = 0;
        for (int i = 0; i < totalProcesadores; i++) {
            if (asignaciones[i] == null) {
                libres++;
            }
        }

        // si no alcanzan de una decimos que no para que haga rollback
        if (libres < cantidadCpus) {
            return false;
        }

        // si si alcanzan las marcamos con el id de la mv
        int asignados = 0;
        for (int i = 0; i < totalProcesadores && asignados < cantidadCpus; i++) {
            if (asignaciones[i] == null) {
                asignaciones[i] = idMV;
                asignados++;
            }
        }
        return true;
    }

    // suelta las cpus de esa mv
    public synchronized void liberar(String idMV) {
        for (int i = 0; i < totalProcesadores; i++) {
            if (idMV.equals(asignaciones[i])) {
                asignaciones[i] = null;
            }
        }
    }

    // revisa si todas quedaron desocupadas
    public synchronized boolean estanTodosLibres() {
        for (int i = 0; i < totalProcesadores; i++) {
            if (asignaciones[i] != null) {
                return false;
            }
        }
        return true;
    }
}
