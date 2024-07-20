package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    /**
     * Inserts a new message into the database.
     * @param message The message to be inserted.
     * @return The inserted message with its generated ID, or null if the insertion failed.
     */
    public Message insertMessage(Message message){
        Connection connection = ConnectionUtil.getConnection();
        try {
            // Write SQL logic here. You should only be inserting with the name column, so that the database may
            // automatically generate a primary key.
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //write preparedStatement's setString method here.
            preparedStatement.setInt(1, message.posted_by);
            preparedStatement.setString(2, message.message_text);
            preparedStatement.setLong(3, message.time_posted_epoch);
            
            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_account_id = (int) pkeyResultSet.getLong(1);
                return new Message(generated_account_id, message.posted_by, message.message_text, message.time_posted_epoch);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Checks if a given account ID exists in the database.
     * @param account_id The account ID to check.
     * @return True if the account ID exists, false otherwise.
     */
    public boolean doesIDExist(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            //Write SQL logic here
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //write preparedStatement's setString method here.
            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();

            // Check if the ResultSet contains any rows
            if (rs.next()) {
                // If there is at least one row, return true
                return true;
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return false;
    }

    /**
     * Retrieves all messages from the database.
     * @return A list of all messages.
     */
    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"),
                                rs.getInt("posted_by"),
                                rs.getString("message_text"),
                                rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;
    }

    /**
     * Retrieves a message by its ID.
     * @param id The ID of the message to retrieve.
     * @return The message with the given ID, or null if not found.
     */
    public Message getMessageByID(int id){
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"),
                                rs.getInt("posted_by"),
                                rs.getString("message_text"),
                                rs.getLong("time_posted_epoch"));
                return message;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Deletes a message by its ID.
     * @param id The ID of the message to delete.
     * @return True if the message was successfully deleted, false otherwise.
     */
    public boolean deleteMessageByID(int id){
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing message in the database.
     * @param message The message to be updated.
     * @return The updated message, or null if the update failed.
     */
    public Message updateMessage(Message message){
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, message.message_text);
            preparedStatement.setInt(2, message.message_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Return a new Message object with the updated text
                return getMessageByID(message.message_id);
            } else {
                // If no rows were affected, return null to indicate the update failed
                return null;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Retrieves all messages written by a particular user.
     * @param account_id The ID of the user whose messages are to be retrieved.
     * @return A list of messages written by the user.
     */
    public List<Message> getMessagesOfUser(int account_id){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT m.* " +
                        "FROM message m " +
                        "INNER JOIN account a ON m.posted_by = a.account_id " +
                        "WHERE a.account_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"),
                                rs.getInt("posted_by"),
                                rs.getString("message_text"),
                                rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;
    }
}
