package sistema;

import static gui.Tocar.desativados;
import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import javax.swing.JOptionPane;

/**
 *
 * @author Bonn
 */
public class GerenciadorDeSom {

    public Som sons[] = new Som[0];
    public Som[] pl = new Som[0];

    public String setup = "Nenhum";
    public String config[];
    
    //cores
    public boolean temCores = false;
    public byte fundoPredefinido = -1; //-1: não usa fundo predefinido
    public Color cores[] = null;
    public float corTempo = 0f;

    public boolean tocando = false;
    public long playlistPredelay = -1;

    public static final byte MORTO = -2; //tocou e foi finalizado
    public static final byte CARREGADO = -1; //carregado e nunca tocou
    public static final byte LIVRE = 0; //carregado e tocou
    public static final byte TOCANDO = 1; //tocando

    private void tocarPlaylist(final File[] arquivos, final String tipo, long preDelay, boolean temPrimeira) {
        try {

            playlistPredelay = preDelay;

            pl = new Som[arquivos.length];
            for (int i = 0; i < pl.length; i++) {
                pl[i] = new Som();
                pl[i].carregarSom(arquivos[i], arquivos[i].getName(), Info.volumeGlobal);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        int sel = -1;
                        boolean tocouPrimeira = false;
                        boolean cortouPrimeira = false;
                        while (tocando) {
                            
                            if (tipo.equals("a")) {
                                int aleatorio = Generico.random(tocouPrimeira ? 1 : 0, pl.length);
                                //parece horrivel e é mesmo. isso tudo para evitar selecionar o mesmo som quando for um aleatorio
                                sel = aleatorio == sel ? (aleatorio + 1 > pl.length - 1 ? (aleatorio - 1 < 0 ? aleatorio : aleatorio - 1) : aleatorio + 1) : aleatorio;
                            } else {
                                sel += sel + 1 >= pl.length ? -pl.length + (temPrimeira ? 2 : 1) : 1;
                            }

                            if (tocouPrimeira == false && temPrimeira) {
                                tocouPrimeira = true;
                                sel = 0;
                            }

                            pl[sel].darStart(false);

                            //normalmente esse sleep fica no final da thread, mas colocar ele no inicio ou no final
                            //pode acabar dando algum atraso no som, então o melhor lugar para colocar ele é aqui,
                            //logo antes da espera do som acabar para se mesclar com ela                            
                            Thread.sleep(500);

                            //usaria esse thread sleep aqui, só que percebi que com sons mais longos, ele se torna inconsistente
                            //Thread.sleep(Math.max(pl[sel].tamDoSomMS - 1000 - preDelay - sistema.Info.preDelay, 1));

                            
                            while (pl[sel].posDoSomMS() < pl[sel].tamDoSomMS - preDelay - sistema.Info.preDelay) {
                                if ((double) (pl[sel].posDoSomMS()) / (double) (pl[sel].tamDoSomMS) < 0.9d) {
                                    //pequeno calculo que faz o sleep ser menor quanto mais perto o som está de acabar
                                    long valor = Math.min(Math.max((pl[sel].tamDoSomMS - pl[sel].posDoSomMS() + 5) / 5, 10), 25000);
                                    Thread.sleep(valor);

                                } else {
                                    Thread.sleep(1);
                                }

                            }
                            
                            if (tocouPrimeira && cortouPrimeira == false) {
                                //thread para finalizar o audio que nao sera mais tocado, ja que
                                //finalizar no meio da execucao pode causar um atraso no audio
                                cortouPrimeira = true;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pl[0].finalizar();
                                    }
                                }, "FINALIZAR SOM").start();
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }, "TOCAR").start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean carregarERodarSetup(String setup) throws Exception {

        File lugar = new File(sistema.Info.localSetups + File.separator + setup + "/mestre.txt");

        String linha = Files.readString(lugar.toPath()).replaceAll(sistema.Info.FILTRO, "");
        config = linha.replace("\r", "").split("\n"); //tirar aquele caractere do mal
        tocando = true;
        //configurar cores, se houver
        for (String c : config) {
            if (c.startsWith("c - ")) {
                temCores = true;
                String[] l = c.split(" - ");
                //pegar cada cor que está separado por ,
                if (l[1].split(", ") != null) {
                    cores = sistema.Generico.converterCor(l[1]);
                } else {
                    fundoPredefinido = Byte.parseByte(l[1]);
                }
                
                corTempo = Float.parseFloat(l[2]);
                break;
            }
        }
        
        //configurar playlist
        for (String c : config) {
            if (c.startsWith("p - ")) {
                String[] l = c.split(" - ");
                //pegar cada som que está separado por ,
                String sons[] = l[1].split(", ");
                //criar um array de arquivos que tem todos os locais dos sons
                File coisos[] = new File[sons.length];
                long playlistPreDelay = Integer.parseInt(l[3]);
                //pesquisa para saber se a playlist tem um som que tocará primeiro
                boolean temPrimeira = false;
                for (int j = 0; j < sons.length; j++) {

                    if (j == 0 && sons[j].startsWith("i: ")) {
                        temPrimeira = true;
                        sons[j] = sons[j].substring(3); //se tiver, tirar o "i: " do nome
                    }

                    sons[j] = sistema.Info.localSetups + File.separator + setup + "/" + sons[j] + ".wav";
                    coisos[j] = new File(sons[j]);
                }
                tocarPlaylist(coisos, l[2], playlistPreDelay, temPrimeira);
                break;
            }
        }

        //carregar sons
        //acaba que o tamanho pode ser um pouco maior que o necessário, ver sobre isso depois
        sons = new Som[config.length];

        for (int i = 0; i < config.length; i++) {
            if (config[i].startsWith("#") || config[i].startsWith("p - ") || config[i].startsWith("c - ")) {
                continue;
            }

            String l[] = config[i].split(", ");
            if (l.length != 3) {
                continue;
            }

            String nome = l[0];

            File arquivo = new File(sistema.Info.localSetups + File.separator + setup + "/" + nome + ".wav");
            sons[i] = new Som();
            sons[i].carregarSom(arquivo, nome, Info.volumeGlobal);
        }
        this.setup = setup;
        rodar();
        return true;
    }

    public void rodar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setPriority(7);
                    //loop principal de tocar o setup
                    while (tocando) {
                        for (int i = 0; i < config.length; i++) {

                            if (config[i] == null) {
                                tocando = false;
                                break;
                            } else if (config[i].startsWith("#") || config[i].startsWith("p - ")) {
                                continue;
                            }

                            String nome = config[i].split(", ")[0];

                            if (config[i].split(", ").length != 3) {
                                continue;
                            }

                            int repetir = Integer.parseInt(config[i].split(", ")[2]);
                            float porcentagemA = Float.parseFloat(config[i].split(", ")[1]);

                            float porcentagemC = Generico.random(0, 1000000) / 10000f; //gerando um int mt grande para dividir por um numero grande e formar um float

                            if (porcentagemC <= porcentagemA) {

                                if (encontrarSomPeloNome(nome).info != GerenciadorDeSom.TOCANDO && !estaDesativado(nome)) {
                                    Som som = encontrarSomPeloNome(nome);
                                    som.darStart(false);
                                    som.configurarLoops(repetir);
                                }
                            }
                        }

                        Thread.sleep(sistema.Info.atualizacao);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showConfirmDialog(null, ex.toString(), "Erro", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    tocando = false;
                } finally {
                    Thread.currentThread().interrupt();
                    tocando = false;
                }
            }
        }, "Rodar").start();
    }

    public boolean estaDesativado(String nome) {
        
        return desativados.contains(nome);
        
//        if (desativados.contains(nome)) {
//            return true;
//        } else {
////            return fals
//        }
        
//        for (int i = 0; i < desativados.length; i++) {
//            if (nome.equals(desativados[i])) {

    }

    public byte posLivre() {
        for (byte i = 0; i < sons.length; i++) {
            if (sons[i] == null || sons[i].info == GerenciadorDeSom.LIVRE) {
                return i;
            }
        }
        return -1;
    }

    public Som encontrarSomPeloNome(String nome) {
        for (int i = 0; i < sons.length; i++) {
            if (sons[i] != null && sons[i].nome.equals(nome)) {
                return sons[i];
            }
        }

        return null;
    }

    public void pararTudo() {
        tocando = false;
        try {
            for (int i = 0; i < sons.length; i++) {
                if (sons[i] != null) {
                    sons[i].finalizar();
                }
            }

            for (int i = 0; i < pl.length; i++) {
                if (pl[i] != null) {
                    pl[i].finalizar();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setarVolune(float volume) {
        try {
            for (int i = 0; i < sons.length; i++) {
                if (sons[i] != null) {
                    sons[i].setarVolume(volume);
                }
            }

            for (int i = 0; i < pl.length; i++) {
                if (pl[i] != null) {
                    pl[i].setarVolume(volume);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
