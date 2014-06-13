package simulador;

import figuras.EsferaMDL;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {

    Figura personaje;
    WakeupOnAWTEvent presionada = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupCondition keepUpCondition = null;
    WakeupCriterion[] continueArray = new WakeupCriterion[2];
    boolean neg;
    

    public DeteccionControlPersonaje(Figura _personaje, boolean negate) {
        personaje = _personaje;
        neg = negate;
        continueArray[0] = liberada;
        continueArray[1] = presionada;
        keepUpCondition = new WakeupOr(continueArray);
    }

    public void initialize() {
        wakeupOn(keepUpCondition);
    }

    public void processStimulus(Enumeration criteria) {
        
        while (criteria.hasMoreElements()) {
            
            WakeupCriterion ster = (WakeupCriterion) criteria.nextElement();
            if (ster instanceof WakeupOnAWTEvent) {
                AWTEvent[] events = ((WakeupOnAWTEvent) ster).getAWTEvent();
                for (int n = 0; n < events.length; n++) {
                    if (events[n] instanceof KeyEvent) {
                        KeyEvent ek = (KeyEvent) events[n];
                        EsferaMDL pj = (EsferaMDL) personaje;                    
                        if (ek.getID() == KeyEvent.KEY_PRESSED) {
                            if (ek.getKeyChar() == 'd' && personaje.ataque==false 
                                    && personaje.ataqueFuerte==false && personaje.parar==false) {
                                pj.ab.playAnimation(pj.nombreAnimacionCaminando, false);
                                if (neg){
                                    personaje.atras = true;
                                } else{
                                    personaje.adelante = true;
                                }
                            } else if (ek.getKeyChar() == 'a'  && personaje.ataque==false
                                    && personaje.ataqueFuerte==false && personaje.parar==false) {
                                pj.ab.playAnimation(pj.nombreAnimacionCaminando, false);
                                if (!neg){
                                    personaje.atras = true;
                                } else{
                                    personaje.adelante = true;
                                }
                            } else if (ek.getKeyChar() == 'z'&& personaje.ataque==false
                                    && personaje.ataqueFuerte==false && personaje.parar==false){
                                pj.ab.playAnimation(pj.nombreAnimacionLuchando, false);
                                personaje.ataque = true;
                                personaje.adelante=false;
                                personaje.atras=false;
                            } else if (ek.getKeyChar() == 'x'&& personaje.ataque==false
                                    && personaje.ataqueFuerte==false && personaje.parar==false){
                                pj.ab.playAnimation(pj.nombreAnimacionLuchandoFuerte, false);
                                personaje.ataqueFuerte = true;
                                personaje.adelante=false;
                                personaje.atras=false;
                            } else if (ek.getKeyChar() == 'c'&& personaje.ataque==false
                                    && personaje.ataqueFuerte==false && personaje.parar==false){
                                pj.ab.playAnimation(pj.nombreAnimacionParando, true);
                                personaje.parar = true;
                            } else if (ek.getKeyChar() == 'w'){
                                personaje.saltar = true;
                            }
                        } else if (ek.getID() == KeyEvent.KEY_RELEASED) {
                            if (ek.getKeyChar() == 'd') {
                                if (neg){
                                    personaje.atras = false;
                                } else{
                                    personaje.adelante = false;
                                }
                            } else if (ek.getKeyChar() == 'a') {
                                if (!neg){
                                    personaje.atras = false;
                                } else{
                                    personaje.adelante = false;
                                }
                            } else if (ek.getKeyChar() == 'z'){
                                personaje.ataque = false;
                            } else if (ek.getKeyChar() == 'x'){
                                personaje.ataqueFuerte = false;
                            } else if (ek.getKeyChar() == 'c'){
                                personaje.parar = false;
                            } else if (ek.getKeyChar() == 'w'){
                                personaje.saltar = false;
                            }
                        }
                    }
                }
            }
        }
         
        
        wakeupOn(keepUpCondition);
    }
}
