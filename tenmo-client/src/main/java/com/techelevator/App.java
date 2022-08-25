package com.techelevator;

import com.techelevator.model.AuthenticatedUser;
import com.techelevator.model.Transfer;
import com.techelevator.model.User;
import com.techelevator.services.AccountService;
import com.techelevator.services.AuthenticationService;
import com.techelevator.services.ConsoleService;
import com.techelevator.model.UserCredentials;
import com.techelevator.services.TransferService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {

        BigDecimal balance = accountService.getBalance(currentUser);
        System.out.println("/n Your current account balance is: $" + balance + "/n");
	}

	private void viewTransferHistory() {
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		User[]users = transferService.getAllUsers();
        Transfer transfer = new Transfer();

        if (users != null) {
            System.out.println("-------------------------------------------\n" +
                    "Users\n" +
                    "ID          Name\n" +
                    "-------------------------------------------");
            for (User user : users) {
                System.out.println(accountService.getByAccountId(user.getId()) + "   " + accountService.getUsername(user.getId()));
            }
            System.out.println("-------------------------------------------");
        }
        //using ConsoleService over Scanner tool
        ConsoleService console = new ConsoleService();
        // need Integer over int to use equals method in User class
        Integer userTo = console.promptForInt("Enter ID of user you are sending to (0 to cancel):");
        Integer accountFrom =  accountService.getByAccountId(currentUser.getUser().getId()).getAccountId();

        if (accountFrom.equals(userTo)) {
             userTo = console.promptForInt("Cannot send money to self");
        } else {
            BigDecimal amountToSend = console.promptForBigDecimal("Enter amount:");

        //  if (amountToSend.intValue() <= accountService.getBalance(currentUser).intValue())
            if (amountToSend.compareTo(accountService.getBalance(currentUser)) <= 0 && amountToSend.intValue() > 0) {
                // -1, 0, 1 <-- Less than, equal to, greater than
                //at this point, everything is valid, now we need to create transfer to store in table
                transfer.setTransferTypeId(2); //referred to tenmo.sql for numerical value
                transfer.setTransferStatusId(2);
                transfer.setAccountFrom(accountFrom);
                transfer.setAccountTo(userTo);

                transferService.addTransfer(transfer);
                BigDecimal remainingBalance = accountService.getBalance(currentUser);
                System.out.println("Remaining Balance: " + remainingBalance);

            }
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}