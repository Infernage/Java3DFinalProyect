package figuras;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.sun.j3d.loaders.Scene;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;
import simulador.*;
import utilidades.CapabilitiesMDL;

public class EsferaMDL extends Figura {

    public Scene escenaPersonaje1;
    public AnimationBehavior ab = null;
    public String nombreAnimacionCorriendo, nombreAnimacionCaminando, nombreAnimacionQuieto, nombreAnimacionLuchando,
            nombreAnimacionLuchandoFuerte, nombreAnimacionParando;
    Vector3d direccion = new Vector3d(0, 0, 10);
    public float radio, alturaP, alturaDeOjos;
    

    public EsferaMDL(String ficheroMDL, float radio, BranchGroup conjunto, ArrayList<Figura> listaObjetos,
            AbstractGame juego, boolean esPersonaje, boolean player2) {
        super(conjunto, listaObjetos, juego, player2);
        esMDL = true;
        this.esPersonaje = esPersonaje;
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        this.radio = radio;
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de la forma visual MDL
        //nombre = "figura_MDL_" + identificador;
        //Sphere figuraVisual = new Sphere(radio);
        TransformGroup figuraVisual = crearObjetoMDL(ficheroMDL, radio * 2, player2);
        SphereShape figuraFisica = new SphereShape(radio);
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);

        //Creaci�n de detector de teclas asociado a este cono
        if (esPersonaje) {
            DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(this, player2);
            mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
            ramaVisible.addChild(mueve);
        }
    }

    TransformGroup crearObjetoMDL(String archivo, float multiplicadorEscala, boolean player2) {
        BranchGroup RamaMDL = new BranchGroup();
        float rotacionX = 0;
        float rotacionY = 0;
        float rotacionZ = 0;
        float escalaTamano = 1f;
        float desplazamientoY = 0;
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            escenaPersonaje1 = nwn2.load(new URL("file://localhost/" + System.getProperty("user.dir") + "/" + archivo));
            RamaMDL = escenaPersonaje1.getSceneGroup();
            //Recorrido por los- objetos para darle capacidades a sus Shapes3D
            CapabilitiesMDL.setCapabilities(RamaMDL, this.identificadorFigura);
            //Para cada Objeto MDL dar nombre las animaciones de la figura. Dar rotaciones a la figuraMDL (suelen venir giradas)
            ab = (AnimationBehavior) escenaPersonaje1.getNamedObjects().get("AnimationBehavior");
            nombreAnimacionCaminando = "air_elemental:cwalk";
            nombreAnimacionCorriendo = "air_elemental:crun";
            nombreAnimacionLuchando = "air_elemental:ca1slashl";
            nombreAnimacionLuchandoFuerte = "air_elemental:ca1stab";
            nombreAnimacionParando = "air_elemental:cpause1";
            nombreAnimacionQuieto = "air_elemental:ctaunt";
            List anims = new ArrayList<>();
            anims.add(nombreAnimacionQuieto);
            ab.setDefaultAnimations(anims);
            rotacionX = -1.5f;
            rotacionZ = player2 ? 3.14f : 0;
            escalaTamano = 0.8f;
            desplazamientoY = -1f;
            alturaP = 2f;
            alturaDeOjos = 1.5f * escalaTamano;
            ab.playAnimation(nombreAnimacionQuieto, true);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.out.println("Error during load mdl");
        }

        //Ajustando rotacion inicial de la figura MLD y aplicando tamano
        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.set(new Vector3f(0, desplazamientoY, 0));
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotX(rotacionX);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotZ(rotacionZ);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(rotacionY);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(escalaTamano * multiplicadorEscala);
        rotacionCombinada.mul(correcionTemp);
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotadorDeFIguraMDL.addChild(RamaMDL);
        return rotadorDeFIguraMDL;
    }
}
