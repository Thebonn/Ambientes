package sistema.especiais;

import java.awt.Color;
import javax.swing.ImageIcon;

/**
 *
 * @author Bonn
 */
public class SetupLoja {
    public String id = "";
    public String nome = "";
    public String descricao = "";
    public String tamanho = "";
    public String autor = "";
    public ImageIcon imagem;
    
    public boolean temCoresCustomizadas = false;
    public boolean usaListaDeCores = false;
    public Color cores[];
    public int tipoAnimacao = 0;  //se não houver cores
}
