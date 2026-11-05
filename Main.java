import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

// aca desempacamos la config de parametros.txt y arrancamos todo el sistema
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Properties config = new Properties();

        FileInputStream archivos = new FileInputStream("parametros.txt");
        config.load(archivos);
        archivos.close();

        // leemos los parametros del archivo
        int nu = Integer.parseInt(config.getProperty("nu"));
        int base = Integer.parseInt(config.getProperty("base"));
        int ng = Integer.parseInt(config.getProperty("ng"));
        int tam1 = Integer.parseInt(config.getProperty("tam1"));
        int tam2 = Integer.parseInt(config.getProperty("tam2"));

        int totalCPUs = Integer.parseInt(config.getProperty("procesadores"));
        int totalRAM = Integer.parseInt(config.getProperty("memoria"));
        int tamPagina = Integer.parseInt(config.getProperty("pagina"));
        int totalDisco = Integer.parseInt(config.getProperty("almacenamiento"));
        int tamBloque = Integer.parseInt(config.getProperty("bloque"));

        // creamos los objetos compartidos
        BuzonEntrada buzon = new BuzonEntrada(tam1, nu);
        ZonaEspera zonadespera = new ZonaEspera(tam2);
        ControladorAlmacenamiento ctrlDisco = new ControladorAlmacenamiento(totalDisco, tamBloque);
        ControladorMemoria ctrlMemoria = new ControladorMemoria(totalRAM, tamPagina);
        ControladorProcesadores ctrlCPU = new ControladorProcesadores(totalCPUs);

        // listas para poder hacer join despues
        List<GestorSolicitudes> gestores = new ArrayList<>();
        List<Usuario> usuarios = new ArrayList<>();
        List<MV> listaMVs = Collections.synchronizedList(new ArrayList<>());

        System.out.println("Iniciando simulador...");
        System.out.println("usuarios: " + nu + ", gestores: " + ng + ", buzon: " + tam1 + ", umbral: " + tam2);

        // arrancamos los gestores
        for (int id = 1; id <= ng; id++) {
            GestorSolicitudes g = new GestorSolicitudes(id, buzon, zonadespera, ctrlCPU, ctrlMemoria, ctrlDisco, listaMVs);
            gestores.add(g);
            g.start();
        }

        // arrancamos los usuarios
        for (int id = 1; id <= nu; id++) {
            Usuario u = new Usuario(id, base, buzon, totalCPUs, totalRAM, totalDisco);
            usuarios.add(u);
            u.start();
        }

        // esperamos que acaben los usuarios
        for (Usuario u : usuarios) {
            u.join();
        }
        System.out.println("usuarios terminaron");

        // esperamos que los gestores procesen todo lo que quedaba en el buzon
        for (GestorSolicitudes g : gestores) {
            g.join();
        }
        System.out.println("gestores terminaron");

        // esperamos que todas las maquinas virtuales acaben su ejecucion
        int i = 0;
        while (true) {
            MV mv = null;
            synchronized (listaMVs) {
                if (i < listaMVs.size()) {
                    mv = listaMVs.get(i);
                    i++;
                }
            }
            if (mv != null) {
                mv.join();
            } else {
                break;
            }
        }
        System.out.println("todas las mvs terminaron");

        // revision final de que todo quedo limpio como pide el caso
        System.out.println("\n--- revision final ---");
        System.out.println("buzon vacio: " + (buzon.estaVacio() ? "si" : "no"));
        System.out.println("zona espera vacia: " + (zonadespera.estaVacia() ? "si" : "no"));
        System.out.println("cpus libres: " + (ctrlCPU.estanTodosLibres() ? "si" : "no"));
        System.out.println("memoria libre: " + (ctrlMemoria.estanTodasLibres() ? "si" : "no"));
        System.out.println("disco libre: " + (ctrlDisco.estanTodosLibres() ? "si" : "no"));
        System.out.println("total mvs: " + listaMVs.size());
    }
}
