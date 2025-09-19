package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.k33ptoo.components.KButton;
import com.k33ptoo.components.KGradientPanel;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import sistema.especiais.SetupLoja;

/**
 *
 * @author Bonn
 */
public class Loja extends javax.swing.JFrame {

    JLabel id[];
    JLabel au[];
    JLabel ti[];
    JLabel ta[];
    JLabel im[];
    JTextArea de[];
    JPanel pa[];
    KGradientPanel fu[];

    public Loja() {
        FlatDarkLaf.install();
        initComponents();

        this.id = new JLabel[]{id1, id2, id3};
        this.au = new JLabel[]{autor1, autor2, autor3};
        this.ti = new JLabel[]{titulo1, titulo2, titulo3};
        this.ta = new JLabel[]{tamanho1, tamanho2, tamanho3};
        this.im = new JLabel[]{img1, img2, img3};
        this.de = new JTextArea[]{descricao1, descricao2, descricao3};
        this.pa = new JPanel[]{painel1, painel2, painel3};
        this.fu = new KGradientPanel[]{kGradientPanel1, kGradientPanel2, kGradientPanel3};

        for (int i = 0; i < 3; i++) {
            de[i].getCaret().setVisible(false);
        }
        
        descarregar();

        txfProvedor.setText(sistema.Info.provedor);
        btnAvancar.requestFocus();
        atualizarInfo();

        this.setTitle("Loja de setups");

        this.setIconImage(new ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo 2.png")).getImage());

    }

    int pag = 0;
    int max = 12;

    String links = "";

    String setupsId[] = null;

    SetupLoja setups[] = null;

    void descarregar() {
        for (int i = 0; i < 3; i++) {
            de[i].setText("");
            im[i].setIcon(new ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif")));
            ti[i].setText("Carregando...");
            ta[i].setText("Tamanho: 0 MB");
            au[i].setText("Autor: Nenhum");
            id[i].setText("ID: Nenhum");
            fu[i].setkFillBackground(false);
            fu[i].setkStartColor(new Color(0x5e6266));
            fu[i].setkEndColor(new Color(0x5e6266));
            fu[i].updateUI();
        }
    }

    public void atualizarInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    links = sistema.Conectar.pegarSimples(sistema.Info.provedor + "/links.txt");

                    setupsId = links.split(", ");

                    if (max > setupsId.length) {
                        max = setupsId.length;
                    }

                    //checar se ja existe e se o tamanho foi atualizado
                    if (setups == null || setups.length != setupsId.length) {
                        setups = new SetupLoja[setupsId.length];
                    }

                    pgbProgresso.setVisible(true);
                    pgbProgresso.setMaximum(max);

                    for (int i = pag * 3; i < (max >= setupsId.length ? setupsId.length : max); i++) {
                        pgbProgresso.setValue(i);

                        //se setups[i] ja ta carreagdo e certo, entao pular o carregamento
                        if (setups[i] != null && setups[i].id.equals(setupsId[i])) {
                            continue;
                        }

                        setups[i] = new SetupLoja();
                        setups[i].id = setupsId[i];
                        //apenas a string para saber a formatacao geral [que depende inteiramente no ar[i]]
                        String linkInfo = sistema.Info.provedor + "/" + setupsId[i] + "/info.txt";

                        //usa a string criada para acessar o conteudo de info.txt dependendo do setup [determinada pelo ar[i]]
                        String coiso[] = sistema.Conectar.pegarSimples(linkInfo).replace("\r", "").split("\n");

                        //sempre formatado em [nome] / [tamanho] / [cores, se houver]
                        String setupInfo[] = coiso[0].split(" / "); //posicao 0 por que é sempre a primeira linha que contem a info
                        setups[i].nome = setupInfo[0];
                        setups[i].tamanho = setupInfo[1];

                        if (setupInfo.length > 2) {
                            setups[i].temCoresCustomizadas = true;
                            if (setupInfo[2].split(", ").length > 1) {
                                setups[i].usaListaDeCores = true;
                                setups[i].cores = sistema.Generico.converterCor(setupInfo[2]);
                            } else {
                                setups[i].tipoAnimacao = Integer.parseInt(setupInfo[2]);
                            }

                        }

                        if (setups[i].imagem == null) {
                            Image img = ImageIO.read(new URL(sistema.Info.provedor + "/" + setupsId[i] + "/cover.png"));
                            setups[i].imagem = new ImageIcon(img.getScaledInstance(150, 150, BufferedImage.SCALE_SMOOTH));
                        }

                        //j = 1 para pular a primeira linha que é sempre de informacao
                        for (int j = 1; j < coiso.length; j++) {
                            setups[i].descricao += coiso[j] + "\n";
                        }

                        setups[i].autor = sistema.Conectar.pegarSimples(sistema.Info.provedor + "/" + setupsId[i] + "/copyright.txt").split("\n")[0];

                    }

                    pgbProgresso.setVisible(false);

                    exibirSetups();

                } catch (Exception ex) {
                    JOptionPane.showConfirmDialog(null, "Não foi possível carregar a URL: " + ex.toString() + ".\n\nO site está fora do ar ou o link está errado.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }, "ATUALIZAR INFO").start();
    }

    public void exibirSetups() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lblPagina.setText("Página " + (pag + 1));

                    if ((pag * 3) - max >= 0) {
                        max += 12;
                        atualizarInfo();
                        return;
                    }

                    pgbProgresso.setVisible(true);
                    pgbProgresso.setMaximum(3);

                    if (setups[pag * 3] == null) {
                        atualizarInfo();
                    }

                    btnAvancar.setEnabled(true);

                    if (pag * 3 + 4 > setupsId.length) {
                        btnAvancar.setEnabled(false);
                    }

                    for (int i = pag * 3; i < max; i++) {

                        pgbProgresso.setValue(i + 1 - (pag * 3));

                        if (setups[i] == null) {
                            continue;
                        }

                        //atualizar informacoes na janela
                        int qual = i - (pag * 3);
                        if (qual < 3) {
                            fu[qual].setVisible(true);
                            ti[qual].setText(setups[i].nome);
                            ta[qual].setText("Tamanho: " + setups[i].tamanho);
                            de[qual].setText(setups[i].descricao);
                            id[qual].setText("ID: " + setupsId[i]);
                            im[qual].setIcon(setups[i].imagem);
                            im[qual].repaint();
                            au[qual].setText("Autor: " + setups[i].autor);
                            if (setups[i].usaListaDeCores && setups[i].cores.length > 1) {
                                fu[qual].setkFillBackground(true);
                                fu[qual].setkStartColor(setups[i].cores[0]);
                                fu[qual].setkEndColor(setups[i].cores[1]);
                                fu[qual].updateUI();
                            }

                        }
                    }

                    btnVoltar.setEnabled(pag != 0);

                    for (int i = 0; i < id.length; i++) {
                        if (id[i].getText().equals("ID: Nenhum")) {
                            fu[i].setVisible(false);

                        }
                    }

                    mudarCor(btnAvancar);
                    mudarCor(btnVoltar);

                    pgbProgresso.setVisible(false);
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    JOptionPane.showConfirmDialog(null, "Um erro ocorreu ao atualizar os elementos da loja: " + ex.toString() + ".\n\nReporte esse erro!", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }, "EXIBIR SETUPS").start();
    }

    public void mudarCor(KButton botao) {
        if (botao.isEnabled()) {
            botao.setkStartColor(new Color(0x666666));
            botao.setkEndColor(new Color(0x999999));
        } else {
            botao.setkStartColor(new Color(0x4A4A4A));
            botao.setkEndColor(new Color(0x333333));
        }
        botao.updateUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pgbProgresso = new javax.swing.JProgressBar();
        btnAvancar = new com.k33ptoo.components.KButton();
        btnVoltar = new com.k33ptoo.components.KButton();
        lblPagina = new javax.swing.JLabel();
        txfProvedor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        kGradientPanel1 = new com.k33ptoo.components.KGradientPanel();
        painel1 = new javax.swing.JPanel();
        img1 = new javax.swing.JLabel();
        titulo1 = new javax.swing.JLabel();
        tamanho1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descricao1 = new javax.swing.JTextArea();
        autor1 = new javax.swing.JLabel();
        baixar1 = new javax.swing.JButton();
        id1 = new javax.swing.JLabel();
        kGradientPanel2 = new com.k33ptoo.components.KGradientPanel();
        painel2 = new javax.swing.JPanel();
        img2 = new javax.swing.JLabel();
        titulo2 = new javax.swing.JLabel();
        tamanho2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descricao2 = new javax.swing.JTextArea();
        autor2 = new javax.swing.JLabel();
        baixar2 = new javax.swing.JButton();
        id2 = new javax.swing.JLabel();
        kGradientPanel3 = new com.k33ptoo.components.KGradientPanel();
        painel3 = new javax.swing.JPanel();
        img3 = new javax.swing.JLabel();
        titulo3 = new javax.swing.JLabel();
        tamanho3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        descricao3 = new javax.swing.JTextArea();
        autor3 = new javax.swing.JLabel();
        baixar3 = new javax.swing.JButton();
        id3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Open Sauce One Black", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Loja de setups");

        jPanel3.setMinimumSize(new java.awt.Dimension(729, 20));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pgbProgresso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pgbProgresso, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        btnAvancar.setText(">");
        btnAvancar.setkBorderRadius(250);
        btnAvancar.setkEndColor(new java.awt.Color(153, 153, 153));
        btnAvancar.setkHoverEndColor(new java.awt.Color(102, 102, 102));
        btnAvancar.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAvancar.setkHoverStartColor(new java.awt.Color(153, 153, 153));
        btnAvancar.setkStartColor(new java.awt.Color(102, 102, 102));
        btnAvancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvancarActionPerformed(evt);
            }
        });

        btnVoltar.setText("<");
        btnVoltar.setkBorderRadius(250);
        btnVoltar.setkEndColor(new java.awt.Color(153, 153, 153));
        btnVoltar.setkHoverEndColor(new java.awt.Color(102, 102, 102));
        btnVoltar.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnVoltar.setkHoverStartColor(new java.awt.Color(153, 153, 153));
        btnVoltar.setkStartColor(new java.awt.Color(102, 102, 102));
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });

        lblPagina.setFont(new java.awt.Font("Open Sauce One", 0, 18)); // NOI18N
        lblPagina.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPagina.setText("Página 0");

        txfProvedor.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        txfProvedor.setText("https://de-bonn.netlify.app/arquivos/ambientes");

        jLabel3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel3.setText("Provedor de setups:");

        jButton1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jButton1.setText("Atualizar provedor");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        painel1.setPreferredSize(new java.awt.Dimension(150, 150));

        img1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        img1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif"))); // NOI18N

        titulo1.setFont(new java.awt.Font("Open Sauce One", 1, 18)); // NOI18N
        titulo1.setText("Carregando...");

        tamanho1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        tamanho1.setText("Tamanho: 0 MB");

        jScrollPane1.setBorder(null);

        descricao1.setEditable(false);
        descricao1.setColumns(20);
        descricao1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        descricao1.setRows(5);
        jScrollPane1.setViewportView(descricao1);

        autor1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        autor1.setText("Autor: Nenhum");

        baixar1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        baixar1.setText("Baixar");
        baixar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baixar1ActionPerformed(evt);
            }
        });

        id1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        id1.setText("ID: Nenhum");

        javax.swing.GroupLayout painel1Layout = new javax.swing.GroupLayout(painel1);
        painel1.setLayout(painel1Layout);
        painel1Layout.setHorizontalGroup(
            painel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painel1Layout.createSequentialGroup()
                .addComponent(img1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titulo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(painel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tamanho1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(autor1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(baixar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(id1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        painel1Layout.setVerticalGroup(
            painel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(img1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(painel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titulo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(painel1Layout.createSequentialGroup()
                        .addComponent(tamanho1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autor1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(id1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(baixar1)))
                .addContainerGap())
        );

        javax.swing.GroupLayout kGradientPanel1Layout = new javax.swing.GroupLayout(kGradientPanel1);
        kGradientPanel1.setLayout(kGradientPanel1Layout);
        kGradientPanel1Layout.setHorizontalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painel1, javax.swing.GroupLayout.DEFAULT_SIZE, 837, Short.MAX_VALUE)
                .addContainerGap())
        );
        kGradientPanel1Layout.setVerticalGroup(
            kGradientPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painel1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        painel2.setPreferredSize(new java.awt.Dimension(150, 150));

        img2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        img2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif"))); // NOI18N

        titulo2.setFont(new java.awt.Font("Open Sauce One", 1, 18)); // NOI18N
        titulo2.setText("Carregando...");

        tamanho2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        tamanho2.setText("Tamanho: 0 MB");

        jScrollPane2.setBorder(null);

        descricao2.setEditable(false);
        descricao2.setColumns(20);
        descricao2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        descricao2.setRows(5);
        jScrollPane2.setViewportView(descricao2);

        autor2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        autor2.setText("Autor: Nenhum");

        baixar2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        baixar2.setText("Baixar");
        baixar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baixar2ActionPerformed(evt);
            }
        });

        id2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        id2.setText("ID: Nenhum");

        javax.swing.GroupLayout painel2Layout = new javax.swing.GroupLayout(painel2);
        painel2.setLayout(painel2Layout);
        painel2Layout.setHorizontalGroup(
            painel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painel2Layout.createSequentialGroup()
                .addComponent(img2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titulo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(painel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tamanho2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(autor2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(baixar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(id2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        painel2Layout.setVerticalGroup(
            painel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(img2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(painel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titulo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(painel2Layout.createSequentialGroup()
                        .addComponent(tamanho2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autor2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(id2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(baixar2)))
                .addContainerGap())
        );

        javax.swing.GroupLayout kGradientPanel2Layout = new javax.swing.GroupLayout(kGradientPanel2);
        kGradientPanel2.setLayout(kGradientPanel2Layout);
        kGradientPanel2Layout.setHorizontalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painel2, javax.swing.GroupLayout.DEFAULT_SIZE, 837, Short.MAX_VALUE)
                .addContainerGap())
        );
        kGradientPanel2Layout.setVerticalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painel2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        painel3.setPreferredSize(new java.awt.Dimension(150, 150));

        img3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        img3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif"))); // NOI18N

        titulo3.setFont(new java.awt.Font("Open Sauce One", 1, 18)); // NOI18N
        titulo3.setText("Carregando...");

        tamanho3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        tamanho3.setText("Tamanho: 0 MB");

        jScrollPane3.setBorder(null);

        descricao3.setEditable(false);
        descricao3.setColumns(20);
        descricao3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        descricao3.setRows(5);
        jScrollPane3.setViewportView(descricao3);

        autor3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        autor3.setText("Autor: Nenhum");

        baixar3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        baixar3.setText("Baixar");
        baixar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baixar3ActionPerformed(evt);
            }
        });

        id3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        id3.setText("ID: Nenhum");

        javax.swing.GroupLayout painel3Layout = new javax.swing.GroupLayout(painel3);
        painel3.setLayout(painel3Layout);
        painel3Layout.setHorizontalGroup(
            painel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painel3Layout.createSequentialGroup()
                .addComponent(img3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titulo3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(painel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tamanho3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(autor3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(baixar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(id3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        painel3Layout.setVerticalGroup(
            painel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(img3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(painel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titulo3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(painel3Layout.createSequentialGroup()
                        .addComponent(tamanho3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autor3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(id3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(baixar3)))
                .addContainerGap())
        );

        javax.swing.GroupLayout kGradientPanel3Layout = new javax.swing.GroupLayout(kGradientPanel3);
        kGradientPanel3.setLayout(kGradientPanel3Layout);
        kGradientPanel3Layout.setHorizontalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painel3, javax.swing.GroupLayout.DEFAULT_SIZE, 837, Short.MAX_VALUE)
                .addContainerGap())
        );
        kGradientPanel3Layout.setVerticalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(painel3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 687, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAvancar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfProvedor, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(kGradientPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kGradientPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kGradientPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txfProvedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kGradientPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kGradientPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kGradientPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAvancar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Tocar.lojaaberta = false;
    }//GEN-LAST:event_formWindowClosing

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        btnVoltar.setEnabled(false);
        descarregar();
        pag--;
        exibirSetups();
        btnVoltar.setEnabled(true);
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void btnAvancarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvancarActionPerformed
        btnAvancar.setEnabled(false);
        descarregar();
        pag++;
        exibirSetups();
        btnAvancar.setEnabled(true);
    }//GEN-LAST:event_btnAvancarActionPerformed

    private void baixar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixar3ActionPerformed
        new BaixarSetup(id3.getText().substring(4), titulo3.getText(), new File(System.getProperty("java.io.tmpdir") + titulo3.getText() + ".png")).setVisible(true);
    }//GEN-LAST:event_baixar3ActionPerformed

    private void baixar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixar2ActionPerformed
        new BaixarSetup(id2.getText().substring(4), titulo2.getText(), new File(System.getProperty("java.io.tmpdir") + titulo2.getText() + ".png")).setVisible(true);
    }//GEN-LAST:event_baixar2ActionPerformed

    private void baixar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixar1ActionPerformed
        new BaixarSetup(id1.getText().substring(4), titulo1.getText(), new File(System.getProperty("java.io.tmpdir") + titulo1.getText() + ".png")).setVisible(true);
    }//GEN-LAST:event_baixar1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        sistema.Info.provedor = txfProvedor.getText();
        sistema.Configs.salvar();
        txfProvedor.setText(sistema.Info.provedor);
        atualizarInfo();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Loja.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Loja.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Loja.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Loja.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Loja().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel autor1;
    private javax.swing.JLabel autor2;
    private javax.swing.JLabel autor3;
    private javax.swing.JButton baixar1;
    private javax.swing.JButton baixar2;
    private javax.swing.JButton baixar3;
    private com.k33ptoo.components.KButton btnAvancar;
    private com.k33ptoo.components.KButton btnVoltar;
    private javax.swing.JTextArea descricao1;
    private javax.swing.JTextArea descricao2;
    private javax.swing.JTextArea descricao3;
    private javax.swing.JLabel id1;
    private javax.swing.JLabel id2;
    private javax.swing.JLabel id3;
    private javax.swing.JLabel img1;
    private javax.swing.JLabel img2;
    private javax.swing.JLabel img3;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private com.k33ptoo.components.KGradientPanel kGradientPanel1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel3;
    private javax.swing.JLabel lblPagina;
    private javax.swing.JPanel painel1;
    private javax.swing.JPanel painel2;
    private javax.swing.JPanel painel3;
    private javax.swing.JProgressBar pgbProgresso;
    private javax.swing.JLabel tamanho1;
    private javax.swing.JLabel tamanho2;
    private javax.swing.JLabel tamanho3;
    private javax.swing.JLabel titulo1;
    private javax.swing.JLabel titulo2;
    private javax.swing.JLabel titulo3;
    private javax.swing.JTextField txfProvedor;
    // End of variables declaration//GEN-END:variables
}
