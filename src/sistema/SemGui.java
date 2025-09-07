package sistema;

import java.util.Scanner;

/**
 *
 * @author Bonn
 */
public class SemGui {

    String divisor = "-------------------------------------";

    String textoDeAjuda = """
                          -t / -tocar <Nome do setup> .............. Toca um setup que esá na pasta "setups".
                          -p / -parar .............................. Para o setup que estava tocando no momento.
                          -v / -vol / -volume <float de 0 até 100> . Muda o volume para o valor inserido.
                          -s / -setup .............................. Mostra a configuração do setup atual.
                          -o / -opcoes <Nome do som> ............... Alterna entre ligar e desligar o som.
                          """;

    public void principal() {
        System.out.println("Bem-vindo ao ambientes sem interface!");
        System.out.println(divisor);
        String saida;
        String extra = "";
        try {
            System.out.println("Conectando...");
            saida = sistema.Conectar.pegarSimples("https://pastebin.com/raw/jwdN4yRp").replace("\r", ""); //caractere maligno

            String tmp[] = saida.split("\n");

            System.out.println("= " + tmp[new java.util.Random().nextInt(tmp.length - 1) + 1] + " =");
            sistema.Info.versaoReal = Double.parseDouble(tmp[0]);

            if (sistema.Info.versaoReal > sistema.Info.VERSAO_ATUAL) {
                System.out.println("Há uma atualização disponível para ser instalada! Visite https://github.com/Thebonn/Ambientes para instalar a versão nova");
                extra = "(Versão nova: " + sistema.Info.versaoReal + ")";
            }

        } catch (Exception ex) {
            System.out.println("Não foi possível se conectar à internet.");
        }

        System.out.println("Você está na versão " + sistema.Info.VERSAO_ATUAL + ". " + extra);
        System.out.println(divisor + "\nDigite -h ou -a para ajuda");

        GerenciadorDeSom gerenciador = new GerenciadorDeSom();

        while (true) {

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String div[] = input.split(" ");

            switch (div[0]) {
                case "-a":
                case "-h":
                    System.out.println(textoDeAjuda);
                    break;

                case "-t":
                case "-tocar":

                    if (div.length > 2) {
                        String nome = "";
                        for (int i = 1; i < div.length; i++) {
                            nome += div[i] + " ";
                        }
                        nome = nome.substring(0, nome.length() - 1);
                        try {
                            gerenciador.carregarERodarSetup(nome);
                        } catch (Exception ex) {
                            System.out.println("Houve um erro ao tocar o setup \"" + nome +"\".");
                            System.out.println("   " + ex.toString());
                            System.out.println("      " + ex.getMessage());
                        }

                    } else {
                        System.out.println("Comando malformado");
                    }
                    
                    break;

                default:
                    System.out.println("Comando não reconhecido");
                    break;
            }
        }
    }
}
