public class testDB {
    public static void main(String[] args) {
        if (ConnectionProvider.getCon() != null) {
            System.out.println(" Database connection successful!");
        } else {
            System.out.println(" Database connection failed!");
        }
    }
}
