package sistema;

/**
 *
 * @author Bonn
 */
public class Ambiente {

    public static void main(String[] args) {

        boolean semgui = false;
        boolean semsplash = false;

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "nogui":
                case "semgui":
                    semgui = true;
                    break;

                case "semsplash":
                case "nosplash":
                    semsplash = true;
                    break;
            }

        }

        if (semgui) {
            new sistema.SemGui().principal();
        } else {
            try {
                if (semsplash) {
                    sistema.Configs.carregar();
                    if (sistema.Info.primeiraVez) {
                        new gui.PrimeiraVez().setVisible(true);
                    } else {
                        new gui.Tocar(sistema.Info.animacaoIntroducao).setVisible(true);
                    }
                } else {
                    new gui.Splash().setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
