package repiola;

/**
 *
 * @author mariano
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {

            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // handle exception
        }


        (new gui.Gui()).setVisible(true);
    }
}
