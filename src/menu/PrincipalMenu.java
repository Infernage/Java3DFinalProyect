/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Menu;

import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Container;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import simulador.IAvsIA;
import simulador.Juego;

/**
 *
 * @author plopez
 */
public class PrincipalMenu extends JFrame{
    
    public static SimpleUniverse universo;
    public static JFrame ventana;
    public static String[] argumentos;
    
    public PrincipalMenu (){
        Container miPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D (SimpleUniverse.getPreferredConfiguration());
        miPanel.add(zonaDibujo);
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
    }
    
    public static void main (String args[]){
        argumentos = args;
        ventana = new PrincipalMenu ();
        ventana.setTitle ("");
        ventana.setSize(800,600);
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public BranchGroup crearEscena (){
        BranchGroup objRoot = new BranchGroup();
        
        menu.Menu menuPrincipal = new menu.Menu();
        
        menu.Opcion opcionSalir = new menu.Opcion ("Salir del juego");
        opcionSalir.setEjecutable (new Runnable(){
            public void run (){
                PrincipalMenu.ventana.dispose();
                System.exit(0);
            }
        });
        
        menu.Opcion opcionJugadorIA = new menu.Opcion ("Jugador vs Bot");
        opcionJugadorIA.setEjecutable(new Runnable(){
            public void run (){      
                universo.removeAllLocales();
                PrincipalMenu.ventana.setVisible(false);
                Juego.principal(argumentos);
                PrincipalMenu.ventana.dispose();
            }
        });
        
        menu.Opcion opcionIAIA = new menu.Opcion ("Bot vs Bot");
        opcionIAIA.setEjecutable(new Runnable(){
            public void run(){
                universo.removeAllLocales();
                PrincipalMenu.ventana.setVisible(false);
                IAvsIA.principal(argumentos);
                PrincipalMenu.ventana.dispose();
            }
        });
        
        menuPrincipal.anadirOpcion(opcionJugadorIA);
        menuPrincipal.anadirOpcion(opcionIAIA);
        menuPrincipal.anadirOpcion(opcionSalir);
        
        objRoot.addChild(menuPrincipal.conjuntoMenu());
        
        return objRoot;
    }
}
