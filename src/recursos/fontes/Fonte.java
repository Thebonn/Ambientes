package recursos.fontes;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author Bonn
 */
public class Fonte {

    //talvez no futuro ser possivel o usuario carregar as proprias fontes
    public static boolean carregarFontes() {
        try {
            String fontes[] = {"OpenSauceOne-Regular.ttf", "OpenSauceOne-Bold.ttf", "OpenSauceOne-Light.ttf", "OpenSauceOne-Black.ttf"};
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (int i = 0; i < fontes.length; i++) {
                Font fonte = Font.createFont(Font.PLAIN, Fonte.class.getResourceAsStream("/recursos/fontes/" + fontes[i]));
                ge.registerFont(fonte);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
