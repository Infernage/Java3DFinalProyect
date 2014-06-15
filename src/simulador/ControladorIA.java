/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

import figuras.EsferaMDL;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Fco Javier
 */
public class ControladorIA {

    Figura jugador;
    EsferaMDL bot;

    public ControladorIA(EsferaMDL jugador_, EsferaMDL bot_) {
        jugador = jugador_;
        bot = bot_;
    }

    public void comprobarEstado() {
        float distancia;
        distancia = jugador.posiciones[2] - bot.posiciones[2];
        if (distancia > 1f) {
            bot.adelante = true;
        }

        if (distancia < 1f) {
            bot.adelante = false;
            if (jugador.ataque && (!bot.ataque && !bot.ataqueFuerte)) {
                bot.parar = true;
            } else if ((jugador.ataqueFuerte || jugador.saltar) && (!bot.ataque && !bot.ataqueFuerte)) {
                bot.atras = true;
            } else if (jugador.parar && (!bot.ataque && !bot.ataqueFuerte)) {
                bot.ataqueFuerte = true;
            } else if (!bot.ataque && !bot.ataqueFuerte) {
                bot.ataque = true;
            }
        }

        actualizarBot();
    }

    public void actualizarBot() {
        float fuerzaHaciaAdelante = 0;
        if (bot.adelante) {
            fuerzaHaciaAdelante = -bot.masa * 10f;
            
        }
        if (bot.atras) {
            fuerzaHaciaAdelante = bot.masa * 10f;
        }
        Vector3d direccionFrente = bot.conseguirDireccionFrontal();
        bot.ab.playAnimation(bot.nombreAnimacionCaminando, false);
        bot.cuerpoRigido.setLinearVelocity(new Vector3f((float) direccionFrente.x * fuerzaHaciaAdelante * 0.1f, 0, (float) direccionFrente.z * fuerzaHaciaAdelante * 0.1f));
        
        if(bot.ataque){
            bot.adelante = false;
            bot.atras = false;
            bot.ab.playAnimation(bot.nombreAnimacionLuchando, false);
        }
        
        if(bot.ataqueFuerte){
            bot.adelante = false;
            bot.atras = false;
            bot.ab.playAnimation(bot.nombreAnimacionLuchandoFuerte, false);
        }
        
        if(bot.parar){
            bot.adelante = false;
            bot.atras = false;
            bot.ab.playAnimation(bot.nombreAnimacionParando, false);
        }
    }
}
