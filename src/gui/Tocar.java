package gui;

import sistema.Configs;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;
import javax.sound.sampled.FloatControl;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.io.InputStream;
import sistema.Info;
import static sistema.Rpc.presence;

/**
 *
 * @author Thebonn
 */
public final class Tocar extends javax.swing.JFrame {

    /**
     * Creates new form Tocar
     */
    Clip clips[] = new Clip[20];
    static Clip playlists[] = new Clip[20];
    String clipname[] = new String[20];
    public static String setup = "nenhum";
    public static String config[] = new String[200];
    public static boolean terminar = false;
    public static byte volumeGlobal = -3;

    public static String desativados[] = new String[50];
    public static boolean podeAbrir = true;

    float contagem = 10;

    int selecionadoAnter = 0;

    public byte setupsInstalados = 0;

    public static Color cores[] = new Color[20];
    public static String coresLegiveis = "";
    public static byte coresEspSel = 0;
    public static float mudancaVel = 0.002f;

    boolean tocandoPlaylist = false;
    boolean tocouPrimeira = false;
    public static int playlistPreDelay = 0;
    public static int esperaDelay = 5;

    boolean avisoPlaylist = false;
    boolean avisoFechar = false;
    boolean play = true;
    public static boolean lojaaberta = false;

    boolean suportaSystemTray = false;
    TrayIcon trayIcon;

    public Thread animThread = new Thread();

    sistema.Sistema sist;
    Configuracoes configs;

    public Tocar() {
        FlatDarkLaf.install();
        initComponents();
        this.setTitle("Ambientes " + sistema.Info.VERSAO_ATUAL);
        Configs configs = new Configs();
        configs.carregar();
        Arrays.fill(clipname, "");
        animar();
        adicionarSetups();

        URL iconURL = getClass().getResource("/imagens/ambientes logo 2.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());

        contagemInatividade();
        bandeja();
        sist = new sistema.Sistema();
        cores = sist.converterCor("ABDEE6, CBAACB, FFFFB5, FFCCB6, F3B0C3");

        escolherCor();
        this.requestFocus();

        pnlFundo.requestFocus();
        //meu deus isso é mt complexo, aprendi isso hoje

        ActionMap actionMap = pnlFundo.getActionMap();
        BotaoNumericoAction ngc = new BotaoNumericoAction();
        actionMap.put("mudar", ngc);
        pnlFundo.setActionMap(actionMap);

        InputMap imap = pnlFundo.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        imap.put(KeyStroke.getKeyStroke(' '), "mudar");

        sistema.Rpc.iniciarRPC();
        atualizacaoBaixaFrequencia();

    }

    private class BotaoNumericoAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            escolherCor();
        }
    }

    public void subir() {
        setLocation(getLocation().x, getLocation().y + 130);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    float velocidade = 10;

                    for (int i = 0; i < 35; i++) {

                        setLocation(getLocation().x, (int) (getLocation().y - velocidade));
                        velocidade = velocidade * 0.9f;
                        Thread.sleep(10);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "SubirJFrame").start();
    }

    void atualizacaoBaixaFrequencia() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                int iter = 0;
                try {
                    while (true) {
                        iter++;

                        if (iter == 10) {
                            System.gc();
                            iter = 0;
                        }
                        atualizarRpc();
                        Thread.sleep(5000);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "ATTBaixaFrequencia").start();
    }

    public void bandeja() {
        if (!SystemTray.isSupported()) {
            System.out.println("Sem suporte para SystemTray");
            return;
        }

        try {
            suportaSystemTray = true;

            final PopupMenu popup = new PopupMenu();

            InputStream is = getClass().getResource("/imagens/ambientes logo.png").openStream();
            ImageIcon icon = new ImageIcon(ImageIO.read(is).getScaledInstance(15, 15, BufferedImage.SCALE_SMOOTH));
            is.close();

            trayIcon = new TrayIcon(icon.getImage());
            final SystemTray tray = SystemTray.getSystemTray();

            MenuItem abrirItem = new MenuItem("Abrir");
            MenuItem sairItem = new MenuItem("Fechar");
            MenuItem tocarpararItem = new MenuItem("Tocar/Parar");
            MenuItem configurarItem = new MenuItem("Configurar");

            popup.add(abrirItem);
            popup.add(tocarpararItem);
            popup.add(configurarItem);
            popup.addSeparator();
            popup.add(sairItem);

            trayIcon.setPopupMenu(popup);

            sairItem.addActionListener((e) -> {
                System.exit(0);
            });
            abrirItem.addActionListener((e) -> {
                this.setVisible(true);
            });
            tocarpararItem.addActionListener((e) -> {
                tocarParar();
            });

            configurarItem.addActionListener((e) -> {
                abrirConfigs();
            });

            tray.add(trayIcon);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void atualizarRpc() {

        if (sistema.Info.rpcTipo == 2) {
            sistema.Rpc.atualizarDetalhes("Usando o Ambientes");
            return;
        }

        if (sistema.Info.mostrarSetups) {
            sistema.Rpc.atualizarEstado(setupsInstalados + " setups instalados");
        } else {
            sistema.Rpc.atualizarEstado("");
        }

        if (lojaaberta) {
            sistema.Rpc.atualizarDetalhes("Na loja de setups");
            return;
        }

        if (!play) {
            presence.largeImageKey = "logo";
            if (sistema.Info.rpcTipo == 0) {
                if (cbbSetups.getSelectedItem().equals("Obter mais setups")) {
                    adicionarSetups();
                }
                sistema.Rpc.atualizarDetalhes("Ouvindo " + cbbSetups.getSelectedItem());
            } else if (sistema.Info.rpcTipo == 1) {
                sistema.Rpc.atualizarDetalhes("Ouvindo um setup");
            }
        } else {
            sistema.Rpc.atualizarDetalhes("Parado");
            presence.largeImageKey = "logoparado";
        }
    }

    public void adicionarSetups() {
        String atual = cbbSetups.getSelectedItem().toString();
        File pasta = new File("Arquivos/setups/");
        String setups[] = pasta.list();
        cbbSetups.removeAllItems();
        for (int i = 0; i < setups.length; i++) {
            cbbSetups.addItem(setups[i]);
        }

        setupsInstalados = (byte) setups.length;
        cbbSetups.setSelectedItem(atual);
        cbbSetups.addItem("Obter mais setups");

    }

    public void contagemInatividade() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (sistema.Info.podeColorir) {

                            pgbSumir.setValue((int) (contagem));
                            pgbSumir.setMaximum(sistema.Info.maximo);
                            if (contagem <= 0) {
                                jPanel1.setVisible(false);
                            } else {
                                jPanel1.setVisible(true);
                            }
                        } else {
                            contagem = 0;
                            jPanel1.setVisible(true);
                        }

                        contagem -= 0.1;
                        Thread.sleep(100);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "ContagemInatividade").start();
    }

    public void rodar(String setup) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setPriority(7);
                    Arrays.fill(config, null);
                    setTitle("Ambientes - " + setup);
                    lblStatus.setText("Carregando...");
                    File lugar = new File("Arquivos/setups/" + setup + "/mestre.txt");
                    FileReader fr = new FileReader(lugar);
                    BufferedReader ler = new BufferedReader(fr);
                    String linha = ler.readLine();

                    int pos = 0;

                    while (linha != null) {
                        config[pos] = linha;
                        linha = ler.readLine();
                        pos++;
                    }

                    fr.close();
                    ler.close();

                    lblStatus.setText("Tocando agora: " + setup);

                    while (terminar == false) {
                        jButton4.setEnabled(podeAbrir);

                        Random random = new Random();
                        for (int i = 0; i < config.length; i++) {
                            if (config[i] != null) {

                                if (config[i].startsWith("p - ")) {
                                    if (tocandoPlaylist == false) {

                                        if (avisoPlaylist == false) {
                                            JOptionPane.showConfirmDialog(null, "Esse setup que você selecionou utiliza o sistema de playlist para\nfuncionar. Se você ouvir um silêncio/sobreposição de uma música\nou áudio para outro, mude o predelay nas configurações.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                                            avisoPlaylist = true;
                                        }

                                        String l[] = config[i].split(" - ");
                                        String musicas[] = l[1].split(", ");
                                        File coisos[] = new File[musicas.length];
                                        playlistPreDelay = Integer.parseInt(l[3]);
                                        boolean temPrimeira = false;
                                        for (int j = 0; j < musicas.length; j++) {

                                            if (j == 0 && musicas[j].startsWith("i:")) {
                                                temPrimeira = true;
                                                musicas[j] = musicas[j].substring(2);
                                            }

                                            musicas[j] = "Arquivos/setups/" + setup + "/" + musicas[j] + ".wav";
                                            coisos[j] = new File(musicas[j]);
                                        }
                                        tocarPlaylist(coisos, l[2], temPrimeira);
                                    }
                                } else {

                                    String nome = config[i].split(", ")[0];

                                    int repetir = Integer.parseInt(config[i].split(", ")[2]);
                                    float porcentagemA = Float.parseFloat(config[i].split(", ")[1]);

                                    boolean achou = false;

                                    float porcentagemC = random.nextInt(1000000) / 10000f; //gerando um int mt grande para dividir por um numero grande e formar um float

                                    if (porcentagemC <= porcentagemA) {
                                        for (int j = 0; j < clipname.length; j++) {
                                            if (clipname[j].equals(nome)) {
                                                achou = true;
                                                break;
                                            }
                                        }
                                        if (achou == false && !estaDesativado(nome)) {
                                            tocar("Arquivos/setups/" + setup + "/" + nome + ".wav", posLivre(), 0, new Date(), repetir, nome);
                                        }
                                    }
                                }
                            }
                        }

                        Thread.sleep(sistema.Info.atualizacao);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showConfirmDialog(null, ex.toString(), "Erro", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    terminar = true;
                    tocarParar();
                } finally {
                    setTitle("Ambientes " + sistema.Info.VERSAO_ATUAL);
                    lblStatus.setText("Parado...");
                    Thread.currentThread().interrupt();
                    terminar = false;
                }
            }
        }, "Rodar").start();
    }

    public boolean tocando() {
        for (int i = 0; i < playlists.length; i++) {
            if (playlists[i] != null) {
                return true;
            }
        }
        return false;
    }

    public boolean estaDesativado(String nome) {
        for (int i = 0; i < desativados.length; i++) {
            if (nome.equals(desativados[i])) {
                return true;
            }
        }
        return false;
    }

    public void animar() {

        animThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //rgb
                    Thread.currentThread().setPriority(2);
                    float hue = 0;
                    Color ini = Color.black;
                    Color fin = Color.black;
                    int tempo = 10;
                    Random r = new Random();

                    //raios
                    short ultimo = 0;
                    short countup = 0;

                    //fogo
                    float luz = 0.6f;

                    //foco do gradiente
                    short foco;
                    boolean descendo = false;

                    //cores especiais
                    byte ciclo = 1;
                    byte cicloAnterior;
                    short espera = 10;

                    //para easing
                    double negocio = 0.0d;

                    sistema.Easings easings = new sistema.Easings();

                    while (true) {

                        if ((play == false) && isActive() && isFocused()) {

                            if (descendo) {
                                negocio -= 0.001d * sistema.Info.velocidade / 2;
                            } else {
                                negocio += 0.001d * sistema.Info.velocidade / 2;
                            }
                            foco = (short) (easings.easeInOutCirc(negocio) * 700);

                            if (negocio >= 1) {
                                descendo = true;
                            } else if (negocio <= 0) {
                                descendo = false;
                            }

                            switch (sistema.Info.animTipo) {
                                case 0:
                                    luz += ((r.nextInt(10) - 5f) / 200f);
                                    hue = 0.06f + ((r.nextInt(10) - 5f) / 150f);
//                                    System.out.println(luz);
                                    if (luz > 0.7f) {
                                        luz = 0.2f;
                                    } else if (luz < 0.1f) {
                                        luz = 0.25f;
                                    }
                                    ini = new Color(Color.HSBtoRGB(hue, 1f, luz));
                                    fin = new Color(Color.HSBtoRGB(hue + ((r.nextInt(10) - 5) / 100), 1f, luz - 0.1f));
                                    break;
                                case 1:
//                                    tempo = 10;
                                    hue += 0.0003f * sistema.Info.velocidade;
                                    ini = new Color(Color.HSBtoRGB(hue, 0.9f, 0.9f));
                                    fin = new Color(Color.HSBtoRGB(hue + 0.1f, 0.9f, 0.9f));
                                    if (hue >= 1) {
                                        hue = 0;
                                    }
                                    break;
                                case 2:

                                    luz = 0.8f + ((r.nextInt(2) - 1f) / 700f);
                                    hue = 0.56f + ((r.nextInt(10) - 5f) / 650f);
                                    ini = new Color(Color.HSBtoRGB(hue, 1f, luz));
                                    fin = new Color(Color.HSBtoRGB(hue + ((r.nextInt(10) - 5) / 100), 1f, luz - 0.1f));

                                    break;

                                case 3:
                                    countup++;
                                    ini = new Color(0x141516);
                                    fin = new Color(0x1b1c1f);

                                    boolean deNovo = ((countup - ultimo > 1 && countup - ultimo < 10) && r.nextInt(1000) < 200);
                                    if (r.nextInt(1000) / 10f < 0.3f || deNovo) {
                                        float valor = (r.nextInt(6) + 3) / 10f;
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

                                    break;
                                case 4:
                                    ini = new Color(Color.HSBtoRGB(1, 0f, (float) ((foco + 700) / 1400f / 2)));
                                    fin = Color.lightGray;
                                    break;
                                case 5:
                                    espera -= 1 * sistema.Info.velocidade;
                                    if (espera <= 0) {
                                        espera = 500;
                                        ciclo++;
                                        cicloAnterior = (byte) (ciclo - 1);

                                        while (cores[ciclo] == null) {
                                            ciclo++;
                                            if (ciclo >= cores.length) {
                                                ciclo = 0;
                                                break;
                                            }
                                        }

                                        if (ciclo >= cores.length) {
                                            ciclo = 0;
                                        }

                                        if (cicloAnterior < 0) {
                                            cicloAnterior = (byte) cores.length;
                                        }

                                        sist.acender(cores[ciclo], cores[cicloAnterior], pnlFundo, (float) (mudancaVel * sistema.Info.velocidade), 15);
                                    }
                                    break;
                                case 6:
                                    ini = Color.gray;
                                    fin = Color.darkGray;
                                    break;
                                default:
                                    break;
                            }

                            if (sistema.Info.animTipo != 5) {
                                pnlFundo.setkStartColor(ini);
                                pnlFundo.setkEndColor(fin);

                                sist.escurecerFundo(pnlFundo);

                            }
                            pnlFundo.setkGradientFocus(foco);
                            pnlFundo.updateUI();

                            Thread.sleep(tempo);

                        }

                        Thread.sleep(10);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        animThread.setPriority(1);
        animThread.start();
    }

    public byte posLivre() {
        for (byte i = 0; i < clips.length; i++) {
            if (clips[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void tocar(String arquivo, int pos, long salto, Date momento, int repetir, String nome) {
        Date data = new Date();
        if (new File(arquivo).exists()) {

            try {

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(arquivo));

                long pular = ((data.getTime() - momento.getTime()));

                clips[pos] = AudioSystem.getClip();
                clips[pos].open(audioStream);
//                clipinfo[pos] = "tocando";
                clipname[pos] = nome;

                audioStream.close();
                FloatControl volume = (FloatControl) clips[pos].getControl(FloatControl.Type.MASTER_GAIN);

                new Thread(new Runnable() {
                    @Override

                    public void run() {
                        clips[pos].setMicrosecondPosition(pular * 1000);

                        volume.setValue(volumeGlobal);

                        clips[pos].start();

                        if (repetir == -1) {
                            clips[pos].loop(Clip.LOOP_CONTINUOUSLY);
                        }

                        try {
                            while (clips[pos] != null && clips[pos].getMicrosecondPosition() <= clips[pos].getMicrosecondLength() - 10000) {
                                Thread.sleep(esperaDelay);
                            }

                            if (repetir >= 0) {
                                parar(pos);
                                Thread.currentThread().interrupt();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }, "Tocar").start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void tocarPlaylist(final File[] arquivos, final String tipo, final boolean temPrimeira) {
        try {
            playlists = new Clip[arquivos.length];
            tocouPrimeira = false;
            for (int i = 0; i < arquivos.length; i++) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivos[i]);
                playlists[i] = AudioSystem.getClip();
                playlists[i].open(audioStream);
                audioStream.close();
            }

            (new Thread(new Runnable() {
                public void run() {
                    Clip clipTemp;
                    FloatControl volume;

                    int pos = 0;
                    playlists[0].start();

                    if (temPrimeira) {
                        tocouPrimeira = true;
                    }

                    Random random = new Random();
                    tocandoPlaylist = true;

                    while (!terminar) {

                        try {

                            pos = random.nextInt(arquivos.length);

                            if (playlists[pos] != null) {
                                volume = (FloatControl) playlists[pos].getControl(FloatControl.Type.MASTER_GAIN);
                                volume.setValue(volumeGlobal);

                                if (playlists[pos].isRunning()) {
                                    System.out.println(playlists[pos].getLevel());
                                }

                                if (playlists[pos].getMicrosecondPosition() >= playlists[pos].getMicrosecondLength() - TimeUnit.MILLISECONDS.toMicros((sistema.Info.preDelay + playlistPreDelay))) {
                                    if (pos + 1 >= arquivos.length) {

                                        if (tocouPrimeira) {
                                            playlists[1].start();
                                        } else {
                                            playlists[0].start();
                                        }
                                    } else {
                                        playlists[pos + 1].start();
                                    }
                                }
                                if (playlists[pos].getMicrosecondPosition() >= playlists[pos].getMicrosecondLength()) {
                                    terminarPlaylist(pos);

                                    if (tipo.equals("a")) {

                                        for (int i = playlists.length - 1; i > 0; i--) {

                                            int index;

                                            if (temPrimeira) {
                                                index = random.nextInt(i) + 1;
                                            } else {
                                                index = random.nextInt(i + 1);
                                            }

                                            clipTemp = playlists[index];

                                            playlists[index] = playlists[i];
                                            if (temPrimeira && i > 1) {
                                                playlists[i] = clipTemp;
                                            } else if (temPrimeira == false) {
                                                playlists[i] = clipTemp;
                                            }

                                        }

                                    }

                                }
                            }
                            Thread.sleep(esperaDelay);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    tocandoPlaylist = false;
                    Thread.currentThread().interrupt();
                }
            }, "TocarPlaylist")).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        this.tocandoPlaylist = false;
    }

    public void parar(int pos) {
        try {
            clipname[pos] = "";
            if (clips[pos] != null) {
                clips[pos].stop();
                clips[pos].flush();
                clips[pos].close();
            }
            clips[pos] = null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void terminarPlaylist(int pos) {
        playlists[pos].stop();
        playlists[pos].setMicrosecondPosition(0);
    }

    public void pararPlaylist(int pos) {

        try {
            playlists[pos].stop();
            playlists[pos].flush();
            playlists[pos].close();
            playlists[pos] = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void pararTudo() {
        try {
            for (int i = 0; i < clips.length; i++) {
                if (clips[i] != null) {
                    parar(i);
                }
            }

            for (int i = 0; i < playlists.length; i++) {
                if (playlists[i] != null) {
                    pararPlaylist(i);
                }
            }
            setarIcones(false);
            tocandoPlaylist = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setarIcones(boolean tocando) {
        URL botaoURL;
        URL logoURL = getClass().getResource("/imagens/ambientes logo 2.png");

        if (tocando) {
            botaoURL = getClass().getResource("/imagens/pausegp.png");
            logoURL = getClass().getResource("/imagens/ambientes logo.png");
        } else {
            botaoURL = getClass().getResource("/imagens/playgp.png");
        }

        btnTocar.setIcon(new ImageIcon(botaoURL));
        if (Info.iconeInterativo) {
            this.setIconImage(new ImageIcon(logoURL).getImage());
        }

    }

    public void esperarAcabar(boolean parar) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                try {
                    btnTocar.setEnabled(false);
                    if (parar) {
                        while (!lblStatus.getText().equals("Parado...")) {
                            Thread.sleep(100);
                        }
                    } else {
                        Thread.sleep(1000);
                    }

                    btnTocar.setEnabled(true);
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "EsperarAcabar").start();
    }

    public void tocarParar() {
        if (play) {
            play = false;
            rodar(cbbSetups.getSelectedItem().toString());
            setarIcones(!play);
        } else {
            play = true;

            setarIcones(!play);

            terminar = true;
            sist.ratio = 1;
            pararTudo();
            sist.acender(Color.darkGray, pnlFundo.getkStartColor(), pnlFundo, 0.04f, 10);
            System.gc();
        }
        esperarAcabar(play);
    }

    public void setarVolume(int quantidade) {
        try {
            for (int i = 0; i < clips.length; i++) {
                if (clips[i] != null) {
                    FloatControl volume = (FloatControl) clips[i].getControl(FloatControl.Type.MASTER_GAIN);
                    volume.setValue(quantidade);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void abrirConfigs() {
        if (configs == null || !configs.isVisible()) {
            jButton3.setText("Abrindo...");
            jButton3.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    configs = new Configuracoes();
                    configs.setVisible(true);

                    jButton3.setText("Configurações");
                    jButton3.setEnabled(true);
                }
            }, "AbrirConfigs").start();
        } else {
            jButton3.setText("Já tá aberto!!");
        }
    }

    void escolherCor() {
        if (play == true) {
            Color cor[] = sist.escolherCor();
            pnlFundo.setkStartColor(cor[0]);
            pnlFundo.setkEndColor(cor[1]);
            pnlFundo.updateUI();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFundo = new com.k33ptoo.components.KGradientPanel();
        jPanel1 = new javax.swing.JPanel();
        cbbSetups = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        btnTocar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        lblVolume = new javax.swing.JLabel();
        pgbSumir = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlFundo.setkBorderRadius(0);
        pnlFundo.setkEndColor(new java.awt.Color(106, 142, 179));
        pnlFundo.setkGradientFocus(0);
        pnlFundo.setkStartColor(new java.awt.Color(49, 32, 53));
        pnlFundo.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                pnlFundoMouseMoved(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 1, true));
        jPanel1.setPreferredSize(new java.awt.Dimension(280, 280));
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel1MouseMoved(evt);
            }
        });

        cbbSetups.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Modificado", "quando", "executado" }));
        cbbSetups.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cbbSetupsMouseEntered(evt);
            }
        });
        cbbSetups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbSetupsActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Setup:");

        jSlider1.setMajorTickSpacing(100);
        jSlider1.setMinorTickSpacing(5);
        jSlider1.setToolTipText("");
        jSlider1.setValue(90);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Volume");

        btnTocar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/playgp.png"))); // NOI18N
        btnTocar.setBorderPainted(false);
        btnTocar.setIconTextGap(0);
        btnTocar.setMaximumSize(new java.awt.Dimension(30, 30));
        btnTocar.setOpaque(true);
        btnTocar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTocarActionPerformed(evt);
            }
        });

        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Parado...");

        jButton3.setText("Configurações");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Opções");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        lblVolume.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVolume.setText("90%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cbbSetups, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4))
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnTocar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblVolume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pgbSumir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbbSetups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVolume)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(btnTocar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(pgbSumir, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnlFundoLayout = new javax.swing.GroupLayout(pnlFundo);
        pnlFundo.setLayout(pnlFundoLayout);
        pnlFundoLayout.setHorizontalGroup(
            pnlFundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFundoLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jPanel1, 278, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        pnlFundoLayout.setVerticalGroup(
            pnlFundoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFundoLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jPanel1, 274, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFundo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTocarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTocarActionPerformed
        if (cbbSetups.getSelectedIndex() + 1 == cbbSetups.getItemCount()) {
            cbbSetups.setSelectedIndex(0);
        }

        tocarParar();
    }//GEN-LAST:event_btnTocarActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        if (contagem < 1) {
            contagem = 2;
        }

        volumeGlobal = (byte) ((jSlider1.getValue() * 86 / 100) - 80);
        lblVolume.setText(jSlider1.getValue() + "%");
        setarVolume(volumeGlobal);
    }//GEN-LAST:event_jSlider1StateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        abrirConfigs();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (podeAbrir) {
            podeAbrir = false;
            new Opcoes().setVisible(true);
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void pnlFundoMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFundoMouseMoved
        if (contagem < 1) {
            contagem = 2;
        }
    }//GEN-LAST:event_pnlFundoMouseMoved

    private void jPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseMoved
        contagem = sistema.Info.maximo + 1;
    }//GEN-LAST:event_jPanel1MouseMoved

    private void cbbSetupsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbbSetupsMouseEntered
        adicionarSetups();
    }//GEN-LAST:event_cbbSetupsMouseEntered

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (suportaSystemTray) {
            if (avisoFechar == false) {
                avisoFechar = true;
                trayIcon.displayMessage("Ambientes", "O programa ainda está rodando! Caso queria fechá-lo totalmente, vá nos seus ícones de bandeja, clique com o botão direito no ícone do Ambientes e clique em fechar.", TrayIcon.MessageType.INFO);
            }
            this.setVisible(false);
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void cbbSetupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbSetupsActionPerformed
        if (cbbSetups.getSelectedItem() != null && cbbSetups.getSelectedItem().equals("Obter mais setups") && lojaaberta == false) {
            lojaaberta = true;
            new Loja().setVisible(true);
        }
    }//GEN-LAST:event_cbbSetupsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tocar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tocar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tocar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tocar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tocar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTocar;
    private javax.swing.JComboBox<String> cbbSetups;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVolume;
    private javax.swing.JProgressBar pgbSumir;
    private com.k33ptoo.components.KGradientPanel pnlFundo;
    // End of variables declaration//GEN-END:variables
}
