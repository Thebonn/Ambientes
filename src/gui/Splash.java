/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Thebonn
 */
public class Splash extends javax.swing.JFrame {

    /**
     * Creates new form Splash
     */
    Tocar tocar;
    String escolhido = "";

    public Splash() {
        FlatDarkLaf.install();
        initComponents();
        
        URL iconURL = getClass().getResource("/imagens/ambientes logo 2.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
        
        funcionar();

    }

    void ficarMudandoTexto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        mudatexto();
                        Thread.sleep(1000);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "TAD").start();
    }

    public void funcionar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);

//                    sistema.Configs configs = new sistema.Configs();

                    pgbProgresso.setString("Verificando as pastas...");

                    File file = new File("Arquivos/setups");
                    if (!file.exists()) {
                        pgbProgresso.setString("Criando as pastas...");
                        file.mkdirs();
                    }

                    pgbProgresso.setString("Verificando arquivos...");

                    File configsFile = new File("Arquivos/configs.txt");

                    if (!configsFile.exists()) {
                        try {
                            configsFile.createNewFile();
                            sistema.Configs.salvar();
                        } catch (Exception ex) {
                            JOptionPane.showConfirmDialog(null, "Não foi possível criar o arquivo de configuração: " + ex.toString() + ". O programa fechará agora.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }

                    }

                    pgbProgresso.setString("Verificando setups...");

                    File setups[] = new File("Arquivos/setups").listFiles();
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

                    pgbProgresso.setString("Criando negócios...");
                    pgbProgresso.setIndeterminate(true);

                    
                    pgbProgresso.setString("Fazendo as coisas acontecerem...");
                    tocar = new Tocar();
                    
                    pgbProgresso.setString("Verificando atualizações...");
                    selecionarTexto();
                    mudatexto();

                    pgbProgresso.setString("Abrindo o Ambientes...");
                    Thread.sleep(2000);

                    cair();

                    
                } catch (Exception ex) {
                    ex.printStackTrace();
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

            sistema.Info.versaoReal = Double.parseDouble(ar[0]);
            if (sistema.Info.versaoReal > sistema.Info.VERSAO_ATUAL) {
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

            Random random = new Random();
            escolhido = ar[random.nextInt(ar.length - 1) + 1];
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

                    tocar.setVisible(true);

                    if (sistema.Info.animacaoIntroducao == true) { //dps vejo como eu faco isso sem repetir o if to mt cansado desculpa
                        tocar.subir();
                    }

                    dispose();
                    System.gc();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "TAD").start();
    }

    public int calcularTamanho(int tamDefault, String texto) {
        String div[] = texto.split("");
        String minus = "abcdefghijklmnopqrstuvwxyzáéíóúäëïöüàèìòùâêîôûãõ";
        String maius = minus.toUpperCase();
        String outro = "! .,;?\"@#$%&*_+-=/><'1234567890ýçÇÝ[](){}äëïöü|;";

        float resultado = 0f;

        for (int i = 0; i < div.length; i++) {
            for (int j = 0; j < minus.length(); j++) {
                if (div[i].contains(minus.split("")[j])) {
                    resultado += 0.9f;
                } else if (div[i].contains(maius.split("")[j])) {
                    resultado += 1f;
                } else if (div[i].contains(outro.split("")[j])) {
                    resultado += 0.6f;
                }
            }

        }

//        float divisao = new Float(div.length) / resultado;
        int saida = (int) (tamDefault * 10 / resultado);

        if (saida < 10) {
            saida = 10;
        } else if (saida > 100) {
            saida = 100;
        }
        return saida;
    }

    //textos caso a pessoa esteja sem internet
    public String textos[] = {"Agora sem açúcar!", "Lorem Ipsum", "GAY GAY HOMOSSEXUAL GAY", "e n t e r i n g  t h e  p l a z a . . .",
        "Vamo que vamo!", "ENCARE O MC POZES POR 10 HORAS SEM RIR", "Dança gatinho, dança!", "as vozes...", "WOOF WOOF BARK BARK ARF ARF BARK WOOF WOOF GRRRRR",
        "Nunca ouça nada desse programa às 3 da manhã", "Vamos vamos!", "Simbora!!!", "u lkdusfg;ouzfgou", "MIAU!!!!!", "Divirta-se", "Que belos sons!", "Aviso de pancadas de chuva!",
        "so mim umilhao e mim, maltratao", "Meia noite eu te conto", "O próprio!", "ABCW", "Só nos sonszinhos!!!", "Bem na hora!", "eles disseram, \"O ambiente.\"", "biribabum",
        "Agora sem o Delay de Libet!", "Começando o dia bem", "Uiui", "Experimente CHOCOLATE™"};

    public void mudatexto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String caracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!;.,";
                    Random random = new Random();

                    String saida;
                    if (escolhido.equals("")) {
                        escolhido = textos[random.nextInt(textos.length)];
                    }

//                    int div = escolhido.length() / 10;
//                    if (div <= 0) {
//                        div = 1;
//                    }
                    Font fonte = new Font("Gadugi", Font.BOLD, calcularTamanho(40, escolhido));

                    lblNegocio.setFont(fonte);

                    for (int i = 0; i < 5; i++) {
                        saida = "";
                        for (int j = 0; j < escolhido.length() / 1.5; j++) {
                            saida += caracteres.split("")[random.nextInt(caracteres.length())];
                        }
                        lblNegocio.setText(saida);
                        Thread.sleep(40);
                    }

                    lblNegocio.setText(escolhido);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "MUDATEXTO").start();
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
        lblNegocio = new javax.swing.JLabel();
        pgbProgresso = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/ambientes logo.gif"))); // NOI18N

        lblNegocio.setFont(new java.awt.Font("Gadugi", 1, 24)); // NOI18N
        lblNegocio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNegocio.setText("O programa está iniciando!");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(pgbProgresso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
