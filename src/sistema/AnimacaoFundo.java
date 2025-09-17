package sistema;

import com.k33ptoo.components.KGradientPanel;
import gui.Tocar;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author Bonn
 */
public class AnimacaoFundo {

    public boolean tocando = false;

    public double foco = 0;

    //primeira vez usando um map :3
    Map<Integer, Runnable> animacoes = new HashMap<>();
    Thread animacaoAtual;
    Tocar tocar;

    int ms = 16;

    public AnimacaoFundo(Tocar tocar) {
        this.tocar = tocar;
        carregarAnimacoes();
    }

    public void carregarAnimacoes() {

        animacoes.put(0, () -> {
            float luz = 0.6f;
            float hue;

            while (tocando) {
                //o segundo argumento faz ter a dormida ser dentro da thread, então não há perigo [há um atraso de
                //um frame por conta desse método, mas acho que essa troca vale a pena]
                if (!podeAnimar(tocar, true)) {
                    continue;
                }

                luz += ((Generico.random(0, 10) - 5f) / 200f);
                hue = 0.06f + ((Generico.random(0, 10) - 5f) / 150f);
                if (luz > 0.7f) {
                    luz = 0.2f;
                } else if (luz < 0.1f) {
                    luz = 0.25f;
                }
                Color ini = new Color(Color.HSBtoRGB(hue, 1f, luz));
                Color fin = new Color(Color.HSBtoRGB(hue + ((Generico.random(0, 10) - 5) / 100), 1f, luz - 0.1f));
                atualizarFundo(tocar.pnlFundo, ini, fin);
            }

        });

        animacoes.put(1, () -> {
            float hue = 0;

            while (tocando) {
                if (!podeAnimar(tocar, true)) {
                    continue;
                }
                hue += 0.0003f * sistema.Info.velocidade;
                Color ini = new Color(Color.HSBtoRGB(hue, 0.9f, 0.9f));
                Color fin = new Color(Color.HSBtoRGB(hue + 0.1f, 0.9f, 0.9f));
                if (hue >= 1) {
                    hue = 0;
                }
                atualizarFundo(tocar.pnlFundo, ini, fin);
            }
        });

        animacoes.put(2, () -> {
            float hue;
            float luz;
            float aux = 0;
            boolean subindoluz = false;

            while (tocando) {
                if (!podeAnimar(tocar, true)) {
                    continue;
                }

                aux += subindoluz ? 0.000010f + Generico.random(0, 20) / 1000f : -(0.000010f + Generico.random(0, 20) / 1000f);

                if (aux > 0.250f) {
                    subindoluz = false;
                } else if (aux < -0.100f) {
                    subindoluz = true;
                }

                luz = 0.7f + (((Generico.random(0, 20)) / 10000f) + aux);
                hue = 0.56f + ((Generico.random(0, 50)) / 10000f);
                Color ini = new Color(Color.HSBtoRGB(hue, 1f, luz));
                Color fin = new Color(Color.HSBtoRGB(hue + ((Generico.random(0, 10)) / 100), 1f, luz - (aux * 2)));

                atualizarFundo(tocar.pnlFundo, ini, fin);
            }
        });

        animacoes.put(3, () -> {
            int countup = 0;
            int ultimo = 0;

            while (tocando) {
                if (!podeAnimar(tocar, true)) {
                    continue;
                }

                countup++;
                Color ini = new Color(0x141516);
                Color fin = new Color(0x1b1c1f);

                boolean deNovo = ((countup - ultimo > 1 && countup - ultimo < 10) && Generico.random(0, 1000) < 200);
                if (Generico.random(0, 1000) / 10f < 0.1f || deNovo) {
                    float valor = (Generico.random(0, 6) + 3) / 10f;
                    if (deNovo == false) {
                        ultimo = countup;
                    }

                    Color cor = Color.getHSBColor(0.5f, 0.1f, valor);
                    ini = cor;
                    fin = cor;
                }

                if (countup > 3000) {
                    countup -= 3000;
                    ultimo -= 3000;
                }
                atualizarFundo(tocar.pnlFundo, ini, fin);
            }
        });

        animacoes.put(4, () -> {

            while (tocando) {
                if (!podeAnimar(tocar, true)) {
                    continue;
                }
                Color ini = new Color(Color.HSBtoRGB(1, 0f, (float) ((foco + 800) / 1600f / 2)));
                Color fin = Color.lightGray;
                atualizarFundo(tocar.pnlFundo, ini, fin);
            }
        });

        animacoes.put(5, () -> {

            int espera = 0;
            byte ciclo = 0;
            byte cicloAnterior = 0;

            while (tocando) {
                if (!podeAnimar(tocar, true) || Tocar.coresUsadas == null) {
                    continue;
                }

                espera -= 1 * sistema.Info.velocidade;
                if (espera <= 0) {
                    espera = 500;
                    ciclo++;
                    cicloAnterior = (byte) (ciclo - 1);

                    if (ciclo >= Tocar.coresUsadas.length) {
                        ciclo = 0;
                    }

                    if (cicloAnterior < 0) {
                        cicloAnterior = (byte) Tocar.coresUsadas.length;
                    }
                }
                double x = sistema.Easings.ease(Math.abs(espera - 500) / 500f, sistema.Easings.EASE_IN_OUT_SINE);
                sistema.Componentes.mudarCorCru(Tocar.coresUsadas[ciclo], Tocar.coresUsadas[cicloAnterior], tocar.pnlFundo, (float) x);
            }
        });

        animacoes.put(6, () -> {
            Color ini = Color.gray;
            Color fin = Color.darkGray;
            atualizarFundo(tocar.pnlFundo, ini, fin);
        });
    }

    private void dormir(long tempo) {
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean podeAnimar(JFrame jframe, boolean eEsperar) {
        boolean pode = (tocando) && jframe.isActive() && jframe.isFocused();

        if (eEsperar) {
            dormir(ms);
            return pode;
        } else {
            return pode;
        }

    }

    public void animarFoco() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Double negocio = 0d;
                    boolean descendo = false;
                    while (tocando) {

                        if (!podeAnimar(tocar, true)) {
                            continue;
                        }

                        negocio -= (descendo ? 0.005d : -0.005d) * sistema.Info.velocidade / 2;

                        foco = (short) (sistema.Easings.easeInOutCirc(negocio) * 800) - 100;

                        if (negocio >= 1) {
                            descendo = true;
                        } else if (negocio <= 0) {
                            descendo = false;
                        }
                        dormir(30);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "ANIMAR FOCO").start();
    }

    public void atualizarFundo(KGradientPanel painel, Color ini, Color fin) {
        painel.setkStartColor(ini);
        painel.setkEndColor(fin);
        painel.setkGradientFocus((int) foco);

        sistema.Componentes.escurecerFundo(painel);

        painel.updateUI();
    }

    public void animarFundo(int animacao) {
        tocando = true;
        animarFoco();

        animacaoAtual = new Thread(new Runnable() {
            @Override
            public void run() {
                animacoes.get(animacao).run();
            }

        });

        animacaoAtual.start();
    }

    public void pararAnimacao() {
        tocando = false;
        if (animacaoAtual != null) {
            animacaoAtual.interrupt();
        }

    }
}
