public class ZonaEspera {
    public ZonaEspera(int umbral) { ... }

    // Llamado por Gestor: Consulta si superó el umbral
    public synchronized boolean superaUmbral() { ... }

    // Llamado por Gestor: Espera pasiva hasta que las pendientes lleguen a 0
    public synchronized void esperarVaciadoTotal() throws InterruptedException { ... }

    // Llamado por MV: Suspende a la MV (espera pasiva) si falló en conseguir recursos
    public synchronized void entrarEnEspera(MV mv) throws InterruptedException { ... }

    // Llamado por MV al liberar recursos: Despierta a las MVs dormidas
    public synchronized void notificarDisponibilidad() { ... }

    // Llamado por el Main o validadores al final del programa
    public synchronized boolean estaVacia() { ... }
}