package Service;

import Model.Account;
import DAO.AccountDAO;

import java.util.List;

public class AccountService {
    private AccountDAO accountDAO;

    // Default constructor that initializes a new AccountDAO instance
    public AccountService(){
        accountDAO = new AccountDAO();
    }

    // Constructor that accepts an AccountDAO instance, useful for dependency injection and testing
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    /**
     * Retrieves all accounts.
     * @return A list of all accounts.
     */
    public List<Account> getAllAccount() {
        return this.accountDAO.getAllAccounts();
    }

    /**
     * Adds a new account.
     * The registration will be successful if and only if the username is not blank,
     * the password is at least 4 characters long, and an Account with that username does not already exist.
     * @param account The account to be added.
     * @return The added account with its generated ID, or null if the registration criteria are not met.
     */
    public Account addAccount(Account account){
        if(account.getUsername().length() > 0 && account.getPassword().length() >= 4 &&
            accountDAO.getAccountByUsername(account.getUsername()) == null)
        {
             return this.accountDAO.insertAccount(account);
        }
        
        return null;
    }

    /**
     * Logs in an account.
     * The login will be successful if and only if the username and password provided
     * match a real account existing in the database.
     * @param account The account to be logged in.
     * @return The account if the login is successful, null otherwise.
     */
    public Account loginAccount(Account account){
        return accountDAO.getAccountByUsernameAndPassword(account.getUsername(), account.getPassword());
    }
}
