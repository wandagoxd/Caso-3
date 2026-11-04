import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.stream.FileImageInputStream;

//Usamos todas estas clases de acá arriba para desempacar la estructura entre comillas llave valor que tenemos ya creada
//en parametros.txt
public class Main {
    public static void main(){
        Properties config = new Properties();

        FileInputStream archivos = new FileInputStream("parametros.txt")
        config.load(archivos)
        archivos.close()

        //Ahora leemos cada parámetro y le damos el mismo nombre que le dimos en parametros.txt

        int nu = Integer.parseInt(config.getProperty("nu"));
        int base = Integer.parseInt(config.getProperty("base"));
        int ng = Integer.parseInt(config.getProperty("ng"));
        //Capacidad del buzon es tam1
        int tam1 = Integer.parseInt(config.getProperty("tam1"));
        //Capacidad de zona espera es tam2
        int tam2 = Integer.parseInt(config.getProperty("tam2"));
        
        int totalCPUs = Integer.parseInt(config.getProperty("procesadores"));
        int totalRAM = Integer.parseInt(config.getProperty("memoria"));
        int tamPagina = Integer.parseInt(config.getProperty("pagina"));
        int totalDisco = Integer.parseInt(config.getProperty("almacenamiento"));
        int tamBloque = Integer.parseInt(config.getProperty("bloque"));

        //Ahora creamos los nuevos objetos globales del sistema buzón y zona de espera
        Buzon buzon= new BuzonEntrada(tam1)
        ZonaEspera zonadespera= new ZonaEspera(tam2)

        // Ahora instanciamos los hilos de usuario, que entrarán a heap
        for (int id = 1; id <= nu; id++) {
                Usuario u = new Usuario(id, base, buzon, totalCPUs, totalRAM, totalDisco);
                u.start();

    }
    
}
}