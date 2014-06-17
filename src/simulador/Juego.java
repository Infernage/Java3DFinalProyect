package simulador;

import Menu.PrincipalMenu;
import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import com.sun.j3d.utils.geometry.Text2D;
import figuras.Esfera;
import figuras.EsferaMDL;

public class Juego extends JFrame implements Runnable {

    public static final int POS_SCENE_L = 41, POS_SCENE_R = 19, POS_PJ1 = 32, POS_PJ2 = 28;
    public static Juego game;

    int estadoJuego = 0;
    SimpleUniverse universo;
    BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    Thread hebra = new Thread(this);
    ArrayList<simulador.Figura> listaObjetosFisicos = new ArrayList<>();
    ArrayList<simulador.Figura> listaObjetosNoFisicos = new ArrayList<>();
    DiscreteDynamicsWorld mundoFisico;
    BranchGroup conjunto = new BranchGroup();
    public boolean actualizandoFisicas, mostrandoFisicas;
    public float tiempoJuego;
    // Pesonajes importantes del juego
    Figura personaje;  //golem;
    Figura personaje2;
    LifeBar lifeBar1, lifeBar2;
    ControladorIA ai;

    public Juego() {
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        AxisSweep3 broadphase = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        mundoFisico = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        mundoFisico.setGravity(new Vector3f(0, -10, 0));

        Container GranPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);

        hebra.start();
    }

    BranchGroup crearEscena() {
        BranchGroup objRoot = new BranchGroup();
        conjunto = new BranchGroup();
        objRoot.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0); //Localizacion de fuente/paso de luz
        objRoot.addChild(LuzDireccional);
        mostrar.setSchedulingBounds(limites);
        LuzDireccional.setInfluencingBounds(limitesLuz);
        Background bg = new Background();
        bg.setApplicationBounds(limites);
        bg.setColor(new Color3f(135f / 256, 206f / 256f, 250f / 256f));
        objRoot.addChild(bg);
        objRoot.addChild(mostrar);

        //Es sencillo crearlos estaticos como se muestra a continuacion. Si desea que caigan, y se sometan a fuerzas, mejor crear una figura.
       /* float radio = 2f;
         float posY = -4f;
         // tutorial de creacion de una objeto   (la piedra con textura de ladrillo)
         // parte gr�fica del objeto:
         Appearance apariencia = new Appearance();
         apariencia.setTexture(new TextureLoader(System.getProperty("user.dir") + "//texturas//ladrillo.jpg", this).getTexture());
         TextureAttributes texAttr = new TextureAttributes();
         texAttr.setTextureMode(TextureAttributes.MODULATE);
         apariencia.setTextureAttributes(texAttr);
         Sphere figuraVisual = new Sphere(radio, Sphere.GENERATE_TEXTURE_COORDS, 60, apariencia);
         Transform3D desplazamiento2 = new Transform3D();
         desplazamiento2.set(new Vector3f(0f, posY, 0));
         TransformGroup TGesferaFija = new TransformGroup(desplazamiento2);
         TGesferaFija.addChild(figuraVisual);
         objRoot.addChild(TGesferaFija);

         // parte f�sica del objeto:
         float masa = 0f;                                                       //con masa =0 el objeto es est�tico
         SphereShape figuraFisica = new SphereShape(radio);
         CollisionObject ramaFisica = new CollisionObject();
         ramaFisica.setCollisionShape(figuraFisica);
         Transform groundTransform = new Transform();
         groundTransform.setIdentity();
         groundTransform.origin.set(new Vector3f(0, posY, 0));
         Vector3f inerciaLocal = new Vector3f(0, 0, 0);
         DefaultMotionState EstadoDeMovimiento = new DefaultMotionState(groundTransform);
         RigidBodyConstructionInfo InformacionCuerpoR = new RigidBodyConstructionInfo(masa, EstadoDeMovimiento, figuraFisica, inerciaLocal);
         RigidBody cuerpoRigido = new RigidBody(InformacionCuerpoR);
         cuerpoRigido.setActivationState(RigidBody.DISABLE_DEACTIVATION);
         mundoFisico.addRigidBody(cuerpoRigido); // add the body to the dynamics world

         //Para crear objeto que se sometan a fisica, su masa debe seo >0 e invocar continuamente
         // mundoFisico.stepSimulation(dt) y actualizar su objeto java3d a partir de su rigidBody.
         //Mejor, usar la clase Figura simulada con el codigo del run(), mostrar() y actualizar()*/
        return objRoot;
    }

    void cargarContenido() {
        //Creando el personaje del juego, controlado por teclado. Tambien se pudo haber creado en CrearEscena()
        float masa = 1f;
        float radio = 1f;
        float posX = 20f;
        float posY = -2f, posZ = 10f;
        float elasticidad = 0.5f;
        float dampingLineal = 0.5f;
        float dampingAngular = 0.9f;
        personaje = new EsferaMDL("objetosMDL/Air_Elemental.mdl", radio, conjunto, listaObjetosFisicos, this, true, false);
        personaje.crearPropiedades(masa, elasticidad, 0.5f, posX, posY, POS_PJ1, mundoFisico);
        personaje.cuerpoRigido.setDamping(dampingLineal, dampingAngular);
        personaje2 = new EsferaMDL("objetosMDL/Air_Elemental.mdl", radio, conjunto, listaObjetosFisicos, this, false, true);
        personaje2.crearPropiedades(masa, elasticidad, 0.5f, posX, posY, POS_PJ2, mundoFisico);
        personaje2.cuerpoRigido.setDamping(dampingLineal, dampingAngular);
        ai = new ControladorIA((EsferaMDL) personaje, (EsferaMDL) personaje2);

        //Creando un Agente (es decir, un personaje aut�nomo) con el objetivo de perseguir al personaje controlado por teclado
        /*perseguidor = new Esfera(radio, "texturas/balon.jpg", conjunto, listaObjetosFisicos, this);
         if (!actualizandoFisicas) {
         perseguidor.crearPropiedades(masa, elasticidad, dampingLineal, 20, 4, -15, mundoFisico);
         }*/
       //perseguidor.asignarObjetivo(personaje,15f);   //Este objetivo de perseguir DEBE actualizado para que persiga la nueva posicion del personaje
        //Barra de vida
        lifeBar1 = new LifeBar(personaje, conjunto, true);
        lifeBar2 = new LifeBar(personaje2, conjunto, false);

        //Creacion de un Terreno Simple (no es una figura, no es movil, tiene masa 0)
        float friccion = 0.5f;
        utilidades.TerrenoSimple terreno = new utilidades.TerrenoSimple(150, 150, 3, -3f, -4, "unaTextura_Desabilitada", conjunto, mundoFisico, friccion);
    }

    void actualizar(float dt) {
        //ACTUALIZAR EL ESTADO DEL JUEGO
        if (estadoJuego == 0) {
            //perseguidor.asignarObjetivo(personaje, 15f);
            if (tiempoJuego > 1000) {
                estadoJuego = 1;
            }
            if (personaje.vida <= 0 || personaje2.vida <= 0) {
                Text2D text = new Text2D(personaje.vida <= 0 ? "Has perdido" : "Has ganado", new Color3f(
                        personaje.vida <= 0 ? Color.red : Color.yellow), "Calibri", 144, Font.BOLD);
                PolygonAttributes att = new PolygonAttributes();
                att.setCullFace(PolygonAttributes.CULL_NONE);
                //att.setBackFaceNormalFlip(true);
                text.getAppearance().setPolygonAttributes(att);
                BranchGroup group = new BranchGroup();
                Transform3D tr = new Transform3D();
                tr.set(new Vector3d(40f, 0f, POS_PJ1 - 0.5));
                tr.setRotation(new AxisAngle4d(0, 1d, 0, Math.toRadians(90)));
                TransformGroup gr = new TransformGroup(tr);
                gr.addChild(text);
                group.addChild(gr);
                conjunto.addChild(group);
                estadoJuego = -1;
            }
        } else if (estadoJuego == 1) {
            //Removiendo las figuras dinamicas. El juego continua 10 segundos mas
            int i = 1;
            while (listaObjetosFisicos.size() > i) {
                listaObjetosFisicos.get(i).remover();      //Elimina a pertir de la i-esima figura
            }
            if (tiempoJuego > 20000) {
                estadoJuego = -1;                                                                    //Con estado del juego -1 el juego termina
                System.out.println("Fin del juego");
            }
        }

        //Actualizar barras de vida
        lifeBar1.actualizar();
        lifeBar2.actualizar();

        ai.comprobarEstado();

        //ACTUALIZAR DATOS DE FUERZAS DEL PERSONAJE CONTROLADO POR EL JUGADOR
        Vector3d direccionFrente2 = personaje2.conseguirDireccionFrontal();
        Vector3d direccionFrente1 = personaje.conseguirDireccionFrontal();
        if (personaje != null) {
            float fuerzaHaciaAdelante = 0, fuerzaVertical = 0;
            boolean disableVelocity = false;
            if (personaje.adelante) {
                fuerzaHaciaAdelante = personaje.masa * 5f;
            }
            if (personaje.atras) {
                fuerzaHaciaAdelante = -personaje.masa * 5f;
            }
            if (personaje.saltar) {
                fuerzaVertical = ((int) personaje.posiciones[1]) == -2 ? 10F : 0F;
            }
            if (!personaje.player2) {
                fuerzaHaciaAdelante = -fuerzaHaciaAdelante;
            }

            if (((int) personaje.posiciones[2]) >= POS_SCENE_L || ((int) personaje.posiciones[2]) <= POS_SCENE_R) {
                personaje.cuerpoRigido.clearForces();
                if (((int) personaje.posiciones[2]) > POS_SCENE_L) {
                    disableVelocity = true;
                    fuerzaHaciaAdelante = 0;
                    personaje.cuerpoRigido.setLinearVelocity(new Vector3f((float) direccionFrente2.x
                            * -10F * 0.1f, 0, (float) direccionFrente2.z * -10F * 0.1f));
                } else if (((int) personaje.posiciones[2]) < POS_SCENE_R) {
                    disableVelocity = true;
                    personaje.cuerpoRigido.setLinearVelocity(new Vector3f((float) direccionFrente2.x
                            * 10F * 0.1f, 0, (float) direccionFrente2.z * 10F * 0.1f));
                    fuerzaHaciaAdelante = 0;
                }
                if (((int) personaje.posiciones[2]) >= POS_SCENE_L && (personaje.player2 ? personaje.adelante : personaje.atras)) {
                    fuerzaHaciaAdelante = 0;
                } else if (((int) personaje.posiciones[2]) <= POS_SCENE_R && (personaje.player2 ? personaje.atras
                        : personaje.adelante)) {
                    fuerzaHaciaAdelante = 0;
                }
            }

            if (personaje.saltar || ((int) personaje.posiciones[1]) != -2) {
                personaje.cuerpoRigido.applyCentralForce(new Vector3f((float) direccionFrente1.x * fuerzaHaciaAdelante * 0.1f, 0, (float) direccionFrente1.z * fuerzaHaciaAdelante * 0.1f));
                personaje.cuerpoRigido.applyCentralImpulse(new Vector3f(0, fuerzaVertical, 0));
            } else {
                if (!disableVelocity) {
                    personaje.cuerpoRigido.setLinearVelocity(new Vector3f((float) direccionFrente1.x * fuerzaHaciaAdelante * 0.1f, 0, (float) direccionFrente1.z * fuerzaHaciaAdelante * 0.1f));
                }
            }
            personaje.cuerpoRigido.applyTorque(new Vector3f(0, 0, 0));
        }
        if (personaje2 != null) {
            float fuerzaHaciaAdelante = 0, fuerzaVertical = 0;
            boolean disableVelocity = false;
            if (personaje2.adelante) {
                fuerzaHaciaAdelante = personaje2.masa * 10f;
            }
            if (personaje2.atras) {
                fuerzaHaciaAdelante = -personaje2.masa * 10f;
            }
            if (personaje2.saltar) {
                fuerzaVertical = ((int) personaje2.posiciones[1]) == -2 ? 10F : 0F;
            }
            if (!personaje2.player2) {
                fuerzaHaciaAdelante = -fuerzaHaciaAdelante;
            }

            if (((int) personaje2.posiciones[2]) >= POS_SCENE_L || ((int) personaje2.posiciones[2]) <= POS_SCENE_R) {
                personaje2.cuerpoRigido.clearForces();
                if (((int) personaje2.posiciones[2]) > POS_SCENE_L) {
                    disableVelocity = true;
                    fuerzaHaciaAdelante = 0;
                    personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0, (float) direccionFrente2.z * -10F * 0.1f));
                } else if (((int) personaje2.posiciones[2]) < POS_SCENE_R) {
                    disableVelocity = true;
                    personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0, (float) direccionFrente2.z * 10F * 0.1f));
                    fuerzaHaciaAdelante = 0;
                }
                if (((int) personaje2.posiciones[2]) >= POS_SCENE_L && (personaje2.player2 ? personaje2.adelante : personaje2.atras)) {
                    fuerzaHaciaAdelante = 0;
                } else if (((int) personaje2.posiciones[2]) <= POS_SCENE_R && (personaje2.player2 ? personaje2.atras
                        : personaje2.adelante)) {
                    fuerzaHaciaAdelante = 0;
                }
            }

            if (personaje2.saltar || ((int) personaje2.posiciones[1]) != -2) {
                personaje2.cuerpoRigido.applyCentralForce(new Vector3f(0, 0, (float) direccionFrente2.z * fuerzaHaciaAdelante * 0.1f));
                personaje2.cuerpoRigido.applyCentralImpulse(new Vector3f(0, fuerzaVertical, 0));
            } else {
                if (!disableVelocity) {
                    personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0, (float) direccionFrente2.z * fuerzaHaciaAdelante * 0.1f));
                }
            }
            personaje2.cuerpoRigido.applyTorque(new Vector3f(0, 0, 0));
        }

        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS  (ej. para que cada figura pueda persiguir su objetivo)
        for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
            listaObjetosFisicos.get(i).actualizar();
        }

        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS FISICAS
        this.actualizandoFisicas = true;
        float ataque = -20f, ataqueFuerte = -40f;
        try {
            mundoFisico.stepSimulation(dt);    //mundoFisico.stepSimulation ( dt  ,50000, dt*0.2f);
            /*int maniFolds = mundoFisico.getDispatcher().getNumManifolds();
             for (int i = 0; i < maniFolds; i++) {
             PersistentManifold fold = mundoFisico.getDispatcher().getManifoldByIndexInternal(i);
             CollisionObject a = (CollisionObject) fold.getBody0(), b = (CollisionObject) fold.getBody1();
             int contacts = fold.getNumContacts();
             for (int j = 0; j < contacts; j++) {
             ManifoldPoint point = fold.getContactPoint(j);
             if (point.getDistance() < 0.f) {
             if (mundoFisico.getCollisionObjectArray()
             .get(personaje.identificadorFisico).equals(a) && mundoFisico.getCollisionObjectArray()
             .get(personaje2.identificadorFisico).equals(b)) {
             if (personaje.ataque) {
             if (!personaje2.parar) personaje2.vida += ataque;
             personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente2.z * ataque * 0.1f));
             } else if (personaje.ataqueFuerte) {
             if (!personaje2.parar) personaje2.vida += ataqueFuerte;
             personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente2.z * ataqueFuerte * 0.1f));
             } else if (personaje2.ataque) {
             if (!personaje.parar) personaje.vida += ataque;
             personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente1.z * -ataque * 0.1f));
             } else if (personaje2.ataqueFuerte) {
             if (!personaje.parar) personaje.vida += ataqueFuerte;
             personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente1.z * -ataqueFuerte * 0.1f));
             }
             } else if (mundoFisico.getCollisionObjectArray()
             .get(personaje.identificadorFisico).equals(b) && mundoFisico.getCollisionObjectArray()
             .get(personaje2.identificadorFisico).equals(a)) {
             if (personaje.ataque) {
             if (!personaje2.parar) personaje2.vida += ataque;
             personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente2.z * ataque * 0.1f));
             } else if (personaje.ataqueFuerte) {
             if (!personaje2.parar) personaje2.vida += ataqueFuerte;
             personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente2.z * ataqueFuerte * 0.1f));
             } else if (personaje2.ataque) {
             if (!personaje.parar) personaje.vida += ataque;
             personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente1.z * -ataque * 0.1f));
             } else if (personaje2.ataqueFuerte) {
             if (!personaje.parar) personaje.vida += ataqueFuerte;
             personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
             (float) direccionFrente1.z * -ataqueFuerte * 0.1f));
             }
             }
             }
             }
             }*/

            float distancia = personaje.posiciones[2] - personaje2.posiciones[2];
            if (distancia <= 2.1f && distancia > 0) {
                if (personaje.ataque) {
                    if (!personaje2.parar) {
                        personaje2.vida += ataque;
                    }
                    personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
                            (float) direccionFrente1.z * -ataque * 0.1f));
                } else if (personaje.ataqueFuerte) {
                    if (!personaje2.parar) {
                        personaje2.vida += ataqueFuerte;
                    }
                    personaje2.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
                            (float) direccionFrente1.z * -ataqueFuerte * 0.1f));
                }
                if (personaje2.ataque) {
                    if (!personaje.parar) {
                        personaje.vida += ataque;
                    }
                    personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
                            (float) direccionFrente1.z * -ataque * 0.1f));
                } else if (personaje2.ataqueFuerte) {
                    if (!personaje.parar) {
                        personaje.vida += ataqueFuerte;
                    }
                    personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0,
                            (float) direccionFrente1.z * -ataqueFuerte * 0.1f));
                }
            }

            mundoFisico.stepSimulation(dt);
            Transform t = new Transform();
            personaje.cuerpoRigido.getWorldTransform(t);
            t.origin.x = 20f;
            personaje.cuerpoRigido.setWorldTransform(t);
            personaje2.cuerpoRigido.getWorldTransform(t);
            t.origin.x = 20f;
            personaje2.cuerpoRigido.setWorldTransform(t);
            mundoFisico.stepSimulation(dt);
        } catch (Exception e) {
            System.out.println("JBullet forzado. No debe crearPropiedades de solidoRigidos durante la actualizacion stepSimulation");
        }
        this.actualizandoFisicas = false;
    }

    void mostrar() throws Exception {
        //MOSTRAR FIGURAS FISICAS (muestra el componente visual de la figura, con base en los datos de localizacion del componente fisico)
        this.mostrandoFisicas = true;
        try {
            if ((mundoFisico.getCollisionObjectArray().size() != 0) && (listaObjetosFisicos.size() != 0)) {
                for (int idFigura = 0; idFigura <= this.listaObjetosFisicos.size() - 1; idFigura++) {     // Actualizar posiciones fisicas y graficas de los objetos.
                    try {
                        int idFisico = listaObjetosFisicos.get(idFigura).identificadorFisico;
                        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(idFisico); //
                        RigidBody cuerpoRigido = RigidBody.upcast(objeto);
                        listaObjetosFisicos.get(idFigura).mostrar(cuerpoRigido);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
        this.mostrandoFisicas = false;
    }

    public void run() {
        cargarContenido();
        float dt = 3f / 100f;
        int tiempoDeEspera = (int) (dt * 1000);
        while (estadoJuego != -1) {
            try {
                actualizar(dt);
            } catch (Exception e) {
                System.out.println("Error durante actualizar. Estado del juego " + estadoJuego);
            }
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < 3000; i += 100) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        universo.removeAllLocales();
        game.dispose();
        PrincipalMenu.main(null);
    }

    void colocarCamara(SimpleUniverse universo, Point3d posicionCamara, Point3d objetivoCamara) {
        posicionCamara = new Point3d(posicionCamara.x + 0.001, posicionCamara.y + 0.001d, posicionCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TransformGroup TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void principal(String[] args) {
        game = new Juego();
        game.setTitle("Juego");
        game.setSize(1000, 800);
        game.setVisible(true);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        float L = 1f;
        game.colocarCamara(game.universo, new Point3d(50f * L, 0f * L, 30f * L), new Point3d(10, 0, 31));
    }
}
