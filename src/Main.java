public class Main {
    public static void main(String[] args) {
        // Launch the login frame
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}

