/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

/**
 *
 * @author Bonn
 */
public class Info {
    //valores estáticos e com _ no inicio não serão escritas no arquivo de configuração
    
    public static final double VERSAO_ATUAL = 1.2;
    public static double _versaoReal = 0;
    
    public static byte animTipo = 1;
    public static double velocidade = 1;
    public static short preDelay = 30;
    public static boolean podeColorir = true;
    public static byte maximo = 10;
    public static byte tipo = 0;
    public static short atualizacao = 800;
    public static float intensidade = 1f;
    public static byte escurecerFundo = 0;
    public static byte rpcTipo = 0; //0 = mostra tudo, 1 = mostra pouco, 2 = nao mostra
    public static boolean mostrarSetups = true;
    public static boolean animacaoIntroducao = true;
    public static boolean iconeInterativo = true;
    
    public static float volumeGlobal = 90;
    
    public static final String FILTRO = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
}
