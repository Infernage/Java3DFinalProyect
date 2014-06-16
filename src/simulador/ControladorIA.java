/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

import figuras.EsferaMDL;
import java.util.Random;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Fco Javier
 */
public class ControladorIA {

    Figura jugador;
    EsferaMDL bot;
    private int timer = 0;
    Random rnd = new Random();

    public ControladorIA(EsferaMDL jugador_, EsferaMDL bot_) {
        jugador = jugador_;
        bot = bot_;
    }

    public void comprobarEstado() {
        float distancia;
        distancia = jugador.posiciones[2] - bot.posiciones[2];
        if (!bot.atras || distancia >= 6f) {
            if (distancia > 2.1f) {
                bot.adelante = true;
                bot.parar = bot.ataque = bot.atras = bot.saltar = bot.ataqueFuerte = false;
            } else if (distancia < 2.1f && distancia > 0 && timer == 0) {
                bot.adelante = false;
                if (bot.saltar) {
                    bot.atras = bot.saltar = false;
                }
                if (jugador.ataque && (!bot.ataque && !bot.ataqueFuerte)) {
                    bot.parar = true;
                } else if ((jugador.ataqueFuerte || jugador.saltar) && (!bot.ataque && !bot.ataqueFuerte)) {
                    bot.atras = true;
                } else if (jugador.parar && (!bot.ataque && !bot.ataqueFuerte)) {
                    bot.ataqueFuerte = true;
                    bot.adelante = false;
                    bot.atras = false;
                    bot.saltar = false;
                    timer = 30;
                } else if (!bot.ataque && !bot.ataqueFuerte) {
                    if (rnd.nextBoolean()) {
                        bot.ataque = true;
                    } else {
                        bot.ataqueFuerte = true;
                    }
                    bot.adelante = false;
                    bot.atras = false;
                    bot.saltar = false;
                    timer = 30;
                }
            } else if (timer == 30) {
                if (bot.ataque) {
                    bot.ataque = false;
                } else if (bot.ataqueFuerte) {
                    bot.ataqueFuerte = false;
                }
                timer--;
            } else if (timer <= 30 && timer > 0) {
                timer--;
            } else if (distancia < -2.1f) {
                bot.atras = true;
                bot.parar = bot.saltar = bot.ataque = bot.adelante = bot.ataqueFuerte = false;
            }
        } else if (bot.atras && distancia > -2.1f && distancia < 0) {
            bot.saltar = true;
        } else if (distancia < -2.1f) {
            bot.atras = true;
            bot.parar = bot.saltar = bot.ataque = bot.adelante = bot.ataqueFuerte = false;
        } else if (distancia > 0 && bot.saltar) {
            bot.saltar = false;
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
        if (bot.saltar || bot.posiciones[1] != -2) {
            bot.cuerpoRigido.applyCentralForce(new Vector3f(0, 0, (float) direccionFrente.z * fuerzaHaciaAdelante * 0.1f));
            bot.cuerpoRigido.applyCentralImpulse(new Vector3f(0, ((int) bot.posiciones[1]) == -2 ? 5F : 0F, 0));
        } else {
            bot.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0, (float) direccionFrente.z * fuerzaHaciaAdelante * 0.1f));
        }

        if (bot.ataque) {
            bot.adelante = false;
            bot.atras = false;
            bot.ab.playAnimation(bot.nombreAnimacionLuchando, false);
        }

        if (bot.ataqueFuerte) {
            bot.adelante = false;
            bot.atras = false;
            bot.ab.playAnimation(bot.nombreAnimacionLuchandoFuerte, false);
        }

        if (bot.parar) {
            bot.adelante = false;
            bot.atras = false;
            bot.ab.playAnimation(bot.nombreAnimacionParando, false);
        }
    }
}
