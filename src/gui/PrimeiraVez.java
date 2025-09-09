package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import sistema.Componentes;
import sistema.Easings;
import sistema.Info;

/**
 *
 * @author Bonn
 */
public class PrimeiraVez extends javax.swing.JFrame {

    public boolean setaPararDeMexer = false;

    public PrimeiraVez() {
        initComponents();
        Point pos = this.getLocation();
        this.setOpacity(0);
        FlatDarkLaf.install();
        this.setLocation(pos.x, Toolkit.getDefaultToolkit().getScreenSize().height);
        this.setOpacity(1);
        Componentes.moverJanela(this, pos.x, pos.y, 0.005, sistema.Easings.EASE_OUT_CIRC);
        funcionar();
    }

    public void funcionar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);

                    Componentes.resizeJanela(PrimeiraVez.this, 500, 200, 0.005, sistema.Easings.EASE_IN_OUT_EXPO);
                    Componentes.mudarTexto("Parece que essa é a sua primeira vez aqui", lblTexto, 50);
                    Thread.sleep(3000);
                    Componentes.mudarTexto("Vamos aos básicos", lblTexto, 50);
                    Thread.sleep(2500);

                    Info.maximo = 127;

                    Tocar tocar = new Tocar(true);
                    tocar.setVisible(true);
                    tocar.contagem = 127;
                    Thread.sleep(700);
                    sistema.Componentes.moverJanela(tocar, tocar.getLocation().x - 200, tocar.getLocation().y, 0.005, sistema.Easings.EASE_OUT_QUART);
                    sistema.Componentes.moverJanela(PrimeiraVez.this, getLocation().x + 200, getLocation().y, 0.005, sistema.Easings.EASE_OUT_QUART);

                    Componentes.mudarTexto("Essa é a sua janela principal", lblTexto, 50);
                    Thread.sleep(3000);

                    JFrame seta = new JFrame();
                    seta.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                    seta.setUndecorated(true);
                    seta.setLocationRelativeTo(null);
                    seta.setBackground(new Color(0, 0, 0, 0));
                    JLabel imagem = new JLabel();
                    imagem.setText("→");
                    imagem.setFont(new Font("Consolas", 0, 150));
                    imagem.setHorizontalAlignment(SwingConstants.CENTER);
                    imagem.setBounds(0, 0, 200, 200);
                    seta.add(imagem);
                    seta.pack();
                    seta.setVisible(true);

                    seta.setLocation(tocar.getLocation().x - 120, tocar.getLocation().y + 13);
                    mexer(seta);

                    Componentes.mudarTexto("Aqui você seleciona algum setup que você tiver para ouvir", lblTexto, 50);
                    Thread.sleep(3000);
                    pararMexerEEsperar();

                    Componentes.moverJanela(seta, tocar.getLocation().x + 80, tocar.getLocation().y + 13, 0.005, Easings.EASE_IN_OUT_CIRC);
                    Thread.sleep(1000);
                    mexer(seta);

                    Componentes.mudarTexto("Aqui você pode configurar algum setup do jeito que preferir", lblTexto, 50);
                    Thread.sleep(4000);
                    pararMexerEEsperar();

                    Componentes.moverJanela(seta, tocar.getLocation().x - 50, tocar.getLocation().y + 136, 0.005, Easings.EASE_IN_OUT_CIRC);
                    Thread.sleep(1000);
                    mexer(seta);

                    Componentes.mudarTexto("Caso você escute alguma sobreposição ou silêncio no som, mude o predelay nas configurações", lblTexto, 50);
                    Thread.sleep(4000);
                    Componentes.mudarTexto("Você pode configurar e personalizar coisas extras também aqui!", lblTexto, 50);
                    Thread.sleep(3000);
                    pararMexerEEsperar();
                    Componentes.moverJanela(seta, seta.getLocation().x, seta.getLocation().y + java.awt.Toolkit.getDefaultToolkit().getScreenSize().height, 0.004, Easings.EASE_IN_QUART);

                    Componentes.mudarTexto("É isso. Divirta-se!", lblTexto, 50);
                    Thread.sleep(3000);
                    seta.dispose();

                    sistema.Componentes.moverJanela(tocar, tocar.getLocation().x + 200, tocar.getLocation().y, 0.004, sistema.Easings.EASE_IN_OUT_CIRC);

                    Componentes.moverJanela(PrimeiraVez.this, getLocation().x, getLocation().y + java.awt.Toolkit.getDefaultToolkit().getScreenSize().height, 0.004, Easings.EASE_IN_QUART);

                    Thread.sleep(3000);
                    Info.primeiraVez = false;
                    Info.maximo = 10;
                    sistema.Configs.salvar();
                    
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "Thread").start();
    }

    void mexer(JFrame janela) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (setaPararDeMexer == false) {
                        sistema.Componentes.moverJanela(janela, janela.getLocation().x + 50, janela.getLocation().y, 0.008, sistema.Easings.EASE_IN_OUT_CIRC);
                        Thread.sleep(600);
                        sistema.Componentes.moverJanela(janela, janela.getLocation().x - 50, janela.getLocation().y, 0.008, sistema.Easings.EASE_IN_OUT_CIRC);
                        Thread.sleep(600);
                    }
                } catch (Exception ex) {
                }
            }
        }, "Thread").start();
    }

    void pararMexerEEsperar() {
        try {
            setaPararDeMexer = true;
            Thread.sleep(1500);
            setaPararDeMexer = false;
        } catch (Exception ex) {
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

        jPanel1 = new javax.swing.JPanel();
        lblTexto = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        lblTexto.setFont(new java.awt.Font("Open Sauce Sans Black", 1, 70)); // NOI18N
        lblTexto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTexto.setText("Olá!");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTexto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTexto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            java.util.logging.Logger.getLogger(PrimeiraVez.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrimeiraVez.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrimeiraVez.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrimeiraVez.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrimeiraVez().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTexto;
    // End of variables declaration//GEN-END:variables
}
