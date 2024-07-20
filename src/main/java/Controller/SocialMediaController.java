package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;

import Model.*;
import Service.*;

import java.util.List;

/**
 * Endpoints and handlers for the controller.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        accountService = new AccountService();
        messageService = new MessageService();
    }
    /**
     * the endpoints in the startAPI() method
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postUserRegistration);
        app.post("/login", this::postUserLogin);
        app.post("messages", this::postNewMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessageByID);
        app.delete("/messages/{message_id}", this::deleteByMessageID);
        app.patch("/messages/{message_id}", this::updateMessageByID);
        app.get("/accounts/{account_id}/messages", this::retrieveAllMessageOfUser);

        return app;
    }

    /**
     * submit a new post
     * The request body will contain a JSON representation of a message, 
     * which should be persisted to the database, but will not contain a message_id.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    private void postNewMessage(Context ctx) throws JsonMappingException, JsonProcessingException {
        //- The creation of the message will be successful if and only if the message_text is not blank, 
        // is not over 255 characters, and posted_by refers to a real, existing user. If successful, the 
        // response body should contain a JSON of the message, including its message_id. The response status 
        // should be 200, which is the default. The new message should be persisted to the database.
        ObjectMapper om = new ObjectMapper();
        Message message = om.readValue(ctx.body(), Message.class);
        Message addedMessage = messageService.addMessage(message);

        if(addedMessage != null){
            ctx.json(addedMessage);
        }else{
            //- If the creation of the message is not successful, the response status should be 400. (Client error)
            ctx.status(400);
        }
    }

    /**
     *  A user should be able to submit a GET request on the endpoint to retrieve all message
     * 
     * @param ctx
     */
    private void getAllMessages(Context ctx){
        //- The response body should contain a JSON representation of a list containing all messages retrieved 
        // from the database. It is expected for the list to simply be empty if there are no messages. 
        // The response status should always be 200, which is the default.
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    /**
     *  Retrieve a message by its ID
     * 
     * @param ctx
     */
    private void getMessageByID(Context ctx){
        //- The response body should contain a JSON representation of the message identified by the message_id. 
        // It is expected for the response body to simply be empty if there is no such message. 
        // The response status should always be 200, which is the default.
        int id = Integer.parseInt(ctx.pathParam("message_id"));

        Message message = messageService.getMessageByID(id);

        // if the message is not null, then it exists.
        if(message != null){ 
            ctx.json(message);
        }else{
            ctx.status(200).result("");
        }
    }

     /**
     *  submit a DELETE request on the endpoint
     * 
     * @param ctx
     */
    private void deleteByMessageID(Context ctx){
        //- The deletion of an existing message should remove an existing message from the database. 
        // If the message existed, the response body should contain the now-deleted message. 
        // The response status should be 200, which is the default.
        int id = Integer.parseInt(ctx.pathParam("message_id"));

        Message message = messageService.getMessageByID(id);

        // if message is not null, it exists and therefore should be deleted.
        if(message != null){
            messageService.deleteMessageByID(id);
            ctx.json(message);
        }else{
            //- If the message did not exist, the response status should be 200, but the response body should be empty. 
            // This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint 
            // should respond with the same type of response.
            ctx.status(200).result("");
        }
    }

    /**
     * Update a message text identified by a message ID.
     * The request body should contain a new message_text values to replace the message identified by message_id. 
     * The request body can not be guaranteed to contain any other information.
     */
    private void updateMessageByID(Context ctx) throws JsonMappingException, JsonProcessingException {
        //- The update of a message should be successful if and only if the message id already exists and the new message_text 
        // is not blank and is not over 255 characters. If the update is successful, the response body should contain the full 
        // updated message (including message_id, posted_by, message_text, and time_posted_epoch), and the response status should 
        // be 200, which is the default. The message existing on the database should have the updated message_text.
        ObjectMapper om = new ObjectMapper();
        Message message = om.readValue(ctx.body(), Message.class);
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Message updatedMessage = null;

        message.setMessage_id(id);
        updatedMessage = messageService.updateMessage(message);

        if(updatedMessage != null){
            ctx.json(updatedMessage);
        }else{
        //- If the update of the message is not successful for any reason, the response status should be 400. (Client error)
            ctx.status(400);
        }
    }

    /**
     * retrieve all messages written by a particular user
     */
    private void retrieveAllMessageOfUser(Context ctx){
        // The response body contains a JSON representation of a list containing all messages posted by a particular user, 
        // which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. The 
        // response status should always be 200, which is the default.
        int id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getAllMessagesOfUser(id);

        ctx.json(messages);
    }

    /**
     * Handler to process new User Registration
     * Users are able to create a new Account on the endpoint POST localhost:8080/register. 
     * The body will contain a representation of a JSON Account, but will not contain an account_id.
     */
    private void postUserRegistration(Context ctx) throws JsonProcessingException {
        //- The registration will be successful if and only if the username is not blank, 
        //the password is at least 4 characters long, and an Account with that username does not already exist. 
        //If all these conditions are met, the response body should contain a JSON of the Account, 
        //including its account_id. The response status should be 200 OK, which is the default. 
        //The new account should be persisted to the database.
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);

        if(addedAccount != null){
            ctx.json(addedAccount);
        }else{
            //- If the registration is not successful, the response status should be 400. (Client error)
            ctx.status(400);
        }
    }

    /**
     * Users verify login info on the endpoint POST localhost:8080/login. The request body 
     * will contain a JSON representation of an Account, not containing an account_id. 
     */
    
    private void postUserLogin(Context ctx) throws JsonProcessingException {
        // The login will be successful if and only if the username and password provided in the request body JSON 
        // match a real account existing on the database. If successful, the response body should contain a JSON of 
        // the account in the response body, including its account_id. The response status should be 200 OK, 
        // which is the default.
        ObjectMapper om = new ObjectMapper();

        Account account = om.readValue(ctx.body(), Account.class);

        Account loggedAccount = accountService.loginAccount(account);

        if(loggedAccount != null){
            ctx.json(loggedAccount);
        }else{
            // If the login is not successful, the response status should be 401. (Unauthorized)
            ctx.status(401);
        }
    }
}