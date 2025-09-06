/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

//import static gui.Tocar.config;
import gui.Tocar;
import static gui.Tocar.desativados;
import static gui.Tocar.esperaDelay;
//import static gui.Tocar.playlistPreDelay;
import static gui.Tocar.podeAbrir;
//import static gui.Tocar.terminar;
//import static gui.Tocar.volumeGlobal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JOptionPane;

/**
 *
 * @author Bonn
 */
public class GerenciadorDeSom {

    public Som sons[] = new Som[0];
    public Som[] pl = new Som[0];

//    Clip clips[] = new Clip[20];
//    Clip playlists[] = new Clip[20];
//    String clipname[] = new String[20];
    public String setup = "nenhum";
    public String config[] = new String[200];
//    public static boolean terminar = false;
    
    public static boolean tocando = false;
    
    

    boolean tocouPrimeira = false;
    boolean tocandoPlaylist = false;

    public static final byte CARREGADO = -1;
    public static final byte LIVRE = 0;
    public static final byte TOCANDO = 1;
//    public static final byte TOCANDO = 1;

    Random random = new Random();

    private void tocarPlaylist(final File[] arquivos, final String tipo, long preDelay, final boolean temPrimeira) {
        try {

            pl = new Som[arquivos.length];
            for (int i = 0; i < pl.length; i++) {
                pl[i] = new Som();
                pl[i].carregarSom(arquivos[i], arquivos[i].getName());
            }

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        int sel = -1;

                        while (tocando) {

                            if (tipo.equals("a")) {
                                int aleatorio = random.nextInt(arquivos.length);
//                                System.out.println("seria o " + aleatorio);
                                //parece horrivel e é mesmo. isso tudo para evitar selecionar o mesmo som quando for um aleatorio
                                sel = aleatorio == sel? (aleatorio + 1 > arquivos.length - 1? (aleatorio - 1 < 0? aleatorio : aleatorio - 1) : aleatorio + 1) : aleatorio;
//                                System.out.println("mas caiu o " + sel);
                            } else {
                                //se for maior q o limite, volte pra 0, caso contrario, adiciona 1
                                sel += sel + 1 >= arquivos.length ? -arquivos.length + 1 : 1;
                            }

//                            sel = 3;
//                            System.out.println("ta tocando " + pl[sel].nome);
                            pl[sel].darStart(false);

                            //normalmente esse sleep fica no final da thread, mas colocar ele no inicio ou no final
                            //pode acabar dando algum atraso no som, então o melhor lugar para colocar ele é aqui,
                            //logo antes da espera do som acabar para se mesclar com ela                            
                            Thread.sleep(500);

                            //usaria esse thread sleep aqui, só que percebi que com sons mais longos, ele se torna inconsistente
                            //Thread.sleep(Math.max(pl[sel].tamDoSomMS - 1000 - preDelay - sistema.Info.preDelay, 1));
                            
                            while (pl[sel].posDoSomMS() < pl[sel].tamDoSomMS - preDelay - sistema.Info.preDelay) {
                                if ((double) (pl[sel].posDoSomMS()) / (double) (pl[sel].tamDoSomMS) < 0.9d) {
                                    long valor = Math.min(Math.max((pl[sel].tamDoSomMS - pl[sel].posDoSomMS() + 5) / 5, 10), 25000);
//                                    System.out.println("no aguardo longo. vai esperar " + (valor/1000) + "s.");
                                    Thread.sleep(valor);

                                } else {
                                    Thread.sleep(1);
                                }

                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }, "TOCAR").start();

//            playlists = new Clip[arquivos.length];
//            tocouPrimeira = false;
//            for (int i = 0; i < arquivos.length; i++) {
//                AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivos[i]);
//                playlists[i] = AudioSystem.getClip();
//                playlists[i].open(audioStream);
//                audioStream.close();
//            }
//
//            (new Thread(new Runnable() {
//                public void run() {
//                    Clip clipTemp;
//                    FloatControl volume;
//
//                    int pos = 0;
//                    playlists[0].start();
//
//                    if (temPrimeira) {
//                        tocouPrimeira = true;
//                    }
//
//                    Random random = new Random();
//                    tocandoPlaylist = true;
//
//                    while (!terminar) {
//
//                        try {
//
//                            pos = random.nextInt(arquivos.length);
//
//                            if (playlists[pos] != null) {
//                                volume = (FloatControl) playlists[pos].getControl(FloatControl.Type.MASTER_GAIN);
//                                volume.setValue(volumeGlobal);
//
//                                if (playlists[pos].isRunning()) {
         ////                                    System.out.println(playlists[pos].getLevel());
//                                }
//
//                                if (playlists[pos].getMicrosecondPosition() >= playlists[pos].getMicrosecondLength() - TimeUnit.MILLISECONDS.toMicros((sistema.Info.preDelay + playlistPreDelay))) {
//                                    if (pos + 1 >= arquivos.length) {
//
//                                        if (tocouPrimeira) {
//                                            playlists[1].start();
//                                        } else {
//                                            playlists[0].start();
//                                        }
//                                    } else {
//                                        playlists[pos + 1].start();
//                                    }
//                                }
//                                if (playlists[pos].getMicrosecondPosition() >= playlists[pos].getMicrosecondLength()) {
//                                    terminarPlaylist(pos);
//
//                                    if (tipo.equals("a")) {
//
//                                        for (int i = playlists.length - 1; i > 0; i--) {
//
//                                            int index;
//
//                                            if (temPrimeira) {
//                                                index = random.nextInt(i) + 1;
//                                            } else {
//                                                index = random.nextInt(i + 1);
//                                            }
//
//                                            clipTemp = playlists[index];
//
//                                            playlists[index] = playlists[i];
//                                            if (temPrimeira && i > 1) {
//                                                playlists[i] = clipTemp;
//                                            } else if (temPrimeira == false) {
//                                                playlists[i] = clipTemp;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            Thread.sleep(esperaDelay);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                    tocandoPlaylist = false;
//                    Thread.currentThread().interrupt();
//                }
//            }, "TocarPlaylist")).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        this.tocandoPlaylist = false;
    }

    public void rodar(String setup) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setPriority(7);
                    Arrays.fill(config, null);
//                    setTitle("Ambientes - " + setup);
//                    lblStatus.setText("Carregando...");

                    File lugar = new File("Arquivos/setups/" + setup + "/mestre.txt");
//                    FileReader fr = new FileReader(lugar);
//                    BufferedReader ler = new BufferedReader(fr);
//                    String linha = ler.readLine();

                    String linha = Files.readString(lugar.toPath()).replaceAll(sistema.Info.FILTRO, "");
                    config = linha.replace("\r", "").split("\n"); //tirar aquele caractere do mal

//                    int pos = 0;
//                    while (linha != null) {
//                        config[pos] = linha;
//                        linha = ler.readLine();
//                        pos++;
//                    }
//                    for (int i = 0; i < config.length; i++) {
//                        System.out.println(config[i]);
//                    }
                    //configurar playlist [fazer em um lugar separado para economizar recursos]
                    for (int i = 0; i < config.length; i++) {
                        if (config[i].startsWith("p - ")) {

                            String l[] = config[i].split(" - ");
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

                                sons[j] = "Arquivos/setups/" + setup + "/" + sons[j] + ".wav";
                                coisos[j] = new File(sons[j]);
                            }
                            tocarPlaylist(coisos, l[2], playlistPreDelay, temPrimeira);
                            break;
                        }
                    }

                    //configurar sons
                    //acaba que o tamanho pode ser um pouco maior que o necessário, ver sobre isso depois
                    sons = new Som[config.length];

                    for (int i = 0; i < config.length; i++) {
                        if (config[i].startsWith("#") || config[i].startsWith("p - ")) {
//                            t--;
                            continue;
                        }

                        String l[] = config[i].split(", ");
                        if (l.length != 3) {
                            continue;
                        }

                        String nome = l[0];

                        File arquivo = new File("Arquivos/setups/" + setup + "/" + nome + ".wav");
                        sons[i] = new Som();
                        sons[i].carregarSom(arquivo, nome, Tocar.volumeGlobal);
                    }

//                    System.out.println(Arrays.toString(sons));
//                    lblStatus.setText("Tocando agora: " + setup);
                    //loop principal de tocar o setup
                    tocando = true;
                    while (tocando) {
//                        jButton4.setEnabled(podeAbrir);

//                        Random random = new Random();
                        for (int i = 0; i < config.length; i++) {

                            if (config[i] == null) {
                                tocando = false;
                                break;
                            } else if (config[i].startsWith("#") || config[i].startsWith("p - ")) {
                                continue;
                            }

//                            if (config[i] != null) {
//                            if (config[i].startsWith("p - ")) {
//                                    if (tocandoPlaylist == false) {
//
                            ////                                        if (avisoPlaylist == false) {
////                                            JOptionPane.showConfirmDialog(null, "Esse setup que você selecionou utiliza o sistema de playlist para\nfuncionar. Se você ouvir um silêncio/sobreposição de uma música\nou áudio para outro, mude o predelay nas configurações.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
////                                            avisoPlaylist = true;
////                                        }
//
//                                       
//                                    }
//                                } else {

                                    String nome = config[i].split(", ")[0];

                            if (config[i].split(", ").length != 3) {
                                continue;
                            }

                            int repetir = Integer.parseInt(config[i].split(", ")[2]);
                            float porcentagemA = Float.parseFloat(config[i].split(", ")[1]);

                            float porcentagemC = random.nextInt(1000000) / 10000f; //gerando um int mt grande para dividir por um numero grande e formar um float

                            if (porcentagemC <= porcentagemA) {
//                                for (int j = 0; j < sons.length; j++) {
//                                    if (sons[j] != null && sons[j].nome.equals(nome)) {
//                                        achou = true;
//                                        break;
//                                    }
//                                }

//                                if () {
//                                    
//                                }
//                                System.out.println(encontrarSomPeloNome(nome).info);
                                if (encontrarSomPeloNome(nome).info != GerenciadorDeSom.TOCANDO && !estaDesativado(nome)) {
                                    System.out.println("tocou " + nome);
//                                    System.out.println(Arrays.toString(sons));

//                                    for (int j = 0; j < sons.length; j++) {
//                                        if (sons[j] != null) {
//                                            System.out.print(sons[j].nome + ", ");
//                                        }
//
//                                    }
//                                        tocar("Arquivos/setups/" + setup + "/" + nome + ".wav", posLivre(), 0, new Date(), repetir, nome);
//                                    tocar(new File("Arquivos/setups/" + setup + "/" + nome + ".wav"), posLivre(), 0, repetir, nome);
                                    Som som = encontrarSomPeloNome(nome);
                                    som.darStart(false);
                                    som.configurarLoops(repetir);
                                }
//                                }
                            }
//                            }
                        }

                        Thread.sleep(sistema.Info.atualizacao);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showConfirmDialog(null, ex.toString(), "Erro", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    tocando = false;;
//                    tocarParar();
                } finally {
//                    setTitle("Ambientes " + sistema.Info.VERSAO_ATUAL);
//                    lblStatus.setText("Parado...");
                    Thread.currentThread().interrupt();
                    tocando = false;
                }
            }
        }, "Rodar").start();
    }

    public boolean estaDesativado(String nome) {
        for (int i = 0; i < desativados.length; i++) {
            if (nome.equals(desativados[i])) {
                return true;
            }
        }
        return false;
    }

    public byte posLivre() {
        for (byte i = 0; i < sons.length; i++) {
            if (sons[i] == null || sons[i].info == GerenciadorDeSom.LIVRE) {
                return i;
            }
        }
        return -1;
    }

//    public void tocar(File arquivo, int pos, long salto, int repetir, String nome) {
////        Date data = new Date();
//
//        if (!arquivo.exists()) {
//            return;
//        }
//
////        if (new File(arquivo).exists()) {
//        try {
//
////                AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo);
////                long pular = ((data.getTime() - momento.getTime()));
//            sons[pos] = new Som();
//            sons[pos].carregarSomCompleto(arquivo, nome, gui.Tocar.volumeGlobal);
//            sons[pos].darStart(true);
//            sons[pos].configurarLoops(repetir);
////                clips[pos] = AudioSystem.getClip();
////                clips[pos].open(audioStream);
////                clipinfo[pos] = "tocando";
////                clipname[pos] = nome;
//
////                audioStream.close();
////                FloatControl volume = (FloatControl) clips[pos].getControl(FloatControl.Type.MASTER_GAIN);
////                new Thread(new Runnable() {
////                    @Override
////                    
////                    public void run() {
//         ////                        clips[pos].setMicrosecondPosition(pular * 1000);
////                        
//////                        volume.setValue(volumeGlobal);
////                        
//////                        sons[pos].clip.start();
////                        sons[pos].darStart();
////                        sons[pos].configurarLoops(repetir);
////                        
//////                        if (repetir == -1) {
//////                            sons[pos].clip.loop(Clip.LOOP_CONTINUOUSLY);
//////                        }
////                        
////                        try {
////                            while (sons[pos].clip != null && sons[pos].clip.getMicrosecondPosition() <= sons[pos].clip.getMicrosecondLength() - 10000) {
////                                Thread.sleep(esperaDelay);
////                            }
////                            
////                            if (repetir >= 0) {
////                                parar(pos);
////                                Thread.currentThread().interrupt();
////                            }
////                            
////                        } catch (Exception ex) {
////                            ex.printStackTrace();
////                        }
////                        
////                    }
////                }, "Tocar").start();
//                
//            } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
////        }
//    }

//    public void parar(int pos) {
//        try {
//            clipname[pos] = "";
//            if (sons[pos].clip != null) {
//                sons[pos].clip.stop();
//                sons[pos].clip.flush();
//                sons[pos].clip.close();
//            }
//            sons[pos].clip = null;
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    public void terminarPlaylist(int pos) {
//        playlists[pos].stop();
//        playlists[pos].setMicrosecondPosition(0);
//    }
//    public void pararPlaylist(int pos) {
//
//        try {
//            playlists[pos].stop();
//            playlists[pos].flush();
//            playlists[pos].close();
//            playlists[pos] = null;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
    public Som encontrarSomPeloNome(String nome) {
        for (int i = 0; i < sons.length; i++) {
            if (sons[i] != null && sons[i].nome.equals(nome)) {
                return sons[i];
            }
        }

        return null;
    }

    public void pararTudo() {
        System.out.println("tudo parado");
        tocando = false; 
       try {
            for (int i = 0; i < sons.length; i++) {
                if (sons[i] != null) {
//                    parar(i);
                    sons[i].finalizar();
                }
            }

            for (int i = 0; i < pl.length; i++) {
                if (pl[i] != null) {
                    pl[i].finalizar();
                }
            }
//            setarIcones(false);
            tocandoPlaylist = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void setarVolune(float volume) {
       try {
            for (int i = 0; i < sons.length; i++) {
                if (sons[i] != null) {
//                    parar(i);
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
