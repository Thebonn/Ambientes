package sistema;

import gui.Tocar;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author Bonn
 */
public class SemGui {

    boolean suportaAnsi = true;
    boolean usarAnsi = true;

    String divisor = "-------------------------------------";
    String textoDeAjuda = """
                          Utilização: <COMANDO> <ARGUMENTO(S)>/[ARGUMENTO(S)
                                <> Indica argumento obrigatório
                                [] Indica argumento facultativo
                          
                            -l / --lista ............................... Mostra a lista de setups instalados.
                            -t / --tocar <Nome do setup> ............... Toca um setup que esá na pasta "setups".
                            -p / --parar ............................... Para o setup que estava tocando no momento.
                            -v / --volume [Float de 0 até 100] ......... Exibe o volume atual.
                                Com argumento: Muda o volume para o valor inserido.
                            -s / --setup ............................... Mostra a configuração do setup atual.
                            -o / --opções [Nome do som] ................ Exibe os sons do setup.
                                Com argumento: Alterna entre ligar e desligar o som.
                            -lj / --loja ............................... Abre a loja de setups (Com GUI).
                            -i / --info ................................ Mostra informações técnicas/debug.
                            -f / --finalizar ........................... Finaliza essa sessão.
                          """;

    public void principal() {

        suportaAnsi = System.getenv().get("TERM") != null;

        if (!suportaAnsi) {
            System.out.println("ATENÇÃO: Não foi detectado suporte para códigos ANSI no seu sistema, isso quer dizer\n"
                    + "que não será exibido cores nesse console e que poderá aparecer caracteres\n"
                    + "extras no meio de frases. Você pode desativar usando o comando --ansi se isso for o caso.");
        }

        pl(sistema.Info.ABERTURAS[sistema.Generico.random(0, sistema.Info.ABERTURAS.length)], VERDE);
        p("Bem-vindo ao ambientes sem interface!");
        p(divisor);

        File configs = new File("Arquivos/configuracoes.txt");
        File padrao = new File(sistema.Info.localSetups);

        if (configs.exists()) {
            try {
                sistema.Configs.carregar();
            } catch (Exception ex) {
                p("Não foi possível carregar as configurações.");
                p("   " + ex.toString());
                p("      " + ex.getMessage());
            }
        } else {
            p(AMARELO + "ATENÇÃO: Nenhum arquivo de configurações foi encontrado. Setups serão instalados no diretório padrão (" + Info.localSetups + ").");
        }

        if (!padrao.exists()) {
            p(AMARELO + "ATENÇÃO: Pasta de setups instalados não foi encontrada. Caso deseja criar, use o comando --criardirs");
            p("\t(Sem a pasta, não será possível instalar setups novos)");
        }

        String saida;
        String extra = "";
        try {
            p("Conectando...");
            saida = sistema.Conectar.pegarSimples("https://pastebin.com/raw/jwdN4yRp").replace("\r", ""); //caractere maligno

            String tmp[] = saida.split("\n");

            p(REINICIAR + "= " + VERDE + tmp[new java.util.Random().nextInt(tmp.length - 1) + 1] + REINICIAR + " =");
            sistema.Info._versaoReal = Double.parseDouble(tmp[0]);

            if (sistema.Info._versaoReal > sistema.Info.VERSAO_ATUAL) {
                p("Há uma atualização disponível! Visite https://github.com/Thebonn/Ambientes para instalar a versão nova");
                extra = "(Versão nova: " + sistema.Info._versaoReal + ")";
            }

        } catch (Exception ex) {
            p(VERMELHO + "Não foi possível se conectar à internet.");
            p("   " + ex.toString());
            p("      " + ex.getMessage());
        }

        p("Você está na versão " + sistema.Info.VERSAO_ATUAL + ". " + extra);
        p(divisor + "\nDigite --ajuda ou --help para ajuda");

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
                        p(textoDeAjuda);
                        break;

                    case "t":
                    case "tocar":

                        if (div.length >= 2) {
                            if (gerenciador.tocando) {
                                p("Um setup já está tocando. Tente parar usando -p.");
                                break;
                            }
                            String nome = formatarArgumento(div);
                            try {
                                gerenciador.carregarERodarSetup(nome);
                                p("Tocando agora: " + nome);
                            } catch (Exception ex) {
                                p(VERMELHO + "Houve um erro ao tocar o setup \"" + nome + "\".");
                                p("   " + ex.toString());
                                p("      " + ex.getMessage());
                            }

                        } else {
                            p("Comando malformado.");
                        }

                        break;
                    case "l":
                    case "lista":

                        if (!padrao.exists()) {
                            System.out.println("A pasta de setups não foi encontrada. Você pode criar a pasta usando o comando --criardirs.");
                            break;
                        }

                        String setups[] = padrao.list();

                        p("Listando os seus setups...");

                        if (setups.length == 0) {
                            p("Você não tem nenhum setup instalado! Instale usando o comando -lj para abrir a loja.");
                        } else {
                            boolean temErro = false;
                            for (int i = 0; i < setups.length; i++) {
                                File lugar = new File(padrao.getAbsolutePath() + "/" + setups[i]);
                                if (lugar.exists()) {
                                    if (new File(lugar.getAbsolutePath() + "/mestre.txt").exists()) {
                                        p("* " + setups[i]);
                                    } else {
                                        temErro = true;
                                        p("* " + setups[i] + " (!)");
                                    }
                                }
                            }

                            if (temErro) {
                                p("(Setups com ! são setups que não tem um arquivo mestre.txt)");
                            }
                        }
                        break;

                    case "p":
                    case "parar":
                        if (gerenciador.tocando) {
                            gerenciador.pararTudo();
                            p("Setup parado.");
                        } else {
                            p("Não há nada tocando no momento.");
                        }
                        break;
                    case "o":
                    case "opções":
                    case "opcões":
                    case "opcoes":
                        if (div.length == 1) {
                            if (gerenciador.config == null) {
                                p("Você não carregou nenhum setup para exibir a configuração do setup.");
                            } else {
                                p("Listando os sons do setup...");
                                for (int i = 0; i < gerenciador.config.length; i++) {
                                    if (!gerenciador.config[i].startsWith("p - ") && !gerenciador.config[i].startsWith("c - ") && !gerenciador.config[i].startsWith("#")) {
                                        if (Tocar.desativados.contains(gerenciador.config[i].split(", ")[0])) {
                                            p(gerenciador.config[i] + " [DESATIVADO]");
                                        } else {
                                            p(gerenciador.config[i]);
                                        }
                                    }
                                }
                            }
                        } else if (div.length > 1) {
                            String nome = formatarArgumento(div);

                            if (Tocar.desativados.contains(nome)) {
                                Tocar.desativados.remove(nome);
                                p(nome + " foi reativado com sucesso!");
                            } else {
                                Tocar.desativados.add(nome);
                                p(nome + " foi desativado com sucesso!");
                            }

                        }

                        break;

                    case "v":
                    case "vol":
                    case "volume":
                        if (div.length == 2) {
                            try {
                                Info.volumeGlobal = Float.parseFloat(div[1]);

                                if (Info.volumeGlobal > 100) {
                                    p("Volume excedido, arredondando para 100...");
                                    Info.volumeGlobal = 100f;
                                } else if (Info.volumeGlobal < 0) {
                                    p("Volume não pode ser menor que 0, arredondando para 0...");
                                    Info.volumeGlobal = 0f;
                                }

                                gerenciador.setarVolune(Info.volumeGlobal);
                                p("Volume alterado para " + Info.volumeGlobal);
                            } catch (Exception ex) {
                                p("Não foi possível alterar o volume para " + div[1]);
                            }

                        }
                        break;
                    case "s":
                    case "setup":
                        if (gerenciador.config == null) {
                            p("Você não carregou nenhum setup para exibir a configuração do setup.");
                        } else {
                            p("Listando a configuração do setup...");
                            for (int i = 0; i < gerenciador.config.length; i++) {
                                p(gerenciador.config[i]);
                            }
                        }

                        break;

                    case "lj":
                    case "loja":
                        if (!padrao.exists()) {
                            p("Lembrando que a pasta de setups instalados não foi encontrada.");
                            p("Um erro ocorrerá se você tentar instalar algo sem ela.");
                            p("Rode o comando --criardirs para criar as pastas necessárias.");
                        }

                        p("Abrindo a loja de setups...");
                        new gui.Loja().setVisible(true);
                        break;

                    case "f":
                    case "finalizar":
                        p("Finalizando essa sessão...");
                        p(VERDE + "Até depois!");
                        System.exit(0);
                        break;

                    case "i":
                    case "info":
                        p("Exibindo informações técnicas...");

                        p("Infos gerais");
                        p("\tSistema Operacional: " + System.getProperty("os.name"));
                        p("\tNome do runtime: " + System.getProperty("java.runtime.name"));
                        p("\tVersão do runtime: " + System.getProperty("java.runtime.version"));
                        p("\tVersão do Java: " + System.getProperty("java.version"));
                        p("\tSistema suporta ícones de bandeja: " + Tocar.suportaSystemTray);
                        p("\nInfos do programa");
                        p("\tSetup: " + gerenciador.setup);
                        p("\tConfig. do setup: " + java.util.Arrays.toString(gerenciador.config));
                        p("\tVolume global: " + Info.volumeGlobal);
                        p("\tDesativados: " + Tocar.desativados.toString());
                        p("\tPode abrir janela de opções: " + Tocar.podeAbrir);
                        p("\tCores legíveis: " + Tocar.coresLegiveis);
                        p("\tPlaylist predelay: " + gerenciador.playlistPredelay);
                        p("\tEspera delay: " + Tocar.esperaDelay);
                        p("\tLoja aberta: " + Tocar.lojaaberta);
                        p("\nConfigurações");
                        p("\tTipo de animação: " + Info.animTipo);
                        p("\tVelocidade: " + Info.velocidade);
                        p("\tPredelay: " + Info.preDelay);
                        p("\tPode colorir: " + Info.podeColorir);
                        p("\tTempo de espera máximo: " + Info.maximo);
                        p("\tTipo de cores especiais: " + Info.tipoCorEspecial);
                        p("\tTempo de atualização: " + Info.atualizacao);
                        p("\tIntensidade da cor secundária: " + Info.intensidade);
                        p("\tValor para escurecer o fundo: " + Info.escurecerFundo);
                        p("\tTipo do RPC: " + Info.rpcTipo);
                        p("\tMostrar setups: " + Info.mostrarSetups);
                        p("\tAnimação de introdução: " + Info.animacaoIntroducao);
                        break;

                    case "criardirs":
                        if (padrao.exists()) {
                            p("O diretório já existe.");
                        } else {
                            p("Criando diretórios...");
                            padrao.mkdirs();
                            p("Diretórios criados!");
                        }
                        break;

                    case "ansi":
                        if (usarAnsi) {
                            p("Os caracteres ANSI serão ignorados a partir de agora.");
                        } else {
                            p("Os caracteres ANSI serão usados a partir de agora.");
                        }

                        usarAnsi = !usarAnsi;

                        break;

                    case "ml":

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                p("Estresse de memory leak iniciado...");
                                while (true) {
                                    try {
                                        p("Carregado");
                                        gerenciador.carregarERodarSetup("ps2");
                                        Thread.sleep(5000);
                                        p("Parado");
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
                        p("-" + div[0] + " não é um comando reconhecido.");
                        break;
                }
            }
            p(divisor);
        }
    }

    void pl(String texto, String COR) {
        if (!usarAnsi) {
            p(texto);
        } else {
            System.out.println(COR + texto.replace("\n", "\n" + COR) + REINICIAR);
        }

    }

    void p(String texto) {
        if (!usarAnsi) {
            texto = texto.replaceAll("\u001B\\[[;\\d]*m", ""); //remover todos os ansi
        }
        System.out.println(texto + REINICIAR);
    }

    public String formatarArgumento(String[] comando) {
        String arg = "";
        for (int i = 1; i < comando.length; i++) {
            arg += comando[i] + " ";
        }
        return arg.substring(0, arg.length() - 1);
    }

    // Reiniciar
    public static final String REINICIAR = "\033[0m";  // Reiniciar o texto

    // Cores
    public static final String PRETO = "\033[0;30m";
    public static final String VERMELHO = "\033[0;31m";
    public static final String VERDE = "\033[0;32m";
    public static final String AMARELO = "\033[0;33m";
    public static final String AZUL = "\033[0;34m";
    public static final String ROXO = "\033[0;35m";
    public static final String CIANO = "\033[0;36m";
    public static final String BRANCO = "\033[0;37m";

    // Negrito
    public static final String PRETO_NEGRITO = "\033[1;30m";
    public static final String VERMELHO_NEGRITO = "\033[1;31m";
    public static final String VERDE_NEGRITO = "\033[1;32m";
    public static final String AMARELO_NEGRITO = "\033[1;33m";
    public static final String AZUL_NEGRITO = "\033[1;34m";
    public static final String ROXO_NEGRITO = "\033[1;35m";
    public static final String CIANO_NEGRITO = "\033[1;36m";
    public static final String BRANCO_NEGRITO = "\033[1;37m";

    // Sublinhado
    public static final String PRETO_SUBLINHADO = "\033[4;30m";
    public static final String VERMELHO_SUBLINHADO = "\033[4;31m";
    public static final String VERDE_SUBLINHADO = "\033[4;32m";
    public static final String AMARELO_SUBLINHADO = "\033[4;33m";
    public static final String AZUL_SUBLINHADO = "\033[4;34m";
    public static final String ROXO_SUBLINHADO = "\033[4;35m";
    public static final String CIANO_SUBLINHADO = "\033[4;36m";
    public static final String BRANCO_SUBLINHADO = "\033[4;37m";
}
