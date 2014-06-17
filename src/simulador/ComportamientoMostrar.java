package simulador;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupOnElapsedFrames;

public class ComportamientoMostrar extends Behavior {
   WakeupOnElapsedFrames framewake = new WakeupOnElapsedFrames(0, true);
   AbstractGame juego;

public ComportamientoMostrar(AbstractGame juego_ ) {
     juego = juego_;
}

public void initialize() { 
    wakeupOn( framewake );
}

public void processStimulus(Enumeration criteria) {
    try{ juego.mostrar(); } catch(Exception e){}
    wakeupOn( framewake ); }
}