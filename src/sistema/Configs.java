/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema;

import gui.Tocar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Thebonn
 */
public class Configs {

    public static File file = new File("Arquivos/configs.txt");

    public static void carregar() throws Exception {
//        try {
        FileReader fr = new FileReader(file);
        BufferedReader ler = new BufferedReader(fr);

        Properties prop = new Properties();
        prop.load(ler);

        sistema.Info.animTipo = Byte.parseByte(prop.getProperty("animTipo"));
        sistema.Info.velocidade = Float.parseFloat(prop.getProperty("velocidade"));
        sistema.Info.preDelay = Short.parseShort(prop.getProperty("preDelay"));
        sistema.Info.podeColorir = prop.getProperty("podeColorir").equalsIgnoreCase("true");
        sistema.Info.maximo = Byte.parseByte(prop.getProperty("maximo"));
        sistema.Info.tipo = Byte.parseByte(prop.getProperty("tipo"));
        sistema.Info.atualizacao = Short.parseShort(prop.getProperty("atualizacao"));
        sistema.Info.intensidade = Float.parseFloat(prop.getProperty("intensidade"));
        sistema.Info.escurecerFundo = Byte.parseByte(prop.getProperty("escurecerFundo"));
        sistema.Info.rpcTipo = Byte.parseByte(prop.getProperty("rpcTipo"));
        sistema.Info.mostrarSetups = prop.getProperty("mostrarSetups").equalsIgnoreCase("true");
        sistema.Info.animacaoIntroducao = prop.getProperty("animacaoIntroducao").equalsIgnoreCase("true");
        sistema.Info.iconeInterativo = prop.getProperty("iconeInterativo").equalsIgnoreCase("true");

        ler.close();
        fr.close();
        salvar();
    }

    public static void salvar() {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter esc = new BufferedWriter(fw);

            esc.write("animTipo: " + sistema.Info.animTipo + "\n");
            esc.write("velocidade: " + sistema.Info.velocidade + "\n");
            esc.write("preDelay: " + sistema.Info.preDelay + "\n");
            esc.write("podeColorir: " + Boolean.toString(sistema.Info.podeColorir) + "\n");
            esc.write("maximo: " + (int) (sistema.Info.maximo - 1) + "\n");
            esc.write("tipo: " + sistema.Info.tipo + "\n");
            esc.write("atualizacao: " + sistema.Info.atualizacao + "\n");
            esc.write("intensidade: " + sistema.Info.intensidade + "\n");
            esc.write("escurecerFundo: " + sistema.Info.escurecerFundo + "\n");
            esc.write("rpcTipo: " + sistema.Info.rpcTipo + "\n");
            esc.write("mostrarSetups: " + Boolean.toString(sistema.Info.mostrarSetups) + "\n");
            esc.write("animacaoIntroducao: " + Boolean.toString(sistema.Info.animacaoIntroducao) + "\n");
            esc.write("iconeInterativo: " + Boolean.toString(sistema.Info.iconeInterativo) + "\n");
            esc.close();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
