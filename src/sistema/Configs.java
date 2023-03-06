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
import javax.swing.JOptionPane;

/**
 *
 * @author Thebonn
 */
public class Configs {
    File file = new File("Arquivos/configs.txt");
    
    public void carregar() {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader ler = new BufferedReader(fr);
            
            String linha = ler.readLine();
            sistema.Info.animTipo = Byte.parseByte(linha);
            linha = ler.readLine();
            sistema.Info.velocidade = Float.parseFloat(linha);
            linha = ler.readLine();
            sistema.Info.preDelay = Short.parseShort(linha);
            linha = ler.readLine();
            sistema.Info.podeColorir = linha.equals("true");
            linha = ler.readLine();
            sistema.Info.maximo = Byte.parseByte(linha);
            linha = ler.readLine();
            sistema.Info.tipo = Byte.parseByte(linha);
            linha = ler.readLine();
            sistema.Info.atualizacao = Short.parseShort(linha);
            linha = ler.readLine();
            sistema.Info.intensidade = Float.parseFloat(linha);
            linha = ler.readLine();
            sistema.Info.escurecerFundo = Byte.parseByte(linha);
            linha = ler.readLine();
            sistema.Info.rpcTipo = Byte.parseByte(linha);
            linha = ler.readLine();
            sistema.Info.mostrarSetups = linha.equals("true");
            linha = ler.readLine();
            sistema.Info.animacaoIntroducao = linha.equals("true");
            linha = ler.readLine();
            sistema.Info.iconeInterativo = linha.equals("true");
            
            ler.close();
            fr.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showConfirmDialog(null, "Houve um erro ao carregar um atributo nas suas configurações. Talvez o arquivo esteja corrompido. O programa irá gerar um arquivo de configurações com a informação ausente com o valor padrão.", "Ambientes", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            salvar();
        }
        
    }
    
    public void salvar() {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter esc = new BufferedWriter(fw);
            
            esc.write(sistema.Info.animTipo + "\n");
            esc.write(sistema.Info.velocidade + "\n");
            esc.write(sistema.Info.preDelay + "\n");
            esc.write(Boolean.toString(sistema.Info.podeColorir) + "\n");
            esc.write((int) (sistema.Info.maximo - 1) + "\n");
            esc.write(sistema.Info.tipo + "\n");
            esc.write(sistema.Info.atualizacao + "\n");
            esc.write(sistema.Info.intensidade + "\n");
            esc.write(sistema.Info.escurecerFundo + "\n");
            esc.write(sistema.Info.rpcTipo + "\n");
            esc.write(Boolean.toString(sistema.Info.mostrarSetups) + "\n");
            esc.write(Boolean.toString(sistema.Info.animacaoIntroducao) + "\n");
            esc.write(Boolean.toString(sistema.Info.iconeInterativo) + "\n");
            esc.close();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
