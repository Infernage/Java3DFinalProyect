/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import com.sun.j3d.utils.geometry.ColorCube;
import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 *
 * @author plopez
 */
public class Menu {

    BranchGroup conjunto;
    Vector3f ultimaPosicion;
    float separacionLineas;
    List<Opcion> listaOpciones;
    List<Texto> listaTextos;
    DetectorTeclado teclado;
    TransformGroup alturaMenu;
    
    int opcionSeleccionada = 0;
    int numeroDeOpciones = 0;

    public Menu() {
        conjunto = new BranchGroup();
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ultimaPosicion = new Vector3f(0, 0, 0);

        listaOpciones = new ArrayList();
        listaTextos = new ArrayList();
        separacionLineas = 0.25f;
        
        alturaMenu = new TransformGroup();
        alturaMenu.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        conjunto.addChild(alturaMenu);
        
        teclado = new DetectorTeclado(this);
        conjunto.addChild(teclado);
    }

    public BranchGroup conjuntoMenu() {

        return conjunto;
    }

    public void anadirOpcion(Opcion opcion) {
        BranchGroup nuevoBloque = new BranchGroup();
        Transform3D traslacion = new Transform3D();
        traslacion.setTranslation(ultimaPosicion);
        TransformGroup transformacion = new TransformGroup(traslacion);
        nuevoBloque.addChild(transformacion);
        Texto nuevoTexto = new Texto(opcion.getTexto());
        listaTextos.add(nuevoTexto);
        transformacion.addChild(nuevoTexto.crearTexto());
        ultimaPosicion.y -= separacionLineas;
        listaOpciones.add(opcion);
        alturaMenu.addChild(nuevoBloque);
        numeroDeOpciones++;
        Transform3D elevar = new Transform3D();
        elevar.setTranslation(new Vector3f(0, -ultimaPosicion.y/4, 0));
        alturaMenu.setTransform(elevar);
    }

    /**
     * Método que se llama cuando se pulsa la tecla arriba.
     */
    public void arriba() {
        if (numeroDeOpciones > 0) {
            listaTextos.get(opcionSeleccionada).noRemarcar();
        }
        opcionSeleccionada--;
        if (opcionSeleccionada < 0) {
            opcionSeleccionada = 0;
        }
        if (numeroDeOpciones > 0) {
            listaTextos.get(opcionSeleccionada).remarcar();
        }
    }

    /**
     * Método que se llama cuando se pulsa la tecla abajo.
     */
    public void abajo() {
        if (numeroDeOpciones > 0) {
            listaTextos.get(opcionSeleccionada).noRemarcar();
        }
        opcionSeleccionada++;
        if (opcionSeleccionada >= numeroDeOpciones) {
            opcionSeleccionada = numeroDeOpciones-1;
        }
        if (numeroDeOpciones > 0) {
            listaTextos.get(opcionSeleccionada).remarcar();
        }
    }

    /**
     * Método que se llama cuando se pulsa la tecla enter.
     */
    public void enter() {
        if (numeroDeOpciones > 0) {
            ejecutarSeleccionada();
        }
    }

    private void ejecutarSeleccionada() {
        listaOpciones.get(opcionSeleccionada).ejecutar();
    }

}
