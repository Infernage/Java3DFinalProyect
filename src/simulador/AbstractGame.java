/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulador;

import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.swing.JFrame;
import javax.vecmath.Point3d;

/**
 *
 * @author Alberto
 */
public abstract class AbstractGame extends JFrame implements Runnable{
    public static AbstractGame game;
    public abstract void mostrar() throws Exception;
    public abstract void colocarCamara(SimpleUniverse universo, Point3d posicionCamara, Point3d objetivoCamara);
    public boolean mostrandoFisicas;
    public int estadoJuego = 0;
    public SimpleUniverse universo;
}
