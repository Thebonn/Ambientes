/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.util.Arrays;
import javax.swing.JTextArea;
import sistema.Info;

/**
 *
 * @author Thebonn
 */
public class Infos extends javax.swing.JFrame {

    /**
     * Creates new form Infos
     */
    public Infos() {
        FlatDarkLaf.install();
        initComponents();
        this.setTitle("Informações");
        atualizarInfo();
        repetiratt();
    }

    void repetiratt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        txtPlay.setText("");
                        addTexto("Slots de playlists:\n", txtPlay);
                        for (int i = 0; i < Tocar.playlists.length; i++) {
                            if (Tocar.playlists[i] != null) {
                                addTexto("Slot " + (i + 1) + ": " + Tocar.playlists[i].getMicrosecondPosition() + "/" + Tocar.playlists[i].getMicrosecondLength(), txtPlay);
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "TAD").start();
    }

    void addTexto(String texto, JTextArea area) {
        area.setText(area.getText() + texto + "\n");
    }

    String conversao(String[] texto) {
        return Arrays.toString(texto).replace(", null", "").replace("[", "").replace("]", "").replace("null", "");
    }

    public void atualizarInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    txtDetalhes.setText("");
                    addTexto("Infos gerais", txtDetalhes);
                    addTexto("  Sistema Operacional: " + System.getProperty("os.name"), txtDetalhes);
                    addTexto("  Nome do runtime: " + System.getProperty("java.runtime.name"), txtDetalhes);
                    addTexto("  Versão do runtime: " + System.getProperty("java.runtime.version"), txtDetalhes);
                    addTexto("  Versão do java: " + System.getProperty("java.version"), txtDetalhes);
                    addTexto("\nInfos do programa", txtDetalhes);
                    addTexto("  Setup: " + Tocar.setup, txtDetalhes);
                    addTexto("  Config. do setup: " + conversao(Tocar.config), txtDetalhes);
                    addTexto("  Volume global: " + Tocar.volumeGlobal, txtDetalhes);
                    addTexto("  Desativados: " + conversao(Tocar.desativados), txtDetalhes);
                    addTexto("  Pode abrir aba de opções: " + Tocar.podeAbrir, txtDetalhes);
                    addTexto("  Cores legíveis: " + Tocar.coresLegiveis, txtDetalhes);
                    addTexto("  Playlist predelay: " + Tocar.playlistPreDelay, txtDetalhes);
                    addTexto("  Espera delay: " + Tocar.esperaDelay, txtDetalhes);
                    addTexto("  Loja aberta: " + Tocar.lojaaberta, txtDetalhes);
                    addTexto("\nConfigurações", txtDetalhes);
                    addTexto("  Tipo de animação: " + Info.animTipo, txtDetalhes);
                    addTexto("  Velocidade: " + Info.velocidade, txtDetalhes);
                    addTexto("  Predelay: " + Info.preDelay, txtDetalhes);
                    addTexto("  Pode colorir: " + Info.podeColorir, txtDetalhes);
                    addTexto("  Tempo de espera máximo: " + Info.maximo, txtDetalhes);
                    addTexto("  Tipo de cores especiais: " + Info.tipo, txtDetalhes);
                    addTexto("  Tempo de atualização: " + Info.atualizacao, txtDetalhes);
                    addTexto("  Intensidade da cor secundária: " + Info.intensidade, txtDetalhes);
                    addTexto("  Valor para escurecer o fundo: " + Info.escurecerFundo, txtDetalhes);
                    addTexto("  Tipo do RPC: " + Info.rpcTipo, txtDetalhes);
                    addTexto("  Mostrar setups: " + Info.mostrarSetups, txtDetalhes);
                    addTexto("  Animação de introdução: " + Info.animacaoIntroducao, txtDetalhes);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, "AtualizarInfo").start();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDetalhes = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtPlay = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtDetalhes.setEditable(false);
        txtDetalhes.setColumns(20);
        txtDetalhes.setRows(5);
        jScrollPane1.setViewportView(txtDetalhes);

        jButton1.setText("Ok, obrigado");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Atualizar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtPlay.setEditable(false);
        txtPlay.setColumns(20);
        txtPlay.setRows(5);
        txtPlay.setFocusable(false);
        jScrollPane2.setViewportView(txtPlay);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        atualizarInfo();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(Infos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Infos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Infos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Infos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Infos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtDetalhes;
    private javax.swing.JTextArea txtPlay;
    // End of variables declaration//GEN-END:variables
}
