/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import java.awt.Color;
import menu.Opcion;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;


public class Texto{
    
    boolean remarcado;
    Appearance apariencia, aparienciaRemarcado;
    Font3D fuente;
    Text3D texto;
    Shape3D shapeTexto;
    String textoS;

    public Texto (String texto){
        remarcado = false;
        this.textoS = texto;
    }
    
    public Texto (){
        remarcado = false;
    }
    
    public void mostrar(){
        if (remarcado){
            shapeTexto.setAppearance(aparienciaRemarcado);
        }else{
            shapeTexto.setAppearance(apariencia);
        }
    }
    
    /**
     * Crea un branchGroup con el texto.
     * @return 
     */
    public BranchGroup crearTexto(){
        BranchGroup resultado = new BranchGroup ();
        shapeTexto = new Shape3D();
        apariencia = new Appearance ();
        apariencia.setColoringAttributes(
                new ColoringAttributes(new Color3f(0f, 0.7f, 1f), ColoringAttributes.FASTEST));
        aparienciaRemarcado = new Appearance();
        aparienciaRemarcado.setColoringAttributes(
                new ColoringAttributes(new Color3f(0f, 0.4f, 0.7f), ColoringAttributes.FASTEST));
        fuente = new Font3D (new Font("Helvetica", Font.PLAIN, 1), new FontExtrusion());
        texto = new Text3D (fuente, textoS);
        texto.setAlignment(Text3D.ALIGN_CENTER);
        shapeTexto.setGeometry(texto);
        shapeTexto.setAppearance(apariencia);
        shapeTexto.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        Transform3D escalar = new Transform3D();
        escalar.setScale(0.2);
        TransformGroup grupoEscala = new TransformGroup(escalar);
        resultado.addChild(grupoEscala);
        grupoEscala.addChild(shapeTexto);
        return resultado;
    }
    
    public void remarcar(){
        remarcado = true;shapeTexto.setAppearance(aparienciaRemarcado);
    }
    
    public void noRemarcar(){
        remarcado = false;shapeTexto.setAppearance(apariencia);
    }
    
}
