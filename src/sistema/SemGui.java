package sistema;

import java.io.File;
import java.util.Scanner;

/**
 *
 * @author Bonn
 */
public class SemGui {

    String divisor = "-------------------------------------";

    String textoDeAjuda = """
                          Utilização: <COMANDO> <ARGUMENTO(S)>
                          -l / --lista ............................... Mostra a lista de setups instalados.
                          -t / --tocar <Nome do setup> ............... Toca um setup que esá na pasta "setups".
                          -p / --parar ............................... Para o setup que estava tocando no momento.
                          -v / -volume <float de 0 até 100> .. Muda o volume para o valor inserido.
                          -s / --setup ............................... Mostra a configuração do setup atual.
                          -o / --opcoes <Nome do som> ................ Alterna entre ligar e desligar o som.
                          -lj / --loja .............. ................ Abre a loja de setups (Com GUI).
                          """;

    public void principal() {

        System.out.println(sistema.Info.ABERTURAS[sistema.Generico.random(0, sistema.Info.ABERTURAS.length)] + "\n");
        System.out.println("Bem-vindo ao ambientes sem interface!");
        System.out.println(divisor);

        File configs = new File("Arquivos/configuracoes.txt");
        File padrao = new File(sistema.Info.localSetups);

        if (configs.exists()) {
            try {
                sistema.Configs.carregar();
            } catch (Exception ex) {
                System.out.println("Não foi possível carregar as configurações.");
                System.out.println("   " + ex.toString());
                System.out.println("      " + ex.getMessage());
            }
        } else {
            System.out.println("ATENÇÃO: Nenhum arquivo de configurações foi encontrado. Setups serão instalados no diretório padrão (" + Info.localSetups + ").");
        }

        if (!padrao.exists()) {
            System.out.println("ATENÇÃO: Pasta de setups instalados não foi encontrada. Caso deseja criar, use o comando --criardirs");
            System.out.println("(Sem a pasta, não será possível instalar setups novos)");
        }

        String saida;
        String extra = "";
        try {
            System.out.println("Conectando...");
            saida = sistema.Conectar.pegarSimples("https://pastebin.com/raw/jwdN4yRp").replace("\r", ""); //caractere maligno

            String tmp[] = saida.split("\n");

            System.out.println("= " + tmp[new java.util.Random().nextInt(tmp.length - 1) + 1] + " =");
            sistema.Info._versaoReal = Double.parseDouble(tmp[0]);

            if (sistema.Info._versaoReal > sistema.Info.VERSAO_ATUAL) {
                System.out.println("Há uma atualização disponível para ser instalada! Visite https://github.com/Thebonn/Ambientes para instalar a versão nova");
                extra = "(Versão nova: " + sistema.Info._versaoReal + ")";
            }

        } catch (Exception ex) {
            System.out.println("Não foi possível se conectar à internet.");
        }

        System.out.println("Você está na versão " + sistema.Info.VERSAO_ATUAL + ". " + extra);
        System.out.println(divisor + "\nDigite --ajuda ou --help para ajuda");

        GerenciadorDeSom gerenciador = new GerenciadorDeSom();

        while (true) {

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String div[] = input.split(" ");

            if (div[0].startsWith("-")) {
                switch (div[0].replace("-", "")) {
                    case "ajuda":
                    case "help":
                    case "a":
                    case "h":
                        System.out.println(textoDeAjuda);
                        break;

                    case "t":
                    case "tocar":

                        if (div.length >= 2) {
                            if (gerenciador.tocando) {
                                System.out.println("Um setup já está tocando. Tente parar usando -p.");
                                break;
                            }
                            String nome = "";
                            for (int i = 1; i < div.length; i++) {
                                nome += div[i] + " ";
                            }
                            nome = nome.substring(0, nome.length() - 1);
                            try {
                                gerenciador.carregarERodarSetup(nome);
                                System.out.println("Tocando agora: " + nome);
                            } catch (Exception ex) {
                                System.out.println("Houve um erro ao tocar o setup \"" + nome + "\".");
                                System.out.println("   " + ex.toString());
                                System.out.println("      " + ex.getMessage());
                            }

                        } else {
                            System.out.println("Comando malformado.");
                        }

                        break;
                    case "l":
                    case "lista":
                        File file = new File("Arquivos/setups");
                        String setups[] = file.list();
                        System.out.println("Listando os seus setups...");
                        boolean temErro = false;
                        for (int i = 0; i < setups.length; i++) {
                            File lugar = new File(file.getAbsolutePath() + "/" + setups[i]);
                            if (lugar.exists()) {
                                if (new File(lugar.getAbsolutePath() + "/mestre.txt").exists()) {
                                    System.out.println("* " + setups[i]);
                                } else {
                                    temErro = true;
                                    System.out.println("* " + setups[i] + " (!)");
                                }
                            }
                        }

                        if (temErro) {
                            System.out.println("(Setups com ! são setups que não tem um arquivo mestre.txt)");
                        }

                        break;

                    case "p":
                    case "parar":
                        if (gerenciador.tocando) {
                            gerenciador.pararTudo();
                            System.out.println("Setup parado.");
                        } else {
                            System.out.println("Não há nada tocando no momento.");
                        }
                        break;

                    case "v":
                    case "vol":
                    case "volume":
                        if (div.length == 2) {
                            try {
                                Info.volumeGlobal = Float.parseFloat(div[1]);

                                if (Info.volumeGlobal > 100) {
                                    System.out.println("Volume excedido, arredondando para 100...");
                                    Info.volumeGlobal = 100f;
                                } else if (Info.volumeGlobal < 0) {
                                    System.out.println("Volume não pode ser menor que 0, arredondando para 0...");
                                    Info.volumeGlobal = 0f;
                                }

                                gerenciador.setarVolune(Info.volumeGlobal);
                                System.out.println("Volume alterado para " + Info.volumeGlobal);
                            } catch (Exception ex) {
                                System.out.println("Não foi possível alterar o volume para " + div[1]);
                            }

                        }
                        break;
                    case "s":
                    case "setup":
                        if (gerenciador.config == null) {
                            System.out.println("Você não carregou nenhum setup para exibir a configuração do setup.");
                        } else {
                            System.out.println("Listando a configuração do setup...");
                            for (int i = 0; i < gerenciador.config.length; i++) {
                                System.out.println(gerenciador.config[i]);
                            }
                        }

                        break;

                    case "lj":
                    case "loja":
                        System.out.println("Abrindo a loja de setups...");
                        new gui.Loja().setVisible(true);
                        break;

                    case "criardirs":
                        if (padrao.exists()) {
                            System.out.println("O diretório já existe.");
                        } else {
                            System.out.println("Criando diretórios...");
                            padrao.mkdirs();
                            System.out.println("Diretórios criados!");
                        }
                        break;

                    case "ml":

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Estresse de memory leak iniciado...");
                                while (true) {
                                    try {
                                        System.out.println("Carregado");
                                        gerenciador.carregarERodarSetup("ps2");
                                        Thread.sleep(5000);
                                        System.out.println("Parado");
                                        gerenciador.pararTudo();
                                        Thread.sleep(5000);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }
                            }
                        }, "ESTRESSE DE MEMORY LEAK").start();

                        break;
                    default:
                        System.out.println("-" + div[0] + " não é um comando reconhecido.");
                        break;
                }
            }
            System.out.println(divisor);
        }
    }
}
