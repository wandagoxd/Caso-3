import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// controla la memoria ram por paginas
public class ControladorMemoria {

    private final int totalMemoriaGiB;
    private final int tamanoPaginaKiB;
    private final long totalPaginas;
    private long paginasLibres;

    // guardamos que rangos de paginas tiene cada mv
    private final Map<String, List<long[]>> asignacionesPorMV;
    // aca guardamos los pedazos de paginas que estan libres
    private final List<long[]> rangosLibres;

    public ControladorMemoria(int totalMemoriaGiB, int tamanoPaginaKiB) {
        this.totalMemoriaGiB = totalMemoriaGiB;
        this.tamanoPaginaKiB = tamanoPaginaKiB;

        // pasamos de gib a kib y sacamos el total de paginas
        this.totalPaginas = ((long) totalMemoriaGiB * 1024 * 1024) / tamanoPaginaKiB;
        this.paginasLibres = totalPaginas;
        this.asignacionesPorMV = new HashMap<>();
        this.rangosLibres = new ArrayList<>();

        // al principio todas las paginas estan libres
        if (totalPaginas > 0) {
            rangosLibres.add(new long[]{0, totalPaginas - 1});
        }
    }

    // traduce los gib a paginas y mira si las puede apartar
    public synchronized boolean intentarReservar(String idMV, int memoriaGiB) {
        // pasamos los gib a paginas
        long paginasRequeridas = ((long) memoriaGiB * 1024 * 1024) / tamanoPaginaKiB;

        // si no alcanzan ni pa que intentarlo
        if (paginasLibres < paginasRequeridas) {
            return false;
        }

        // vamos sacando de los huecos que tenemos libres
        long restantes = paginasRequeridas;
        List<long[]> paginasAsignadas = new ArrayList<>();

        for (int i = 0; i < rangosLibres.size() && restantes > 0; i++) {
            long[] rango = rangosLibres.get(i);
            long tamRango = rango[1] - rango[0] + 1;

            if (tamRango <= restantes) {
                // cogemos todo este pedazo
                paginasAsignadas.add(new long[]{rango[0], rango[1]});
                restantes -= tamRango;
                rangosLibres.remove(i);
                i--;
            } else {
                // cogemos solo lo que nos falta de este pedazo
                long inicio = rango[0];
                long fin = inicio + restantes - 1;
                paginasAsignadas.add(new long[]{inicio, fin});
                rango[0] = fin + 1;
                restantes = 0;
            }
        }

        if (restantes == 0) {
            paginasLibres -= paginasRequeridas;
            asignacionesPorMV.put(idMV, paginasAsignadas);
            return true;
        } else {
            // si algo raro pasa devolvemos lo que habiamos cogido
            devolverRangosALibres(paginasAsignadas);
            return false;
        }
    }

    // suelta las paginas de esa maquina
    public synchronized void liberar(String idMV) {
        List<long[]> rangos = asignacionesPorMV.remove(idMV);
        if (rangos != null) {
            for (long[] r : rangos) {
                paginasLibres += (r[1] - r[0] + 1);
            }
            devolverRangosALibres(rangos);
        }
    }

    // devuelve los rangos a la lista de libres y los pega si quedan seguidos
    private void devolverRangosALibres(List<long[]> devueltos) {
        rangosLibres.addAll(devueltos);
        rangosLibres.sort((a, b) -> Long.compare(a[0], b[0]));

        List<long[]> fusionados = new ArrayList<>();
        for (long[] actual : rangosLibres) {
            if (fusionados.isEmpty()) {
                fusionados.add(new long[]{actual[0], actual[1]});
            } else {
                long[] ultimo = fusionados.get(fusionados.size() - 1);
                if (ultimo[1] + 1 >= actual[0]) {
                    ultimo[1] = Math.max(ultimo[1], actual[1]);
                } else {
                    fusionados.add(new long[]{actual[0], actual[1]});
                }
            }
        }
        rangosLibres.clear();
        rangosLibres.addAll(fusionados);
    }

    // chequear si toda la memoria volvio a quedar libre
    public synchronized boolean estanTodasLibres() {
        return paginasLibres == totalPaginas && asignacionesPorMV.isEmpty();
    }
}
