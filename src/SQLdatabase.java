import java.sql.*;
import java.util.ArrayList;

public class SQLdatabase {
    private final String fileName = "NSDServerDb.db";
    private String connectionURL = "jdbc:sqlite:" + fileName;

    public ArrayList<String[]> GetUsers(){
        String query = "SELECT * FROM Users";
        ArrayList<String[]> Users = new ArrayList<>();
        ResultSet resultSet;
        try (Connection connection = DriverManager.getConnection(connectionURL);
             Statement statement = connection.createStatement())
        {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                String[] temp= new String[2];
                temp[0] = resultSet.getString(2);
                temp[1] = resultSet.getString(3);
                Users.add(temp);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Users;
    }
    public void AddTopic(String topic){
        String query = "INSERT INTO TopicArchive (Topic) VALUES ('"+topic+"');";
        try (Connection connection = DriverManager.getConnection(connectionURL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void ArchiveTopicMsg(String[] Data, String time){
        String query = "INSERT INTO TopicArchive (Topic, User, Time, Msg) VALUES ('"+Data[0]+"','"+Data[1]+"','"+time+"','"+Data[2]+"');";
        try (Connection connection = DriverManager.getConnection(connectionURL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void ArchiveMsg(String[] Data, String time){
        String query = "INSERT INTO MsgArchive (FromUser, ToUser, Msg, Time) VALUES ('"+Data[0]+"','"+Data[1]+"','"+Data[2]+"','"+time+"');";
        try (Connection connection = DriverManager.getConnection(connectionURL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> GetTopicLog(String topic){
        String query = "Select * FROM TopicArchive";
        ArrayList<String[]> TopicLog = new ArrayList<>();
        ResultSet resultSet;
        try (Connection connection = DriverManager.getConnection(connectionURL);
             Statement statement = connection.createStatement())
        {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                if(resultSet.getString(2).equals(topic)){
                    String[] temp= new String[3];
                    temp[0] = resultSet.getString(3);
                    temp[1] = resultSet.getString(4);
                    temp[2] = resultSet.getString(5);
                    TopicLog.add(temp);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return TopicLog;
    }
    public ArrayList<String> Topics() {
        String query = "Select * FROM TopicArchive";
        ArrayList<String> Topics = new ArrayList<>();
        ResultSet resultSet;
        try (Connection connection = DriverManager.getConnection(connectionURL);
             Statement statement = connection.createStatement())
        {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Topics.add(resultSet.getString(2));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Topics;
    }
}
