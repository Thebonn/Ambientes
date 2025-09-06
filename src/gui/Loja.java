/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.k33ptoo.components.KButton;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Thebonn
 */
public class Loja extends javax.swing.JFrame {

    /**
     * Creates new form Loja
     */
//    sist.Sistema sist = new sist.Sistema();
    public Loja() {
        FlatDarkLaf.install();
        initComponents();
//        atualizarInfo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                atualizarInfo();
            }
        }, "TAD").start();

        this.setTitle("Loja de setups");

        this.setIconImage(new ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo 2.png")).getImage());

    }

    int pag = 0;
    int max = 12;
    
    String links = "";

    String ar[] = null;
    String linkInfo[] = null;
    String linhas[][] = null;
    String info[][] = null;
    String autor[] = null;

    public void atualizarInfo() {

        try {
            links = sistema.Conectar.pegarSimples("https://banco-de-dados.netlify.app/Arquivos/Ambientes/links.txt");

            ar = new String[links.split(", ").length];
            ar = links.split(", ");

            if (max > ar.length) {
                max = ar.length;
            }

            linkInfo = new String[ar.length];
            autor = new String[ar.length];
            info = new String[ar.length][];

            /*eu queria q desse para criar o tamanho do segundo coiso dentro do
                    * for com o coiso.length, mas eu nao achei como, entao vou ter que
                    * dar um tamanho limite mesmo
             */
            linhas = new String[ar.length][50];
            System.out.println("vai carregar da pag " + pag * 3 + " ate a " + max);
            for (int i = pag * 3; i < max; i++) {

                if (i >= ar.length) {
                    break;
                }

                linkInfo[i] = "https://banco-de-dados.netlify.app/Arquivos/Ambientes/" + ar[i] + "/info.txt";
                String coiso[] = sistema.Conectar.pegarSimples(linkInfo[i]).split("\n");
                for (int j = 0; j < coiso.length; j++) {
                    linhas[i][j] = coiso[j];
                    info[i] = linhas[i][0].split(", ");
                }

                autor[i] = sistema.Conectar.pegarSimples("https://banco-de-dados.netlify.app/Arquivos/Ambientes/" + ar[i] + "/copyright.txt").split("\n")[0];

            }

            pegarLinks();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void pegarLinks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    jLabel2.setText("Página " + Integer.toString(pag + 1));
                    JLabel t[] = {id1, id2, id3};
                    JPanel p[] = {painel1, painel2, painel3};

                    for (int i = 0; i < t.length; i++) {
                        t[i].setText("ID: Nenhum");
                        p[i].setVisible(true);
                    }

//                    String ar[] = links.split(", ");
                    pgbProgresso.setVisible(true);
                    pgbProgresso.setMaximum(3);

                    if ((pag * 3) - max >= 0) {
                        max += 12;
                        System.out.println("atualize!!!!!!!");
                        atualizarInfo();

                        return;
                    }

                    if (info[pag * 3] == null) {

                        atualizarInfo();
                    }

                    kButton1.setEnabled(true);

                    if (pag * 3 + 4 > ar.length) {
                        kButton1.setEnabled(false);
                    }

                    for (int i = pag * 3; i < max; i++) {

                        pgbProgresso.setValue(i + 1 - (pag * 3));

                        String caminho = System.getProperty("java.io.tmpdir") + info[i][0] + ".png";
                        if (!new File(caminho).exists()) {
                            ImageIO.write(ImageIO.read(sistema.Conectar.pegarInputStream("https://banco-de-dados.netlify.app/Arquivos/Ambientes/" + ar[i] + "/cover.png")), "png", new File(caminho));
                        }
                        
                        InputStream is = new FileInputStream(caminho);
                        ImageIcon imagem = new ImageIcon(ImageIO.read(is).getScaledInstance(150, 150, BufferedImage.SCALE_SMOOTH));

                        switch (i - (pag * 3)) {
                            case 0:
                                titulo1.setText(info[i][0]);
                                tamanho1.setText("Tamanho: " + info[i][1]);
                                descricao1.setText("");
                                id1.setText("ID: " + ar[i]);
                                for (int j = 1; j < linhas.length; j++) {
                                    if (linhas[i][j] != null) {
                                        descricao1.setText(descricao1.getText() + linhas[i][j] + "\n");
                                    }
                                }
                                img1.setIcon(imagem);
                                img1.repaint();
                                autor1.setText("Autor: " + autor[i]);

                                break;
                            case 1:

                                titulo2.setText(info[i][0]);
                                tamanho2.setText("Tamanho: " + info[i][1]);
                                descricao2.setText("");
                                id2.setText("ID: " + ar[i]);
                                for (int j = 1; j < linhas.length; j++) {
                                    if (linhas[i][j] != null) {
                                        descricao2.setText(descricao2.getText() + linhas[i][j] + "\n");
                                    }
                                }
                                img2.setIcon(imagem);
                                img2.repaint();
                                autor2.setText("Autor: " + autor[i]);

                                break;
                            case 2:
                                titulo3.setText(info[i][0]);
                                tamanho3.setText("Tamanho: " + info[i][1]);
                                descricao3.setText("");
                                id3.setText("ID: " + ar[i]);
                                for (int j = 1; j < linhas.length; j++) {
                                    if (linhas[i][j] != null) {
                                        descricao3.setText(descricao3.getText() + linhas[i][j] + "\n");
                                    }

                                }
//                                img3.setIcon(new ImageIcon(aux));
                                img3.setIcon(imagem);
                                img3.repaint();
                                autor3.setText("Autor: " + autor[i]);

                                break;
                            default:
                                break;
                        }

                        is.close();

//                        descricao1.setText(descricao1.getText().replace("null", "").replace("\n\n", ""));
//                        descricao2.setText(descricao2.getText().replace("null", "").replace("\n\n", ""));
//                        descricao3.setText(descricao3.getText().replace("null", "").replace("\n\n", ""));
                    }

                    kButton2.setEnabled(pag != 0);
//                    System.out.println(pag);
//                
                    for (int i = 0; i < t.length; i++) {
                        if (t[i].getText().equals("ID: Nenhum")) {
                            p[i].setVisible(false);

                        }
                    }

                    mudarCor(kButton1);
                    mudarCor(kButton2);

                    pgbProgresso.setVisible(false);
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "TAD").start();
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
        painel1 = new javax.swing.JPanel();
        img1 = new javax.swing.JLabel();
        titulo1 = new javax.swing.JLabel();
        tamanho1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descricao1 = new javax.swing.JTextArea();
        autor1 = new javax.swing.JLabel();
        baixar1 = new javax.swing.JButton();
        id1 = new javax.swing.JLabel();
        painel2 = new javax.swing.JPanel();
        img2 = new javax.swing.JLabel();
        titulo2 = new javax.swing.JLabel();
        tamanho2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descricao2 = new javax.swing.JTextArea();
        autor2 = new javax.swing.JLabel();
        baixar2 = new javax.swing.JButton();
        id2 = new javax.swing.JLabel();
        painel3 = new javax.swing.JPanel();
        img3 = new javax.swing.JLabel();
        titulo3 = new javax.swing.JLabel();
        tamanho3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        descricao3 = new javax.swing.JTextArea();
        autor3 = new javax.swing.JLabel();
        baixar3 = new javax.swing.JButton();
        id3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        pgbProgresso = new javax.swing.JProgressBar();
        kButton1 = new com.k33ptoo.components.KButton();
        kButton2 = new com.k33ptoo.components.KButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Loja de setups");

        painel1.setPreferredSize(new java.awt.Dimension(150, 150));

        img1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif"))); // NOI18N

        titulo1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        titulo1.setText("Carregando...");

        tamanho1.setText("Tamanho: 0 MB");

        descricao1.setEditable(false);
        descricao1.setColumns(20);
        descricao1.setRows(5);
        jScrollPane1.setViewportView(descricao1);

        autor1.setText("Autor: Nenhum");

        baixar1.setText("Baixar");
        baixar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baixar1ActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(baixar1)))
                .addContainerGap())
        );

        painel2.setPreferredSize(new java.awt.Dimension(150, 150));

        img2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif"))); // NOI18N

        titulo2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        titulo2.setText("Carregando...");

        tamanho2.setText("Tamanho: 0 MB");

        descricao2.setEditable(false);
        descricao2.setColumns(20);
        descricao2.setRows(5);
        jScrollPane2.setViewportView(descricao2);

        autor2.setText("Autor: Nenhum");

        baixar2.setText("Baixar");
        baixar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baixar2ActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(baixar2)))
                .addContainerGap())
        );

        painel3.setPreferredSize(new java.awt.Dimension(150, 150));

        img3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        img3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/carregando.gif"))); // NOI18N

        titulo3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        titulo3.setText("Carregando...");

        tamanho3.setText("Tamanho: 0 MB");

        descricao3.setEditable(false);
        descricao3.setColumns(20);
        descricao3.setRows(5);
        jScrollPane3.setViewportView(descricao3);

        autor3.setText("Autor: Nenhum");

        baixar3.setText("Baixar");
        baixar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baixar3ActionPerformed(evt);
            }
        });

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(baixar3)))
                .addContainerGap())
        );

        jPanel3.setMinimumSize(new java.awt.Dimension(729, 20));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(pgbProgresso, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 8, 780, -1));

        kButton1.setText(">");
        kButton1.setkBorderRadius(250);
        kButton1.setkEndColor(new java.awt.Color(153, 153, 153));
        kButton1.setkHoverEndColor(new java.awt.Color(102, 102, 102));
        kButton1.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        kButton1.setkHoverStartColor(new java.awt.Color(153, 153, 153));
        kButton1.setkStartColor(new java.awt.Color(102, 102, 102));
        kButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton1ActionPerformed(evt);
            }
        });

        kButton2.setText("<");
        kButton2.setkBorderRadius(250);
        kButton2.setkEndColor(new java.awt.Color(153, 153, 153));
        kButton2.setkHoverEndColor(new java.awt.Color(102, 102, 102));
        kButton2.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        kButton2.setkHoverStartColor(new java.awt.Color(153, 153, 153));
        kButton2.setkStartColor(new java.awt.Color(102, 102, 102));
        kButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Página 0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(kButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 687, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(kButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(painel1, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
                    .addComponent(painel2, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
                    .addComponent(painel3, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(painel1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(painel2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(painel3, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void kButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kButton2ActionPerformed
        pag--;
        pegarLinks();
    }//GEN-LAST:event_kButton2ActionPerformed

    private void kButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kButton1ActionPerformed
        pag++;
        pegarLinks();
    }//GEN-LAST:event_kButton1ActionPerformed

    private void baixar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixar3ActionPerformed
        new BaixarSetup(id3.getText().substring(4), titulo3.getText(), new File(System.getProperty("java.io.tmpdir") + titulo3.getText() + ".png")).setVisible(true);
    }//GEN-LAST:event_baixar3ActionPerformed

    private void baixar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixar2ActionPerformed
        new BaixarSetup(id2.getText().substring(4), titulo2.getText(), new File(System.getProperty("java.io.tmpdir") + titulo2.getText() + ".png")).setVisible(true);
    }//GEN-LAST:event_baixar2ActionPerformed

    private void baixar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixar1ActionPerformed
        new BaixarSetup(id1.getText().substring(4), titulo1.getText(), new File(System.getProperty("java.io.tmpdir") + titulo1.getText() + ".png")).setVisible(true);
    }//GEN-LAST:event_baixar1ActionPerformed

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
    private javax.swing.JTextArea descricao1;
    private javax.swing.JTextArea descricao2;
    private javax.swing.JTextArea descricao3;
    private javax.swing.JLabel id1;
    private javax.swing.JLabel id2;
    private javax.swing.JLabel id3;
    private javax.swing.JLabel img1;
    private javax.swing.JLabel img2;
    private javax.swing.JLabel img3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private com.k33ptoo.components.KButton kButton1;
    private com.k33ptoo.components.KButton kButton2;
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
    // End of variables declaration//GEN-END:variables
}
