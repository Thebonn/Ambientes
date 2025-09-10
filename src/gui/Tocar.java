package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import sistema.GerenciadorDeSom;
import sistema.Info;
import static sistema.Rpc.presence;

/**
 *
 * @author Bonn
 */
public final class Tocar extends javax.swing.JFrame {

    public static List<String> desativados = new ArrayList<>();
    
    public static boolean podeAbrir = true;

    float contagem = 10;

    public byte setupsInstalados = 0;

    public static Color cores[] = new Color[20];
    public static String coresLegiveis = "";
    public static byte coresEspSel = 0;
    public static float mudancaVel = 0.002f;

    public static int esperaDelay = 5;

    boolean avisoFechar = false;
    boolean play = true;
    public static boolean lojaaberta = false;

    public static boolean suportaSystemTray = false;
    TrayIcon trayIcon;

    public Thread animThread = new Thread();
    Configuracoes configs;
    public static GerenciadorDeSom gerenciadorDeSom;

    public Tocar(boolean subir) {
        FlatDarkLaf.install();
        initComponents();
        this.setTitle("Ambientes " + sistema.Info.VERSAO_ATUAL);

        animar();
        adicionarSetups();
        gerenciadorDeSom = new GerenciadorDeSom();
        sldVolume.setValue((int) Info.volumeGlobal);

        this.setIconImage(new ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo 2.png")).getImage());

        contagemInatividade();
        bandeja();
        cores = sistema.Generico.converterCor("ABDEE6, CBAACB, FFFFB5, FFCCB6, F3B0C3");

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

        if (subir) {
            subir();
        }

    }

    private class BotaoNumericoAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            escolherCor();
        }
    }

    public void subir() {
        setLocation(getLocation().x, getLocation().y + 130);
        sistema.Componentes.moverJanela(this, this.getLocation().x, this.getLocation().y - 130, 0.008, sistema.Easings.EASE_OUT_QUART);
    }

    void atualizacaoBaixaFrequencia() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                try {
                    while (true) {
                        atualizarRpc();
                        Thread.sleep(10000);
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

            trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo.png")).getImage().getScaledInstance(15, 15, BufferedImage.SCALE_SMOOTH));
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
        File pasta = new File(sistema.Info.localSetups);
        String setups[] = pasta.list();
        cbbSetups.removeAllItems();
        
        if (setups != null) {

            for (int i = 0; i < setups.length; i++) {
                cbbSetups.addItem(setups[i]);
            }
            setupsInstalados = (byte) setups.length;
        }

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

                    while (true) {

                        if ((play == false) && isActive() && isFocused()) {

                            if (descendo) {
                                negocio -= 0.001d * sistema.Info.velocidade / 2;
                            } else {
                                negocio += 0.001d * sistema.Info.velocidade / 2;
                            }
                            foco = (short) (sistema.Easings.easeInOutCirc(negocio) * 700);

                            if (negocio >= 1) {
                                descendo = true;
                            } else if (negocio <= 0) {
                                descendo = false;
                            }

                            switch (sistema.Info.animTipo) {
                                case 0:
                                    luz += ((r.nextInt(10) - 5f) / 200f);
                                    hue = 0.06f + ((r.nextInt(10) - 5f) / 150f);
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
                                    if (r.nextInt(1000) / 10f < 0.1f || deNovo) {
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

                                        sistema.Componentes.mudarCor(cores[ciclo], cores[cicloAnterior], pnlFundo, (float) (mudancaVel * sistema.Info.velocidade), 15);
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

                                sistema.Componentes.escurecerFundo(pnlFundo);

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

    public void setarIcones(boolean tocando) {
        URL botaoURL;
        URL logoURL = getClass().getResource("/recursos/imagens/ambientes logo 2.png");

        if (tocando) {
            botaoURL = getClass().getResource("/recursos/imagens/pausegp.png");
            logoURL = getClass().getResource("/recursos/imagens/ambientes logo.png");
        } else {
            botaoURL = getClass().getResource("/recursos/imagens/playgp.png");
        }

        btnTocar.setIcon(new ImageIcon(botaoURL));
        if (Info.iconeInterativo) {
            this.setIconImage(new ImageIcon(logoURL).getImage());
        }

    }

    public void tocarParar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (play) {
                        play = false;
                        btnTocar.setEnabled(false);
                        lblStatus.setText("Carregando...");
                        boolean res = gerenciadorDeSom.carregarERodarSetup(cbbSetups.getSelectedItem().toString());
                        btnTocar.setEnabled(true);
                        if (res) {
                            setTitle("Ambientes - " + gerenciadorDeSom.setup);
                            lblStatus.setText("Tocando agora: " + gerenciadorDeSom.setup);
                            btnOpcoes.setEnabled(podeAbrir);
                            setarIcones(!play);
                        }

                    } else {
                        play = true;
                        setarIcones(!play);
                        sistema.Componentes.ratio = 1;
                        gerenciadorDeSom.pararTudo();
                        setTitle("Ambientes " + sistema.Info.VERSAO_ATUAL);
                        lblStatus.setText("Parado...");
                        sistema.Componentes.mudarCor(Color.darkGray, pnlFundo.getkStartColor(), pnlFundo, 0.04f, 10);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "tocar parar").start();
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
            Color cor[] = sistema.Generico.escolherCor();
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
        sldVolume = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        btnTocar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        btnOpcoes = new javax.swing.JButton();
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

        cbbSetups.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
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

        jLabel1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Setup:");

        sldVolume.setMajorTickSpacing(100);
        sldVolume.setMinorTickSpacing(5);
        sldVolume.setToolTipText("");
        sldVolume.setValue(90);
        sldVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldVolumeStateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Volume");

        btnTocar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/playgp.png"))); // NOI18N
        btnTocar.setBorderPainted(false);
        btnTocar.setIconTextGap(0);
        btnTocar.setMaximumSize(new java.awt.Dimension(30, 30));
        btnTocar.setOpaque(true);
        btnTocar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTocarActionPerformed(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Parado...");

        jButton3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jButton3.setText("Configurações");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnOpcoes.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        btnOpcoes.setText("Opções");
        btnOpcoes.setEnabled(false);
        btnOpcoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpcoesActionPerformed(evt);
            }
        });

        lblVolume.setFont(new java.awt.Font("Open Sauce One", 1, 18)); // NOI18N
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
                    .addComponent(sldVolume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cbbSetups, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpcoes))
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
                    .addComponent(btnOpcoes))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVolume)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
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

    private void sldVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldVolumeStateChanged
        if (contagem < 1) {
            contagem = 2;
        }

        Info.volumeGlobal = sldVolume.getValue();
        lblVolume.setText(sldVolume.getValue() + "%");
//        setarVolume(volumeGlobal);
        gerenciadorDeSom.setarVolune(Info.volumeGlobal);
    }//GEN-LAST:event_sldVolumeStateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        abrirConfigs();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnOpcoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpcoesActionPerformed
        if (podeAbrir) {
            podeAbrir = false;
            new Opcoes().setVisible(true);
        }

    }//GEN-LAST:event_btnOpcoesActionPerformed

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
                new Tocar(true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOpcoes;
    private javax.swing.JButton btnTocar;
    private javax.swing.JComboBox<String> cbbSetups;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVolume;
    private javax.swing.JProgressBar pgbSumir;
    private com.k33ptoo.components.KGradientPanel pnlFundo;
    private javax.swing.JSlider sldVolume;
    // End of variables declaration//GEN-END:variables
}
