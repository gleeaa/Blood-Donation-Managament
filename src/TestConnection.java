import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection con = ConnectionProvider.getCon();
            if (con != null) {
                System.out.println("Connection OK!");
                con.close();
            }
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
