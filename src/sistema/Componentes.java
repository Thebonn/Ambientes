package sistema;

import com.k33ptoo.components.KGradientPanel;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Bonn
 */
public class Componentes {

    public static void moverJanela(JFrame janela, int x, int y, double acrescimo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int xoriginal = janela.getLocation().x;
                int yoriginal = janela.getLocation().y;
                try {
                    for (double animacao = 0; animacao < 1; animacao += acrescimo) {
                        int xlocal = (int) ((xoriginal * (Math.abs(Easings.easeOutQuart(animacao) - 1))) + (Easings.easeOutQuart(animacao) * x));
                        int ylocal = (int) ((yoriginal * (Math.abs(Easings.easeOutQuart(animacao) - 1))) + (Easings.easeOutQuart(animacao) * y));
                        janela.setLocation(xlocal, ylocal);
                        animacao += acrescimo;
                        Thread.sleep(8);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "MOVER JANELA").start();

    }

    public static void mudartexto(String texto, JLabel componente, Font fonte) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String caracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!;.,";
                    Random random = new Random();
                    String saida;
                    Font fonte = new Font("Open Sauce One Black", Font.BOLD, Generico.calcularTamanho(40, texto));
                    fonte = fonte.deriveFont(Generico.calcularTamanho(40, texto));
                    componente.setFont(fonte);
                    for (int i = 0; i < 5; i++) {
                        saida = "";
                        for (int j = 0; j < texto.length() / 1.5; j++) {
                            saida += caracteres.split("")[random.nextInt(caracteres.length())];
                        }
                        componente.setText(saida);
                        Thread.sleep(40);
                    }
                    componente.setText(texto);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "MUDATEXTO").start();
    }

    public static double ratio = 0;

    public static void acender(Color corInicial, Color corFinal, KGradientPanel painel, float quantidade, int tempo) {
//        generico.acenderThread = new Thread(new Runnable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Color inicio = corInicial;
                Color fim = corFinal;
                painel.setkStartColor(inicio);
                painel.setkEndColor(sistema.Generico.processarCor(inicio, Info.intensidade));
                try {
                    boolean acabou = false;
                    while (acabou == false) {
                        int red = (int) Math.abs((ratio * inicio.getRed()) + ((1 - ratio) * fim.getRed()));
                        int green = (int) Math.abs((ratio * inicio.getGreen()) + ((1 - ratio) * fim.getGreen()));
                        int blue = (int) Math.abs((ratio * inicio.getBlue()) + ((1 - ratio) * fim.getBlue()));
                        Color ultimo = new Color(red, green, blue);
                        painel.setkStartColor(ultimo);
                        painel.setkEndColor(sistema.Generico.processarCor(ultimo, Info.intensidade));
                        escurecerFundo(painel);
                        painel.updateUI();
                        Thread.sleep(tempo);
                        ratio += quantidade;
                        if (ratio >= 1) {
                            acabou = true;
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Thread.currentThread().interrupt();
            }
//        }, "TAD");
        }, "TAD").start();
//        generico.acenderThread.start();
    }

    public static void escurecerFundo(KGradientPanel painel) {
        Color cor1escurecido = painel.getkStartColor();
        Color cor2escurecido = painel.getkEndColor();
        for (int i = 0; i < Info.escurecerFundo / 9; i++) {
            cor1escurecido = cor1escurecido.darker();
            cor2escurecido = cor2escurecido.darker();
        }
        painel.setkStartColor(cor1escurecido);
        painel.setkEndColor(cor2escurecido);
    }

}
