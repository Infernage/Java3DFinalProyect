package menu;


/**
 *
 * @author plopez
 */
public class Opcion {
    
    /**
     * Texto que representará la opción en el menú.
     * Se recomienda que sean pocas palabras.
     */
    String texto;
    
    /**
     * Objeto que contiene el método ejecutable
     * asociado a esta opción. Será el que se ejecute
     * cuando se seleccione esta opción.
     */
    Runnable ejecutable;
    
    public Opcion (String texto){
        this.texto = texto;
    }
    
    public String getTexto (){
        return texto;
    }
    
    public void setTexto (String texto){
        this.texto = texto;
    }
    
    public void setEjecutable (Runnable ejecutable){
        this.ejecutable = ejecutable;
    }
    
    public Runnable getEjecutable (){
        return ejecutable;
    }
    
    public void ejecutar(){
        new Thread(ejecutable).start();
    }
}