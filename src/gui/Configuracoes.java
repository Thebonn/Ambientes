package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import sistema.Generico;
import java.awt.Color;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Thebonn
 */
public class Configuracoes extends javax.swing.JFrame {

    Color corExemplo;

    public Configuracoes() {
        FlatDarkLaf.install();
        initComponents();
        carregarConfigs();
        this.setTitle("Configurações");
        corExemplo = new Color(0xFF00FF);
        atualizarExemplo();

        this.setIconImage(new ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo 2.png")).getImage());
        atualizarSelecionados();
    }

    public void atualizarExemplo() {
        float hsb[] = Color.RGBtoHSB(corExemplo.getRed(), corExemplo.getGreen(), corExemplo.getBlue(), null);
        jSlider4.setEnabled(false);
        switch (jComboBox4.getSelectedIndex()) {
            case 0:
                painelExemplo.setkEndColor(corExemplo.darker());
                break;
            case 1:
                painelExemplo.setkEndColor(corExemplo.brighter());
                break;
            case 2:
                painelExemplo.setkEndColor(new Color(Color.HSBtoRGB(hsb[0] - 0.5f, hsb[1], hsb[2])));
                break;
            case 3:
                jSlider4.setEnabled(true);
                painelExemplo.setkEndColor(new Color(Color.HSBtoRGB(hsb[0] + 0.1f * jSlider4.getValue() / 10, hsb[1], hsb[2])));
                break;
            case 4:
                jSlider4.setEnabled(true);
                painelExemplo.setkEndColor(new Color(Color.HSBtoRGB(hsb[0] - 0.1f * jSlider4.getValue() / 10, hsb[1], hsb[2])));
                break;
            case 5:
                painelExemplo.setkEndColor(Color.white);
                break;
            case 6:
                painelExemplo.setkEndColor(Color.black);
                break;
            default:
                painelExemplo.setkEndColor(painelExemplo.getkStartColor());
        }

        painelExemplo.updateUI();
    }

    public void carregarConfigs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                jSlider1.setValue(sistema.Info.atualizacao);
                jLabel2.setText("Tempo de atualização: " + sistema.Info.atualizacao + " ms");
//                System.out.println(Double.toString(Tocar.velocidade));
                jComboBox2.setSelectedItem(Double.toString(sistema.Info.velocidade).replace(".0", ""));
                jSlider2.setValue(sistema.Info.maximo);
                jCheckBox1.setSelected(sistema.Info.podeColorir);
                jComboBox1.setSelectedIndex(sistema.Info.animTipo);
                jComboBox3.setSelectedIndex(Tocar.coresEspSel);
                jTextField1.setText(Tocar.coresLegiveis);
//                jCheckBox2.setSelected(Tocar.sombra);
                jComboBox4.setSelectedIndex(sistema.Info.tipoCorEspecial);
                jTextField2.setText(sistema.Info.preDelay + "");
                jSlider4.setValue((int) (sistema.Info.intensidade * 10));
                jSlider5.setValue(sistema.Info.escurecerFundo);
                jTextField1.setText(Tocar.coresLegiveis);
                jComboBox5.setSelectedIndex(sistema.Info.rpcTipo);
                jCheckBox2.setSelected(sistema.Info.mostrarSetups);
                jCheckBox3.setSelected(sistema.Info.animacaoIntroducao);
                jCheckBox4.setSelected(sistema.Info.iconeInterativo);
                chkBandeja.setSelected(sistema.Info.usarBandeja);

                if (!Tocar.suportaSystemTray) {
                    chkBandeja.setEnabled(false);
                    chkBandeja.setToolTipText("Seu sistema não suporta ícones de bandeja");
                }

                txfLocal.setCaretPosition(txfLocal.getText().length());
                for (int i = 0; i < 4; i++) {
                    if (jComboBox6.getItemAt(i).split(": ")[1].contains(Float.toString(Tocar.mudancaVel))) {
                        jComboBox6.setSelectedIndex(i);
                    }
                }

                txfLocal.setText(sistema.Info.localSetups);
                jCheckBox5.setSelected(sistema.Info.usarCoresDoSetup);
            }
        }, "CARREGAR CONFIGS").start();
    }

    void aplicar() {
        sistema.Info.animTipo = (byte) jComboBox1.getSelectedIndex();
        sistema.Info.atualizacao = (short) jSlider1.getValue();
        sistema.Info.velocidade = Float.parseFloat(jComboBox2.getSelectedItem().toString());
        sistema.Info.maximo = (byte) jSlider2.getValue();
        sistema.Info.podeColorir = jCheckBox1.isSelected();

        Color saida[];

        //em ordem: pastel, carro na chuva, por do sol, verao, aurora boreal, gay, floresta, ps2
        String cores[] = {"ABDEE6, CBAACB, FFFFB5, FFCCB6, F3B0C3", "222633, 452224, 223145, 131830, 101a26, 704219, 0a1f33, 143c4a",
            "eeaf61, fb9062, ee5d6c, ce4993, 6a0d83", "5fa65a, 01b4bb, f5d51e, fa8825, fa5456", "050306, 12093e, 412754, 4cc35b, 68da23",
            "0b7c6a, 5cc8d2, f1eeff, 7bade2, 073370", "90aa4a, 81803e, 202f1f, 535b32, 57743e, c1b967, 90aa4a, 867b3e, 3a5a35",
            "191a61, 010105, 080814, 1f1fb8, 00000a"};

        if ((jComboBox3.getSelectedIndex() == jComboBox3.getItemCount() - 1) && (jTextField1.getText() != null || jTextField1.getText().contains(", "))) {
            saida = Generico.converterCor(jTextField1.getText());
        } else {
            saida = Generico.converterCor(cores[jComboBox3.getSelectedIndex()]);
        }

        Tocar.cores = saida;
        Tocar.coresEspSel = (byte) jComboBox3.getSelectedIndex();
        sistema.Info.tipoCorEspecial = (byte) jComboBox4.getSelectedIndex();
        sistema.Info.preDelay = (short) Integer.parseInt(jTextField2.getText());
        sistema.Info.intensidade = jSlider4.getValue() / 10;
        sistema.Info.escurecerFundo = (byte) jSlider5.getValue();
        Tocar.coresLegiveis = jTextField1.getText();
        sistema.Info.rpcTipo = (byte) jComboBox5.getSelectedIndex();
        sistema.Info.mostrarSetups = jCheckBox2.isSelected();
        sistema.Info.animacaoIntroducao = jCheckBox3.isSelected();
        sistema.Info.iconeInterativo = jCheckBox4.isSelected();
        Tocar.mudancaVel = Float.parseFloat(jComboBox6.getSelectedItem().toString().split(": ")[1]);
        sistema.Info.usarCoresDoSetup = jCheckBox5.isSelected();
        sistema.Info.usarBandeja = chkBandeja.isSelected();
    }

    void atualizarSelecionados() {

        jLabel6.setEnabled(jComboBox1.getSelectedItem().toString().equals("Cores especiais"));
        jComboBox3.setEnabled(jComboBox1.getSelectedItem().toString().equals("Cores especiais"));
        jTextField1.setEnabled(jComboBox1.getSelectedItem().toString().equals("Cores especiais") && jComboBox3.getSelectedItem().toString().equals("Outro"));

        jComboBox2.setEnabled(jComboBox1.getSelectedIndex() != 6);
        lblAviso.setText(jComboBox1.getSelectedIndex() != 5 ? "Aviso: \"Cores especiais\" não está selecionado" : " ");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnSalvar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox<>();
        jCheckBox5 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txfLocal = new javax.swing.JTextField();
        btnMudarLocal = new javax.swing.JToggleButton();
        jLabel16 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();
        lblEscurecer = new javax.swing.JLabel();
        jSlider5 = new javax.swing.JSlider();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        painelExemplo = new com.k33ptoo.components.KGradientPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jSlider3 = new javax.swing.JSlider();
        jSlider4 = new javax.swing.JSlider();
        jLabel12 = new javax.swing.JLabel();
        lblAviso = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jCheckBox4 = new javax.swing.JCheckBox();
        chkBandeja = new javax.swing.JCheckBox();
        jButton5 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        bntAplicar = new javax.swing.JToggleButton();

        jLabel11.setText("jLabel11");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        btnSalvar.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Open Sauce One Black", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Configurações");

        jTabbedPane1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N

        jComboBox2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0.1", "0.5", "1", "1.25", "1.5", "2", "4", "8", "16", "32" }));
        jComboBox2.setSelectedIndex(2);
        jComboBox2.setToolTipText("Velocidade da animação em alguns estilos");

        jLabel3.setText("Velocidade:");

        jComboBox1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fogo", "RGB", "Mar", "Raios", "Luz oscilante", "Cores especiais", "Nenhum" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel1.setText("Estilo de animação:");

        jLabel6.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel6.setText("Cores:");

        jLabel7.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel7.setText("Customizado:");

        jTextField1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jTextField1.setToolTipText("Insira as cores em HEX, separado por vírgula e espaço");
        jTextField1.setEnabled(false);

        jComboBox3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pastel", "Carro na chuva", "Pôr do sol", "Verão", "Aurora boreal", "GAY!!!", "Floresta", "Playstation 2", "Outro" }));
        jComboBox3.setEnabled(false);
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jCheckBox5.setSelected(true);
        jCheckBox5.setText("Usar cores do setup caso tenha");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, 0, 204, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addComponent(jCheckBox5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );

        jTabbedPane1.addTab("Animação", jPanel3);

        jSlider1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jSlider1.setMajorTickSpacing(500);
        jSlider1.setMaximum(2100);
        jSlider1.setMinimum(100);
        jSlider1.setMinorTickSpacing(100);
        jSlider1.setPaintTicks(true);
        jSlider1.setSnapToTicks(true);
        jSlider1.setToolTipText("Enquanto menor o valor, eventos ocorrerão com mais frequência");
        jSlider1.setValue(300);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tempo de atualização: 800 ms");
        jLabel2.setToolTipText("Enquanto menor o valor, eventos ocorrerão com mais frequência");

        jLabel8.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel8.setText("Predelay de playlist:");

        jTextField2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jTextField2.setText("120");
        jTextField2.setToolTipText("Delay em MS, aumente o número caso ouvir o silêncio,\ndiminua se ouvir sobreposição");
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Local dos setups:");

        txfLocal.setEditable(false);
        txfLocal.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        txfLocal.setText("Lorem");

        btnMudarLocal.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        btnMudarLocal.setText("Mudar");
        btnMudarLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMudarLocalActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel16.setText("ms");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(58, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)))
                        .addGap(63, 63, 63)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txfLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMudarLocal))
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE))
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txfLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMudarLocal)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Setups", jPanel4);

        jCheckBox1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Colorir com fundo quando inativo");
        jCheckBox1.setToolTipText("Desative para economizar memória");

        jLabel5.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Tempo de espera: 10 segundos");

        jSlider2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jSlider2.setMaximum(21);
        jSlider2.setMinimum(3);
        jSlider2.setMinorTickSpacing(2);
        jSlider2.setSnapToTicks(true);
        jSlider2.setToolTipText("Tempo de espera para o menu sumir");
        jSlider2.setValue(10);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        lblEscurecer.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        lblEscurecer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEscurecer.setText("Escurecer fundo: 0%");

        jSlider5.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jSlider5.setValue(0);
        jSlider5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider5StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(146, 146, 146)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSlider5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEscurecer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(139, 139, 139))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEscurecer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Fundo", jPanel5);

        jLabel10.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel10.setText("Tipo de cor secundária: ");

        painelExemplo.setkGradientFocus(0);
        painelExemplo.setPreferredSize(new java.awt.Dimension(114, 114));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(40, 40));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Aa");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout painelExemploLayout = new javax.swing.GroupLayout(painelExemplo);
        painelExemplo.setLayout(painelExemploLayout);
        painelExemploLayout.setHorizontalGroup(
            painelExemploLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelExemploLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        painelExemploLayout.setVerticalGroup(
            painelExemploLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelExemploLayout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jComboBox4.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sombra", "Luz", "Cor inversa", "Decslocamento na matiz (+)", "Decslocamento na matiz (-)", "Branco", "Preto", "A mesma da cor primária" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jButton2.setText("Escolher cor");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jSlider3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jSlider3.setMaximum(700);
        jSlider3.setMinimum(-700);
        jSlider3.setToolTipText("");
        jSlider3.setValue(0);
        jSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider3StateChanged(evt);
            }
        });

        jSlider4.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jSlider4.setValue(10);
        jSlider4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider4StateChanged(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel12.setText("Intensidade:");

        lblAviso.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        lblAviso.setForeground(new java.awt.Color(255, 51, 51));
        lblAviso.setText("Aviso: \"Cores especiais\" não está selecionado");

        jLabel14.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel14.setText("Velocidade de transição:");

        jComboBox6.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Instantâneo: 1", "Muito rápido: 0.015", "Rápido: 0.009", "Normal: 0.002", "Lento: 0.001" }));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jSlider4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblAviso)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(painelExemplo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton2)
                        .addGap(4, 4, 4)
                        .addComponent(painelExemplo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAviso)))
        );

        jTabbedPane1.addTab("Cores especiais", jPanel6);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Discord RPC", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mostrar todas as informações", "Mostras poucas informações", "Não mostrar informações", "Desativado" }));

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("Mostrar quantidade de setups instalados");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBox3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Animação de introdução");
        jCheckBox3.setToolTipText("Opção que define se a animação da janela cair e esmaecer ao abrir será exibida");

        jButton3.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jButton3.setText("Fechar programa");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jCheckBox4.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jCheckBox4.setSelected(true);
        jCheckBox4.setText("Ícone interativo");
        jCheckBox4.setToolTipText("O ícone que itera dependendo se algo está sendo tocado");

        chkBandeja.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        chkBandeja.setSelected(true);
        chkBandeja.setText("Usar ícone de bandeja (req. reinc.)");

        jButton5.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jButton5.setText("Reabrir sem interface");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCheckBox3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBox4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkBandeja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jCheckBox3)
                        .addGap(5, 5, 5)
                        .addComponent(jCheckBox4)
                        .addGap(5, 5, 5)
                        .addComponent(chkBandeja)
                        .addGap(18, 18, 18))
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton5))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Outros", jPanel8);

        jScrollPane1.setBorder(null);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Changelog\n\n- Sistema de carregamento reescrito\n- Melhorado o reprodutor de áudio\n- Modo sem gui adicionado\n- Reescrito o sistema de animação de fundo\n- Introdução de primeira vez\n- Fonte customizada embutida na interface\n- Pasta de setups ser alterável\n- Setups podem ter cores\n- Loja melhorada\n\nObrigado por usar esse programa! <3\n\t- Thebonn");
        jScrollPane1.setViewportView(jTextArea1);

        jLabel13.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jLabel13.setText("Ambientes versão 1.2 [20/09/2025]");

        jButton4.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        jButton4.setText("Informações técnicas");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jButton4))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );

        jTabbedPane1.addTab("Informações", jPanel7);

        bntAplicar.setFont(new java.awt.Font("Open Sauce One", 0, 12)); // NOI18N
        bntAplicar.setText("Aplicar");
        bntAplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntAplicarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bntAplicar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalvar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(bntAplicar))
                .addGap(7, 7, 7))
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

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        aplicar();
        sistema.Configs.salvar();
        this.dispose();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        jLabel2.setText("Tempo de atualização: " + jSlider1.getValue() + " ms");
    }//GEN-LAST:event_jSlider1StateChanged

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        jLabel5.setText("Tempo de espera: " + Integer.toString(jSlider2.getValue() - 1) + " segundos");
    }//GEN-LAST:event_jSlider2StateChanged

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        atualizarSelecionados();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        atualizarSelecionados();
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        String caracteres = "1234567890";
        if (!caracteres.contains(evt.getKeyChar() + "")) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField2KeyTyped

    private void jSlider3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider3StateChanged
        painelExemplo.setkGradientFocus(jSlider3.getValue());
        painelExemplo.updateUI();
    }//GEN-LAST:event_jSlider3StateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Color corAntiga = corExemplo;
        corExemplo = JColorChooser.showDialog(null, "Escolha uma cor incrível", corAntiga);
        if (corExemplo == null) {
            corExemplo = corAntiga;
        }

        painelExemplo.setkStartColor(corExemplo);
        atualizarExemplo();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        atualizarExemplo();
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jSlider4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider4StateChanged
        atualizarExemplo();
    }//GEN-LAST:event_jSlider4StateChanged

    private void jSlider5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider5StateChanged
        lblEscurecer.setText("Escurecer fundo: " + jSlider5.getValue() + "%");
    }//GEN-LAST:event_jSlider5StateChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (jButton3.getText().equals("Fechar programa")) {
            jButton3.setText("Pressione novamente");
        } else {
            System.exit(0);
            sistema.Rpc.pararRPC();
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        new Infos().setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnMudarLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMudarLocalActionPerformed

        try {
            JFileChooser jfc = new JFileChooser(new File(sistema.Info.localSetups));
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.showDialog(null, "Escolher local");

            String lista[] = new File(sistema.Info.localSetups).list();
            int e = -1;
            if (lista != null && lista.length > 0) {
                e = JOptionPane.showConfirmDialog(null, "Você tem itens na sua pasta de setups. Deseja mover os itens da pasta antiga para a nova?", "Ambientes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            }
            if (e != 3) {
                if (e == 0) {
                    sistema.Generico.moverPasta(new File(sistema.Info.localSetups), jfc.getSelectedFile());
                    new File(sistema.Info.localSetups).delete();
                }

                sistema.Info.localSetups = jfc.getSelectedFile().getAbsolutePath();
                txfLocal.setText(sistema.Info.localSetups);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_btnMudarLocalActionPerformed

    private void bntAplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntAplicarActionPerformed
        aplicar();
    }//GEN-LAST:event_bntAplicarActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
            //quem sabe um dia: suporte para linux
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \""
                    + "java -jar \"" + new File(Configuracoes.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath() + "\" semgui"
                    + "\"");
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(null, "Não foi possível iniciar Ambientes sem interface: " + ex.toString() + "/nVocê pode tentar rodar o .jar do Ambientes com o argumento 'semgui' manualmente", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(Configuracoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Configuracoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Configuracoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Configuracoes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Configuracoes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton bntAplicar;
    private javax.swing.JToggleButton btnMudarLocal;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox chkBandeja;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private javax.swing.JSlider jSlider5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblEscurecer;
    private com.k33ptoo.components.KGradientPanel painelExemplo;
    private javax.swing.JTextField txfLocal;
    // End of variables declaration//GEN-END:variables
}
