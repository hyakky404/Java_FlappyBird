package SQL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import FlappyBird.FlappyBird;

public class Database 
{
    private final String hostName = "";  //Need to set database host name
    private final String dbName = "flappy_bird";
    private final String userName = "root";
    private final String password = "";
    private final String url = "jdbc:mysql://"  + hostName + "/" + dbName;

    public Connection connect()
    {
        Connection conn = null;

        try 
        {
            conn = DriverManager.getConnection(url, userName, password);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return conn;
    }
    
    public static ArrayList<ScoreList> sAll()
    {
        Database connectDatabase = new Database(); 
        Connection conn = connectDatabase.connect();
        String sql = "SELECT * FROM `score`";
        ArrayList <ScoreList> list = new ArrayList<>();

        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                int score = rs.getInt("score");
                list.add(new ScoreList(score));
            }

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        list.forEach(i -> System.out.println(i));

        return list;
    }
    
    public static void add(ScoreList list) throws SQLException
    {
        Database connectDatabase = new Database(); 
        Connection conn = connectDatabase.connect();

        try
        {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO `score`(`Score`) VALUES ('" + FlappyBird.getScore() +  "')");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
            {
                conn.close();
            }
        }
    }

    public static void deleteAll(ScoreList list) throws SQLException
    {
        Database connectDatabase = new Database(); 
        Connection conn = connectDatabase.connect();
        
        try
        {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM `score`");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
            {
                conn.close();
            }
        }
    }
}
