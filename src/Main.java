public class Main {
    public static void main(String[] args) {
        // Launch the portal selection screen (the new entry point)
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PortalSelectionFrame().setVisible(true);
            }
        });
    }
}
