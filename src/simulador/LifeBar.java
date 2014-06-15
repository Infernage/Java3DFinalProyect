/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

import figuras.EsferaMDL;
import java.awt.Color;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 *
 * @author D
 */
public class LifeBar {

    Figura personaje;
    QuadArray lifeBar;
    float posz = 47;
    float posx = 10;
    float posy = 10;

    float width = 10;
    float vidaAnterior;
    boolean player;

    /**
     *
     * @param personaje_ Personaje del cual se mostrará la vida
     *
     * @param branchGroup_ BranchGroup al que introducir la barra
     * @param player_ Si es jugador 1 (true) o jugador 2 (false)
     */
    public LifeBar(Figura personaje_, BranchGroup branchGroup_, boolean player_) {
        this.personaje = personaje_;
        this.player = player_;
        this.vidaAnterior = personaje.vida;
        if (!player) {
            posz -= 32;

        }
        if (player) {
            lifeBar = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
            lifeBar.setCoordinate(0, new Point3f(posx, posy, posz - width));
            lifeBar.setCoordinate(1, new Point3f(posx, posy + 1, posz - width));
            lifeBar.setCoordinate(2, new Point3f(posx, posy + 1, posz));
            lifeBar.setCoordinate(3, new Point3f(posx, posy, posz));

            lifeBar.setColor(0, new Color3f(Color.green));
            lifeBar.setColor(1, new Color3f(Color.green));
            lifeBar.setColor(2, new Color3f(Color.red));
            lifeBar.setColor(3, new Color3f(Color.red));

        } else {
            lifeBar = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
            lifeBar.setCoordinate(0, new Point3f(posx, posy, posz));
            lifeBar.setCoordinate(1, new Point3f(posx, posy + 1, posz));
            lifeBar.setCoordinate(2, new Point3f(posx, posy + 1, posz + width));
            lifeBar.setCoordinate(3, new Point3f(posx, posy, posz + width));

            lifeBar.setColor(0, new Color3f(Color.red));
            lifeBar.setColor(1, new Color3f(Color.red));
            lifeBar.setColor(2, new Color3f(Color.green));
            lifeBar.setColor(3, new Color3f(Color.green));

        }
        Shape3D lifeBarShape = new Shape3D(lifeBar);
        lifeBar.setCapability(QuadArray.ALLOW_COORDINATE_WRITE);
        lifeBar.setCapability(QuadArray.ALLOW_COORDINATE_WRITE);
        lifeBarShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        lifeBarShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        BranchGroup lifeBarBG = new BranchGroup();
        lifeBarBG.addChild(lifeBarShape);

        branchGroup_.addChild(lifeBarBG);

    }

    public void actualizar() {
        float porcentaje;

        if (personaje.vida > 0 && vidaAnterior != personaje.vida) {
            if (player) {
                porcentaje = personaje.vida / 100;
                lifeBar.setCoordinate(0, new Point3f(posx, posy, posz - (width * porcentaje)));
                lifeBar.setCoordinate(1, new Point3f(posx, posy + 1, posz - (width * porcentaje)));

            } else {
                porcentaje = personaje.vida / 100;
                lifeBar.setCoordinate(2, new Point3f(posx, posy + 1, posz + (width * porcentaje)));
                lifeBar.setCoordinate(3, new Point3f(posx, posy, posz + (width * porcentaje)));

            }

            vidaAnterior = personaje.vida;
        }
    }

}
