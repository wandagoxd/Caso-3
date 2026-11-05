import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// aca controlamos el disco duro por bloques
public class ControladorAlmacenamiento {

    private final int totalDiscoGiB;
    private final int tamanoBloqueKiB;
    private final long totalBloques;
    private long bloquesLibres;

    // guardamos que bloques tiene cada mv
    private final Map<String, List<long[]>> asignacionesPorMV;
    // aca guardamos los pedazos de bloques libres
    private final List<long[]> rangosLibres;

    public ControladorAlmacenamiento(int totalDiscoGiB, int tamanoBloqueKiB) {
        this.totalDiscoGiB = totalDiscoGiB;
        this.tamanoBloqueKiB = tamanoBloqueKiB;

        // pasamos de gib a kib y calculamos total de bloques
        this.totalBloques = ((long) totalDiscoGiB * 1024 * 1024) / tamanoBloqueKiB;
        this.bloquesLibres = totalBloques;
        this.asignacionesPorMV = new HashMap<>();
        this.rangosLibres = new ArrayList<>();

        // arrancamos con todos los bloques disponibles
        if (totalBloques > 0) {
            rangosLibres.add(new long[]{0, totalBloques - 1});
        }
    }

    // pasa los gib a bloques y mira si alcanza
    public synchronized boolean intentarReservar(String idMV, int almacenamientoGiB) {
        // pasamos los gib pedidos a numero de bloques
        long bloquesRequeridos = ((long) almacenamientoGiB * 1024 * 1024) / tamanoBloqueKiB;

        // si no hay suficientes bloques libres chao
        if (bloquesLibres < bloquesRequeridos) {
            return false;
        }

        // vamos sacando espacio de los huecos libres
        long restantes = bloquesRequeridos;
        List<long[]> bloquesAsignados = new ArrayList<>();

        for (int i = 0; i < rangosLibres.size() && restantes > 0; i++) {
            long[] rango = rangosLibres.get(i);
            long tamRango = rango[1] - rango[0] + 1;

            if (tamRango <= restantes) {
                // cogemos el bloque completo
                bloquesAsignados.add(new long[]{rango[0], rango[1]});
                restantes -= tamRango;
                rangosLibres.remove(i);
                i--;
            } else {
                // cogemos solo la parte que nos falta
                long inicio = rango[0];
                long fin = inicio + restantes - 1;
                bloquesAsignados.add(new long[]{inicio, fin});
                rango[0] = fin + 1;
                restantes = 0;
            }
        }

        if (restantes == 0) {
            bloquesLibres -= bloquesRequeridos;
            asignacionesPorMV.put(idMV, bloquesAsignados);
            return true;
        } else {
            // si algo falla devolvemos lo que habiamos apartado
            devolverRangosALibres(bloquesAsignados);
            return false;
        }
    }

    // libera los bloques que tenia esa mv
    public synchronized void liberar(String idMV) {
        List<long[]> rangos = asignacionesPorMV.remove(idMV);
        if (rangos != null) {
            for (long[] r : rangos) {
                bloquesLibres += (r[1] - r[0] + 1);
            }
            devolverRangosALibres(rangos);
        }
    }

    // devuelve los bloques a la lista y pega los continuos
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

    // revisa si todo el disco quedo libre otra vez
    public synchronized boolean estanTodosLibres() {
        return bloquesLibres == totalBloques && asignacionesPorMV.isEmpty();
    }
}
