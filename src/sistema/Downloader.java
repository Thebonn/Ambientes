/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;

/**
 *
 * @author Thebonn
 */
public class Downloader implements Runnable {

    String link;
    File out;
    public static double porcentagem;
    public static boolean baixou = false;

    public Downloader(String link, File out) {
        this.link = link;
        this.out = out;
        baixou = false;
    }

    @Override
    public void run() {
        try {
            baixou = false;
            double baixado = 0.00;
            int lido;
            porcentagem = 0.00;
            
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            double tamanho = (double) http.getContentLength();
            BufferedInputStream inp = new BufferedInputStream(http.getInputStream());
            
            FileOutputStream fos = new FileOutputStream(this.out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            
            
            
            
            while ((lido = inp.read(buffer, 0, 1024)) >= 0) {
                baixou = false;
                bout.write(buffer, 0, lido);
                baixado += lido;
                porcentagem = (baixado * 100)/tamanho;
                String p = String.format("%.4f", porcentagem);
                
            }
            baixou = true;
            bout.close();
            inp.close();
            
            
        } catch (IOException ex) {
            int es = JOptionPane.showConfirmDialog(null, "Não foi possível baixar o arquivo. [Erro: " + ex.toString() + "]. Deseja tentar novamente?", "Ambientes", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (es == 0) {
                new Downloader(link, out).run();
            } else {
                baixou = true;
            }
            
            ex.printStackTrace();
        }
    }

}
