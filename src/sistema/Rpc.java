package sistema;

import dev.firstdark.rpc.DiscordRpc;
import dev.firstdark.rpc.enums.ActivityType;
import dev.firstdark.rpc.enums.ErrorCode;
import dev.firstdark.rpc.handlers.DiscordEventHandler;
import dev.firstdark.rpc.models.DiscordJoinRequest;
import dev.firstdark.rpc.models.DiscordRichPresence;
import dev.firstdark.rpc.models.User;

/**
 *
 * @author Bonn
 */
public class Rpc {

    public static DiscordRpc rpc;
    public static final String APPLICATION_ID = "990002259469406288";
    public static DiscordEventHandler handler;
    public static DiscordRichPresence presence;

    public static String detalhes = "Parado";
    public static String estado = "";
    public static String chaveGrande = "logo";
    public static String textoGrande = "v1.2";
    public static String chavePequena = "";
    public static String textoPequeno = "";

    //essa biblioteca nova requer uma função que coloca tudo de uma vez, diferente da biblioteca anterior
    public static void atualizarRPC() {

        presence = DiscordRichPresence.builder()
                .details(detalhes)
                .state(estado)
                .largeImageKey(chaveGrande)
                .largeImageText(textoGrande)
                .smallImageKey(chavePequena)
                .smallImageText(textoPequeno)
                .activityType(ActivityType.LISTENING)
                .build();
        rpc.updatePresence(presence);
    }

    public static void pararRPC() {
        rpc.shutdown();
    }

    public static void iniciarRPC() {
        try {
            rpc = new DiscordRpc();

            handler = new DiscordEventHandler() {
                @Override
                public void ready(User user) {
                    //não preciso fazer nada aqui porque a classe tocar já atualiza o rpc logo após iniciar
                }

                @Override
                public void disconnected(ErrorCode ec, String ns) {
//                    System.out.println("Disconnected " + ec.name() + " - " + ns);
                }

                @Override
                public void errored(ErrorCode ec, String ns) {
//                    System.out.println("Disconnected " + ec.name() + " - " + ns);
                }

                @Override
                public void joinGame(String string) {
                }

                @Override
                public void spectateGame(String string) {
                }

                @Override
                public void joinRequest(DiscordJoinRequest djr) {
                }

            };

            rpc.init(APPLICATION_ID, handler, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
