package com.example.meteogalicia.NoModificar;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MeteoGaliciaCortoPlazo {

    // Inicializamos el array de localidades con los códigos que usa Meteogalicia
    private final static LocalidadMeteoGalicia[] localidades = {new LocalidadMeteoGalicia("A Coruña", "15030"), new LocalidadMeteoGalicia("Ferrol", "15036"), new LocalidadMeteoGalicia("Lugo", "27028"), new LocalidadMeteoGalicia("Ourense", "32054"), new LocalidadMeteoGalicia("Pontevedra", "36038"), new LocalidadMeteoGalicia("Santiago de Compostela", "15078"), new LocalidadMeteoGalicia("Vigo", "36057")};

    public static LocalidadMeteoGalicia[] getLocalidades() {
        return localidades;
    }

    // Devuelve la ruta hacia el fichero html con el pronóstico descargado del servidor de MeteoGalicia de la localidad correspondiente.
    public static String obtenPronostico(Context contexto, LocalidadMeteoGalicia localidad) {
        InputStream entrada = null;
        String nombreFichero = null;

        try {
            // Si existe de una ejecucion anterior borramos el fichero
            nombreFichero = localidad.getCodigo() + ".html";
            File dir = contexto.getFilesDir();
            System.out.println(dir);

            File fichero = new File(dir, nombreFichero);
            fichero.delete();


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            dbf.setCoalescing(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            // Creamos objeto URL a partir de la direccion web para conectarnos con el servidor
            URL url = new URL("https://servizos.meteogalicia.gal/rss/predicion/rssLocalidades.action?idZona=" + localidad.getCodigo() + "&dia=-1&request_locale=es");

            HttpURLConnection conex = (HttpURLConnection) url.openConnection(); // Abrimos la conexion
            conex.setConnectTimeout(3000); // Esperamos máximo 2 segundos por la respuessta del servidor
            conex.setReadTimeout(3000);
            conex.setUseCaches(false); // Evitamos la cache de datos.
            conex.setRequestProperty("accept", "text/xml"); // Indicamos formato a recibir

            // Abrimos el fichero para su lectura/descarga
            entrada = conex.getInputStream();

            Document arbolXML = db.parse(entrada);
            Element raiz = arbolXML.getDocumentElement();
            raiz.normalize();

            NodeList listaItems = raiz.getElementsByTagName("item");

            StringBuffer sb = new StringBuffer();

            if (listaItems.getLength() > 0)
                sb.append("<hr><h1>" + localidad.getNombre() + "</h1><hr>");

            for (int i = 0; i < listaItems.getLength(); i++) {
                try {
                    Element item = (Element) listaItems.item(i);
                    sb.append("<h2>" + item.getElementsByTagName("title").item(0).getFirstChild().getNodeValue().trim() + "</h2>");
                    sb.append(item.getElementsByTagName("description").item(0).getFirstChild().getNodeValue().trim());
                } catch (Exception e) {
                    Log.e("Pronostico", "Error de parseo XML al obtener el pronóstico.");
                    e.printStackTrace();
                    return null;
                }
            }

            if (sb.length() == 0) {
                Log.e("Pronostico", "No se han obtenido datos para localidad: " + localidad.getNombre());
                return null;
            }

            String cabecera = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "\t<title>Pronósticos MeteoGalicia Corto Plazo</title>\n" + "<meta charset=\"UTF-8\">\n" + "<style>\n" + "h1 {margin:0.2em; text-align:center; font-size:1.5em}\n" + "h2 {font-size: 1.2em; text-align:center; color:#787b00;}\n" + " table {width:100% !important; font-size:0.8em}\n" + "</style>\n" + "</head>\n" + "\n" + "<body>";
            String pie = "</body>\n</html>";
            String texto = cabecera + sb.toString().replaceAll("/datosred", "https://servizos.meteogalicia.gal/datosred") + pie;
            FileOutputStream outputStream;

            try {
                outputStream = contexto.openFileOutput(nombreFichero, Context.MODE_PRIVATE);
                outputStream.write(texto.getBytes());
                outputStream.close();
            } catch (Exception e) {
                Log.e("Pronostico", "Error al guardar el fichero.");
                e.printStackTrace();
                return null;
            }

            return fichero.toString();
        } catch (Exception e) {
            Log.e("Pronostico", "Error al establecer conexión.");
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        } finally {
            try {
                entrada.close();
            } catch (Exception e) {
            }
        }


    }

    // Clase para almacenar los datos de una localidad (su nombre y código de MeteoGalicia
    public static class LocalidadMeteoGalicia {
        private String nombre;
        private String codigo;

        LocalidadMeteoGalicia(String nombre, String codigo) {
            this.nombre = nombre;
            this.codigo = codigo;

        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
