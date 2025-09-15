package sistema;

/**
 *
 * @author Bonn
 */
public class Info {
    //valores estáticos e com _ no inicio não serão escritas no arquivo de configuração

    public static final double VERSAO_ATUAL = 1.2;
    public static double _versaoReal = 0;

    public static boolean primeiraVez = true;

    //fundo
    public static byte animTipo = 1;
    public static float velocidade = 1f;
    public static boolean podeColorir = true;
    public static byte maximo = 10;
    public static byte tipoCorEspecial = 0;
    public static byte escurecerFundo = 0;
    public static float intensidade = 1f;
    public static boolean usarCoresDoSetup = true;

    //setups
    public static short atualizacao = 800;
    public static short preDelay = 30;
    public static float volumeGlobal = 90;
    
    //rpc
    public static byte rpcTipo = 0; //0 = mostra tudo, 1 = mostra pouco, 2 = nao mostra
    public static boolean mostrarSetups = true;
    
    //diversos
    public static boolean animacaoIntroducao = true;
    public static boolean iconeInterativo = true;
    public static String localSetups = "Arquivos/setups";
    public static String provedor = "https://de-bonn.netlify.app/arquivos/ambientes";

    

    public static final String FILTRO = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
}
