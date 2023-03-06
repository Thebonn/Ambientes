/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

/**
 *
 * @author Thebonn
 */
public class Rpc {

    public static DiscordRPC lib;
    public static final String APPLICATION_ID = "990002259469406288";
    public static DiscordEventHandlers handlers;
    public static DiscordRichPresence presence;

    public static void atualizarRPC() {
        lib.Discord_UpdatePresence(presence);
    }

    public static void pararRPC() {
        lib.Discord_Shutdown();
        lib.Discord_ClearPresence();
    }
    
    public static void atualizarDetalhes(String detalhes) {
        presence.details = detalhes;
        lib.Discord_UpdatePresence(presence);
    }
    
    public static void atualizarEstado(String estado) {
        presence.state = estado;
        lib.Discord_UpdatePresence(presence);
    }

    public static void atualizarTextos(String detalhes, String estado) {
        presence.details = detalhes;
        presence.state = estado;
        lib.Discord_UpdatePresence(presence);
    }

    public static void atualizarImagens(String chavePequeno, String chaveGrande, String textoPequeno, String textoGrande) {
        presence.smallImageKey = chavePequeno;
        presence.largeImageKey = chaveGrande;
        presence.smallImageText = textoPequeno;
        presence.largeImageText = textoGrande;

        lib.Discord_UpdatePresence(presence);
    }

    public static void iniciarRPC() {
        lib = DiscordRPC.INSTANCE;

        String steamId = "";
        handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(APPLICATION_ID, handlers, true, steamId);
        presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Parado";
        presence.largeImageKey = "logo";
        presence.largeImageText = "Programa por Thebonn!";

        lib.Discord_UpdatePresence(presence);
        // in a worker thread

        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(2);
                while (!Thread.currentThread().isInterrupted()) {
                    lib.Discord_RunCallbacks();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }, "RPC-Callback-Handler").start();
    }
}
