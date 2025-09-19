package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import sistema.Componentes;
import sistema.Info;

/**
 *
 * @author Bonn
 */
public class Splash extends javax.swing.JFrame {

    String textoSplash = "";

    public Splash() {
        FlatDarkLaf.install();
        initComponents();

        this.setIconImage(new ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo 2.png")).getImage());
        funcionar();

    }

//    void ficarMudandoTexto() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        selecionarTexto();
//                        Componentes.mudarTexto(textoSplash.equals("") ? textos[sistema.Generico.random(0, textos.length)] : textoSplash, lblNegocio, 40);
//                        Thread.sleep(2000);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }, "TAD").start();
//    }

    public void funcionar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Thread.sleep(100);

                    pgbProgresso.setString("Verificando atualizações...");
                    selecionarTexto();
                    Componentes.mudarTexto(textoSplash.equals("") ? textos[sistema.Generico.random(0, textos.length)] : textoSplash, lblNegocio, 40);

                    pgbProgresso.setString("Carregando fontes...");
                    boolean res = recursos.fontes.Fonte.carregarFontes();
                    if (res) {
                        lblNegocio.setFont(new Font("Open Sauce One Black", 0, lblNegocio.getFont().getSize()));
                        pgbProgresso.setFont(new Font("Open Sauce One", 0, 13));
                    } else {
                        JOptionPane.showConfirmDialog(null, "Não foi possível carregar as fontes, o programa funcionará usando a fonte de fallback.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    }

                    pgbProgresso.setString("Verificando as pastas...");
                    File file = new File(Info.localSetups);
                    if (!file.exists()) {
                        pgbProgresso.setString("Criando as pastas...");
                        file.mkdirs();
                    }

                    pgbProgresso.setString("Verificando arquivos...");

                    File configsFile = new File("Arquivos/configuracoes.txt");

                    if (!configsFile.exists()) {
                        try {
                            configsFile.createNewFile();
                            sistema.Configs.salvar();
                        } catch (Exception ex) {
                            JOptionPane.showConfirmDialog(null, "Não foi possível criar o arquivo de configuração: " + ex.toString() + ". O programa fechará agora.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }

                    }

                    sistema.Configs.carregar();

                    pgbProgresso.setString("Verificando setups...");

                    Thread.sleep(100);
                    File setups[] = new File(Info.localSetups).listFiles();

                    if (setups == null) {
                        pgbProgresso.setString("Nenhum setup instalado...");
                        Thread.sleep(200);
                    } else {
                        pgbProgresso.setIndeterminate(false);
                        pgbProgresso.setMaximum(setups.length);

                        for (int i = 0; i < setups.length; i++) {
                            pgbProgresso.setValue(i);
                            File mestre = new File(setups[i].getAbsolutePath() + "/mestre.txt");
                            String nome = new File(mestre.getParent()).getName();
                            pgbProgresso.setString("Verificando " + nome + " ...");

                            if (!mestre.exists()) {
                                JOptionPane.showConfirmDialog(null, "Atenção! O setup " + nome + " não tem o arquivo \"mestre.txt\" para funcionar! É recomendado você contatar o criador", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }

                    pgbProgresso.setIndeterminate(true);

                    pgbProgresso.setString("Abrindo o Ambientes...");
                    Thread.sleep(1000);
                    cair();

                } catch (Exception ex) {
                    JOptionPane.showConfirmDialog(null, "Um erro ocorreu ao tentar abrir o ambientes: " + ex.toString(), "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    System.exit(-1);
                }
            }
        }, "FUNCIONAR").start();
    }

    public void selecionarTexto() {
        String saida = "";
        String ar[];
        try {
            saida = sistema.Conectar.pegarSimples("https://pastebin.com/raw/jwdN4yRp");
        } catch (Exception ex) {
            pgbProgresso.setString("Aviso: Não há conexão com internet ou o servidor do pastebin está fora do ar");
        }

        if (saida != null && !saida.equals("")) {
            ar = saida.split("\n");

            sistema.Info._versaoReal = Double.parseDouble(ar[0]);
            if (sistema.Info._versaoReal > sistema.Info.VERSAO_ATUAL) {
                int opc = JOptionPane.showConfirmDialog(null, "Atenção! Há uma nova versão do Ambientes disponível para download. Você deseja abrir a página do Github e instalar a versão nova?", "Ambientes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opc == 0) {
                    try {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            Desktop.getDesktop().browse(new URI("https://github.com/Thebonn/Ambientes/releases"));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showConfirmDialog(null, "Não foi possível abrir o seu navegador padrão. Você terá que instalar na página https://github.com/Thebonn/Ambientes/releases manualmente.", "Ambientes", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    }

                }
            }

            textoSplash = ar[sistema.Generico.random(1, ar.length)];
        }

    }

    public void cair() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sistema.Info.animacaoIntroducao == true) {
                        float velocidade = 1;
                        Thread.currentThread().setPriority(7);
                        Date anter;
                        Date atual;
                        long adicionar;
                        for (int i = 0; i < 30; i++) {
                            atual = new Date();

                            setLocation(getLocation().x, (int) (getLocation().y + velocidade));
                            velocidade = velocidade * 1.1f;
                            setOpacity(((i / 30f) / -1) + 1);

                            anter = new Date();
                            adicionar = anter.getTime() - atual.getTime();
                            int resultado = (int) (20 - adicionar);
                            if (resultado < 0) {
                                resultado = 0;
                            }
                            Thread.sleep(resultado);
                        }
                    }

                    if (sistema.Info.primeiraVez) {
                        new PrimeiraVez().setVisible(true);
                    } else {
                        new Tocar(sistema.Info.animacaoIntroducao).setVisible(true);
                    }

                    dispose();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "TAD").start();
    }

    //textos caso a pessoa esteja sem internet
    public String textos[] = {"Agora sem açúcar!", "Lorem Ipsum", "GAY GAY HOMOSSEXUAL GAY", "e n t e r i n g  t h e  p l a z a . . .",
        "Vamo que vamo!", "Dança gatinho, dança!", "as vozes...", "WOOF WOOF BARK BARK ARF ARF BARK WOOF WOOF GRRRRR", "Rumo à paz",
        "Nunca ouça nada desse programa às 3 da manhã", "Vamos vamos!", "Simbora!!!", "MIAU!!!!!", "Divirta-se", "Que belos sons!", "Aviso de pancadas de chuva!",
        "so mim umilhao e mim, maltratao", "Meia noite eu te conto", "O próprio!", "ABCW", "Só nos sonszinhos!!!", "Bem na hora!", "eles disseram, \"O ambiente.\"", "biribabum",
        "Agora sem o Delay de Libet!", "Começando o dia bem", "Uiui", "Experimente CHOCOLATE™"};

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
        lblNegocio = new javax.swing.JLabel();
        pgbProgresso = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/imagens/ambientes logo.gif"))); // NOI18N

        lblNegocio.setFont(new java.awt.Font("Gadugi", 1, 24)); // NOI18N
        lblNegocio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNegocio.setText("Ambientes está iniciando!");

        pgbProgresso.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        pgbProgresso.setIndeterminate(true);
        pgbProgresso.setString("Carregando...");
        pgbProgresso.setStringPainted(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pgbProgresso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(lblNegocio, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNegocio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pgbProgresso, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Splash().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblNegocio;
    private javax.swing.JProgressBar pgbProgresso;
    // End of variables declaration//GEN-END:variables
}
