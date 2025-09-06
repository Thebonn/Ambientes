/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

import com.k33ptoo.components.KGradientPanel;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Thebonn
 */
public class Sistema {

    public Thread acenderThread = new Thread();
    public double ratio = 0;

    public static Color[] converterCor(String cores) {
        Color saida[] = new Color[20];
        String ar[] = cores.split(", ");

        for (int i = 0; i < ar.length; i++) {

            if (i < saida.length) {
                if (ar[i] == null || ar[i].equals("")) {
                    ar[i] = "000000";
                }
                saida[i] = new Color(Integer.parseInt(ar[i], 16));
            }
        }

        return saida;
    }

    public void escurecerFundo(KGradientPanel painel) {
        Color cor1escurecido = painel.getkStartColor();
        Color cor2escurecido = painel.getkEndColor();

        for (int i = 0; i < sistema.Info.escurecerFundo / 9; i++) {
            cor1escurecido = cor1escurecido.darker();
            cor2escurecido = cor2escurecido.darker();
        }

        painel.setkStartColor(cor1escurecido);
        painel.setkEndColor(cor2escurecido);
    }

    public void descompactar(File arquivo, File diretorio) {
        try {
            if (!diretorio.exists()) {
                diretorio.mkdir();
            }

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(arquivo));
            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {
                System.out.println("descompactando: " + entry.getName());
                String caminho = diretorio.getPath() + File.separator + entry.getName();

                if (!entry.isDirectory()) {
                    FileOutputStream out = new FileOutputStream(new File(caminho));
                    int tam;
                    while ((tam = zis.read(buffer)) > 0) {
                        out.write(buffer, 0, tam);
                    }
                    out.close();
                } else {
                    File dir = new File(caminho);
                    dir.mkdir();
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void acender(Color corInicial, Color corFinal, KGradientPanel painel, float quantidade, int tempo) {
        acenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Color inicio = corInicial;
                Color fim = corFinal;

                painel.setkStartColor(inicio);
                painel.setkEndColor(processarCor(inicio, sistema.Info.intensidade));

                try {
                    boolean acabou = false;
                    ratio = 0;
                    while (acabou == false) {

                        int red = (int) Math.abs((ratio * inicio.getRed()) + ((1 - ratio) * fim.getRed()));
                        int green = (int) Math.abs((ratio * inicio.getGreen()) + ((1 - ratio) * fim.getGreen()));
                        int blue = (int) Math.abs((ratio * inicio.getBlue()) + ((1 - ratio) * fim.getBlue()));

                        Color ultimo = new Color(red, green, blue);

                        painel.setkStartColor(ultimo);
                        painel.setkEndColor(processarCor(ultimo, sistema.Info.intensidade));

                        escurecerFundo(painel);

                        painel.updateUI();

                        Thread.sleep(tempo);
                        ratio += quantidade;
                        //System.out.println(ratio);
                        if (ratio >= 1) {
                            acabou = true;
                            Thread.currentThread().interrupt();
                            //    System.out.println("a");
                        }

                    }

                    //if (eu == false) {
//                    acendidos[num - 1]--;
                    //}
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //}
                //}, "TAD").start();

                Thread.currentThread().interrupt();
            }
        }, "TAD");
        acenderThread.start();
    }

    public Color processarCor(Color corOriginal, float intensidade) {
        float hsb[] = Color.RGBtoHSB(corOriginal.getRed(), corOriginal.getGreen(), corOriginal.getBlue(), null);
        switch (sistema.Info.tipo) {
            case 0:
                return corOriginal.darker();
            case 1:
                return corOriginal.brighter();
            case 2:
                return new Color(Color.HSBtoRGB(hsb[0] - 0.5f, hsb[1], hsb[2]));
            case 3:
                return new Color(Color.HSBtoRGB(hsb[0] + 0.1f * intensidade, hsb[1], hsb[2]));
            case 4:
                return new Color(Color.HSBtoRGB(hsb[0] - 0.1f * intensidade, hsb[1], hsb[2]));
            case 5:
                return Color.white;
            case 6:
                return Color.black;
            default:
                return corOriginal;
        }
    }
    
    int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max + 1 - min) + max;
    }
    
    public Color[] escolherCor() {
        Random random = new Random();
        float valor = random.nextInt(100) / 100f;
        
//        System.out.println(valor);
        
        Color cor1 = new Color(Color.HSBtoRGB(valor, 0.3f + (random(-10, 10) / 100f), 0.8f + (random(-2, 2) / 100f)));
        
        float hsb[] = Color.RGBtoHSB(cor1.getRed(), cor1.getGreen(), cor1.getBlue(), null);
        
        float deslocamento = random(-4, 4) / 100f;
        
//        System.out.println(deslocamento);
        
        Color cor2 = new Color(Color.HSBtoRGB(hsb[0] + deslocamento, hsb[1], hsb[2] + (random(-5, 5) / 100f)));
        Color[] saida = {cor1, cor2};
        return saida;
    }
    
    //movido para conectar > pegarTextoAvancado/pegarTextoSimples

//    public String pegarTexto(String link) throws Exception {
//        String tempDir = System.getProperty("java.io.tmpdir");
////        try {
//        URL url = new URL(link);
//        InputStream inputStream = (url).openStream();
//        Files.copy(inputStream, Paths.get(String.format("%s%s%s", new Object[]{tempDir, File.separator, "cone.txt"}), new String[0]), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
//
//        String path = String.format("%s%s%s", new Object[]{tempDir, File.separator, "cone.txt"});
//        File arquivo = new File(path);
//
//        BufferedReader ler = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo.toString()), "UTF-8"));
//
//        String saida = ler.readLine();
//        String volta = "";
//
//        while (saida != null) {
//            volta += saida + "\n";
//            saida = ler.readLine();
//        }
//        volta = volta.substring(0, volta.length() - 1);
//        ler.close();
//        arquivo.delete();
//        return volta;
//
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////        return null;
//    }

}
