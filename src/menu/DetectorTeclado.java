package menu;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 * Detector de pulsacion de teclas para el menú.
 * Detecta las teclas de dirección (Arriba, Abajo) y
 * la tecla Enter.
 * @author plopez
 */
public class DetectorTeclado extends Behavior {

    Menu menuAControlar;
    WakeupOnAWTEvent presionada = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupCondition keepUpCondition = null;
    WakeupCriterion[] continueArray = new WakeupCriterion[2];

    public DetectorTeclado(Menu menuAControlar) {
        this.menuAControlar = menuAControlar;
        continueArray[0] = liberada;
        continueArray[1] = presionada;
        keepUpCondition = new WakeupOr(continueArray);
        this.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
    }

    @Override
    public void initialize() {
        wakeupOn(keepUpCondition);
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            try {
                WakeupCriterion ster = (WakeupCriterion) criteria.nextElement();
                if (ster instanceof WakeupOnAWTEvent) {
                    AWTEvent[] events = ((WakeupOnAWTEvent) ster).getAWTEvent();
                    for (int n = 0; n < events.length; n++) {
                        if (events[n] instanceof KeyEvent) {
                            KeyEvent ek = (KeyEvent) events[n];
                            if (ek.getID() == KeyEvent.KEY_PRESSED) {
                                if (ek.getKeyCode() == KeyEvent.VK_UP) {
                                    menuAControlar.arriba();
                                } else if (ek.getKeyCode() == KeyEvent.VK_DOWN) {
                                    menuAControlar.abajo();
                                } else if (ek.getKeyCode() == KeyEvent.VK_ENTER) {
                                    menuAControlar.enter();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        wakeupOn(keepUpCondition);
    }
}