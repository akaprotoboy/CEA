import Clases.Clave;
import Clases.Rango;
import Clases.Respuesta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public abstract class Calificar {
    static List<Clave> claves = MySQLDatabaseHandler.getListaClave();
    static List<Respuesta> respuestas = MySQLDatabaseHandler.getListaRespuesta();

    static List<Rango> rangos = MySQLDatabaseHandler.getListaRango();
    static List<Rango> rangosAux = new ArrayList<>();

    static {
        modificarRango();
        for (int k = 0; k < 12; k++) {
            respuestas.addAll(respuestas);
        }
        System.out.println("Finished");
    }

    public static void modificarRango() {
        rangosAux.clear(); // Clear rangosAux before starting
        for (Rango rango : rangos) {
            boolean found = false;
            for (int i = 0; i < rangosAux.size(); i++) {
                Rango rangoAux = rangosAux.get(i);
                if (rango.getPosicion() == rangoAux.getPosicion()) {
                    found = true;
                    if (rango.getMax() >= rangoAux.getMax()) {
                        rangosAux.set(i, rango); // Update the existing entry
                    }
                    break; // Exit loop since we found the matching position
                }
            }
            if (!found) {
                rangosAux.add(rango); // Add the new rango if not found
            }
        }
    }

    public static double Puntacion(char respuesta, char clave) {
        double puntaje;
        if (respuesta == ' ') {
            puntaje = 0;
        } else if (respuesta == clave) {
            puntaje = 20;
        } else {
            puntaje = -1.125;
        }
        return puntaje;
    }

    public static int getRango(int respuesta) {
        int rango;
        if (respuesta <= rangosAux.get(0).getMax()) {
            rango = 0;
        } else if (respuesta <= rangosAux.get(1).getMax()) {
            rango = 1;
        } else {
            rango = 2;
        }
        return rango;
    }

    public static List<Double> CalificarSecuencial() {
        List<Double> puntajes = new ArrayList<>();

        for (Respuesta respuesta : respuestas) {
            double puntajeHabilidades = 0;
            double puntajeConocimientos = 0;
            Clave clave = claves.get(getRango(respuesta.getIndice()));
            for (int i = 0; i < 100; i++) {
                String claveAux = clave.getRespuesta();
                String respuestaAux = respuesta.getRespuesta();
                char cChar = claveAux.charAt(i);
                char rChar = respuestaAux.charAt(i);
                double puntaje = Puntacion(rChar, cChar);
                if (i < 30) {
                    puntajeHabilidades += puntaje;
                } else {
                    puntajeConocimientos += puntaje;
                }
            }
            puntajes.add(puntajeHabilidades + puntajeConocimientos);
        }
        return puntajes;
    }

    public static List<Double> CalificarConcurrente() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        CalificarTask mainTask = new CalificarTask(0, respuestas.size());
        return forkJoinPool.invoke(mainTask);
    }

    private static class CalificarTask extends RecursiveTask<List<Double>> {
        private final int start;
        private final int end;
        private static final int THRESHOLD = (respuestas.size()/Runtime.getRuntime().availableProcessors())+1; // Threshold for splitting tasks

        CalificarTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<Double> compute() {
            if (end - start <= THRESHOLD) {
                // If task is small enough, compute directly
                return calificarDirectamente();
            } else {
                // Split task into subtasks
                int mid = (start + end) / 2;
                CalificarTask leftTask = new CalificarTask(start, mid);
                CalificarTask rightTask = new CalificarTask(mid, end);
                invokeAll(leftTask, rightTask);
                List<Double> leftResult = leftTask.join();
                List<Double> rightResult = rightTask.join();
                List<Double> result = new ArrayList<>();
                result.addAll(leftResult);
                result.addAll(rightResult);
                return result;
            }
        }

        private List<Double> calificarDirectamente() {
            List<Double> puntajes = new ArrayList<>();
            for (int i = start; i < end; i++) {
                Respuesta respuesta = respuestas.get(i);
                double puntajeHabilidades = 0;
                double puntajeConocimientos = 0;
                Clave clave = claves.get(getRango(respuesta.getIndice()));
                for (int j = 0; j < 100; j++) {
                    String claveAux = clave.getRespuesta();
                    String respuestaAux = respuesta.getRespuesta();
                    char cChar = claveAux.charAt(j);
                    char rChar = respuestaAux.charAt(j);
                    double puntaje = Puntacion(rChar, cChar);
                    if (j < 30) {
                        puntajeHabilidades += puntaje;
                    } else {
                        puntajeConocimientos += puntaje;
                    }
                }
                puntajes.add(puntajeHabilidades + puntajeConocimientos);
            }
            return puntajes;
        }
    }

    public static void Cronometrar() {
        long startTimeCS = System.currentTimeMillis();
        List<Double> puntajesCS = CalificarSecuencial();
        long endTimeCS = System.currentTimeMillis();
        long elapsedTimeCS = endTimeCS - startTimeCS;
        System.out.println("Secuencial: " + elapsedTimeCS + " ms");

        long startTimeCC = System.currentTimeMillis();
        List<Double> puntajesCC = CalificarConcurrente();
        long endTimeCC = System.currentTimeMillis();
        long elapsedTimeCC = endTimeCC - startTimeCC;
        System.out.println("Concurente: " + elapsedTimeCC + " ms");
    }

    public static void Comprobar() {
        List<Double> puntajesCS = CalificarSecuencial();
        List<Double> puntajesCC = CalificarConcurrente();
        boolean iguales = true;

        for (int i = 0; i < puntajesCS.size(); i++) {
            if (!Objects.equals(puntajesCS.get(i), puntajesCC.get(i))) {
                iguales = false;
                break;
            }
        }
        System.out.println(iguales);
    }
}
