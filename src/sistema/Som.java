/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineListener;

/**
 *
 * @author Mateus
 */
public class Som {

    public String nome = "";
    public byte info = GerenciadorDeSom.LIVRE;
    public Clip clip = null;
    public boolean iniciou = false;

    public boolean finalizarSomAoTerminar = false;

    public long tamDoSomMS = 0;

    LineListener lili;

    //talvez seja melhor mover a criacao do clip para o metodo carregarSom
    public Som() {
        try {
            clip = AudioSystem.getClip();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void carregarSom(File arquivo, String nome) throws Exception {
        carregarSom(arquivo, nome, -3);
    }

    public long posDoSomMS() {
        if (clip != null) {
            return clip.getMicrosecondPosition() / 1000;
        } else {
            //talvez tenha acabado, entao o clip ficou null
            if (iniciou) {
                return tamDoSomMS;
            } else {
                return 0;
            }

        }
    }

    public boolean carregarSom(File arquivo, String nome, float volume) throws Exception {
        try {

            if (clip == null) {
                clip = AudioSystem.getClip();
            } else if (clip.isOpen()) {
                return false;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo);
            clip.open(audioStream);
            audioStream.close();

            this.nome = nome;
            info = GerenciadorDeSom.CARREGADO;
            FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            vol.setValue(volume);
            tamDoSomMS = clip.getMicrosecondLength() / 1000;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

//    public void parar() {
//        try {
//            nome = "";
//            if (clip != null) {
//                clip.stop();
//                clip.flush();
//                clip.close();
//            }
//            clip = null;
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public boolean darStart(boolean finalizarSomAoTerminar) {
        try {
            if (clip == null) {
                return false;
            }

            clip.setMicrosecondPosition(0);

            //em raras ocasioes [quando o audio acaba e ja é tocado de novo no mesmo instante], mesmo dando .start,
            //ele nao toca. entao esse while é necessario para ter certeza que o audio vai tocar
            int tentativas = 0;
            do {
                clip.start();
                tentativas++;
                Thread.sleep(5);
                if (tentativas > 400) { //dois segundos tentando
                    return false;
                }
            } while (clip.getMicrosecondPosition() == 0);

            System.out.println(tentativas + ", " + (tentativas * 5) + "ms");
            
             if (iniciou == false) {
                lili = (e) -> {
                    if (clip.getMicrosecondPosition() >= clip.getMicrosecondLength()) {
                        info = GerenciadorDeSom.LIVRE;
                        if (finalizarSomAoTerminar) {
                            finalizar();
                        } else {
                            clip.setMicrosecondPosition(0);
                        }
                        
                    }
                };

                clip.addLineListener(lili);
            }
            
            iniciou = true;

            info = GerenciadorDeSom.TOCANDO;

           
            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public void finalizar() {

        nome = "";

        if (clip != null) {
            clip.stop();
            if (lili != null) {
                clip.removeLineListener(lili);
            }
            clip.flush();
            clip.drain();
            clip.close();
        }
        clip = null;

    }

    public void configurarLoops(int repetir) {
        if (repetir == -1) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.loop(repetir);
        }
    }

    public void setarVolume(float volume) {
        
        if (clip == null) {
            return;
        }
        
        FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        vol.setValue(volume);
    }

}
