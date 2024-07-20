package Service;

import Model.Message;
import DAO.MessageDAO;

import java.util.List;

public class MessageService {
    MessageDAO messageDAO;

    // Default constructor that initializes a new MessageDAO instance
    public MessageService(){
        messageDAO = new MessageDAO();
    }

    // Constructor that accepts a MessageDAO instance, useful for dependency injection and testing
    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }

    /**
     * Adds a new message.
     * The creation of the message will be successful if and only if the message_text is not blank, 
     * is not over 255 characters, and posted_by refers to a real, existing user. If successful, the 
     * response body should contain a JSON of the message, including its message_id. The response status 
     * should be 200, which is the default. The new message should be persisted to the database.
     *
     * @param message The message to be added
     * @return The added message with its generated ID, or null if the message is invalid
     */
    public Message addMessage(Message message){
        if(!(message.message_text.length() > 255) && (message.message_text.length() > 0) 
            && messageDAO.doesIDExist(message.posted_by))
        {
            return messageDAO.insertMessage(message);
        }
        
        return null;
    }

    /**
     * Updates an existing message.
     * The updating of the message will be successful if and only if the message_text is not blank, 
     * is not over 255 characters, and posted_by refers to a real, existing user. If successful, the 
     * response body should contain a JSON of the message, including its message_id. The response status 
     * should be 200, which is the default. The updated message should be persisted to the database.
     *
     * @param message The message to be updated
     * @return The updated message, or null if the message is invalid
     */
    public Message updateMessage(Message message){
        if(!(message.message_text.length() > 255) && (message.message_text.length() > 0) &&
            doesIDExist(message.getMessage_id()))
        {
            return messageDAO.updateMessage(message);
        }
        
        return null;
    }

    /**
     * Retrieves all messages.
     *
     * @return A list of all messages
     */
    public List<Message> getAllMessages(){
        return messageDAO.getAllMessages();
    }

    /**
     * Checks if a given ID exists.
     *
     * @param id The ID to check
     * @return True if the ID exists, false otherwise
     */
    public boolean doesIDExist(int id){
        return messageDAO.doesIDExist(id);
    }

    /**
     * Retrieves a message by its ID.
     * Returns null if there is no such message.
     *
     * @param id The ID of the message to retrieve
     * @return The message with the given ID, or null if not found
     */
    public Message getMessageByID(int id){
        if(doesIDExist(id))
        {
            return messageDAO.getMessageByID(id);
        }
        return null;
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id The ID of the message to delete
     * @return True if the message was successfully deleted, false otherwise
     */
    public boolean deleteMessageByID(int id){
        return messageDAO.deleteMessageByID(id);
    }

    /**
     * Retrieves all messages written by a particular user.
     *
     * @param account_id The ID of the user
     * @return A list of messages written by the user
     */
    public List<Message> getAllMessagesOfUser(int account_id){
        return messageDAO.getMessagesOfUser(account_id);
    }
}
