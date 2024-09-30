# A continuacion se van a presentar 3 correcciones:
- Method names should comply with a naming convention (java:S100)
  Varios metodos tienen nombres que empiezan con mayuscula. Se refactorizó los siguientes: Puntuacion, CalificarSecuencuencial, CalificarConcurente, Cronometrar y Comprobar.
  > public static double Puntacion(char respuesta, char clave) {} -> public static double puntacion(char respuesta, char clave) {}
  > public static List<Double> CalificarSecuencial() {} -> public static List<Double> calificarSecuencial() {}
  > public static List<Double> CalificarConcurrente() {} -> public static List<Double> calificarConcurrente() {}
  > public static void Cronometrar() {} -> public static void cronometrar() {}
  > public static void Comprobar() {} -> public static void comprobar() {}
- Unused local variables should be removed java:S1481
  Hay 2 variables que no se utizan. Estan se utilian para medir el tiempo de ejeccucion, pero no son necesarias.
  > List<Double> puntajesCS = CalificarSecuencial(); -> CalificarSecuencial();
  > List<Double> puntajesCC = CalificarConcurrente(); -> CalificarConcurrente();
- Standard outputs should not be used directly to log anything java:S106
  Hay varias instancias de System.out.println() que se utilizan para ver el funcionamiento del codigo. Se deberian pasar a logger.info()
  > System.out.println("Secuencial: " + elapsedTimeCS + " ms"); -> logger.info("Secuencial: " + elapsedTimeCS + " ms");
  > System.out.println("Concurente: " + elapsedTimeCC + " ms"); -> logger.info("Concurente: " + elapsedTimeCC + " ms");
  > System.out.println(iguales); -> logger.info(iguales);
