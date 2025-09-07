package sistema;

/**
 *
 * @author Thebonn
 */
public class Ambiente {

    
    
    public static void main(String[] args) {
        boolean semgui = false;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("nogui") || args[i].contains("semgui")) {
                semgui = true;
                break;
            }
        }

        if (semgui) {
           new sistema.SemGui().principal();
        } else {
            new gui.Splash().setVisible(true);
        }
        
    }
}
