package com.techelevator;

import com.techelevator.model.AuthenticatedUser;
import com.techelevator.model.Transfer;
import com.techelevator.model.User;
import com.techelevator.model.UserCredentials;
import com.techelevator.services.AccountService;
import com.techelevator.services.AuthenticationService;
import com.techelevator.services.ConsoleService;
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
        System.out.println("\n Your current account balance is: $" + balance + "\n");
	}

	private void viewTransferHistory() {

        Transfer[]transfers =  transferService.getAllTransfers(accountService.getAccountId(currentUser.getUser().getId()));

		User[]users = transferService.getAllUsers();
        String username = null;

        System.out.println("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------");

        for (Transfer transfer : transfers) {

            if (accountService.getAccountId(currentUser.getUser().getId()).equals(transfer.getAccountFrom())) {
                for (User user : users) {
                    if (accountService.getAccountId(user.getId()).equals(transfer.getAccountTo())) {
                        username = user.getUsername();
                    }
                }
                System.out.println(transfer.getTransferID() + "        To:   " + username + "                " + transfer.getAmount());
            }
            else if (accountService.getAccountId(currentUser.getUser().getId()).equals(transfer.getAccountTo())) {
                for (User user : users) {
                    if (accountService.getAccountId(user.getId()).equals(transfer.getAccountFrom())) {
                        username = user.getUsername();
                    }
                }
                System.out.println(transfer.getTransferID() + "        From: " + username + "                " + transfer.getAmount());
            }
        }
        System.out.println("\n---------\n");
        ConsoleService console = new ConsoleService();
        Integer transferId = console.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if(transferId == 0) {
            mainMenu();
        }

        //transferDetails
       for (Transfer transfer : transfers) {
           if (transferId == 0) {
               break;
           }
           if (transfer.getTransferID() != transferService.getTransferDetails(transferId).getTransferID()){
        //       System.out.println("Invalid id");
           } else {
               System.out.println("\n--------------------------------------------\n" +
                       "Transfer Details\n" +
                       "--------------------------------------------\n");
               System.out.println("Id: " + transfer.getTransferID() + "\n"
                    //   + "From: " + transferService.getTransferDetails(accountService.getByAccountId(transfer.getAccountFrom()).getUserId()).getAccountFrom() + "\n"
//                       + "From: " + accountService.getByAccountId(transfer.getAccountFrom()).getUserId() + "\n"
                       + "From: " + currentUser.getUser().getUsername()+ "\n"
//                       + "To: " + transferService.getTransferDetails(accountService.getByAccountId(transfer.getAccountTo()).getUserId()).getAccountTo() + "\n"
                       + "To: " + accountService.getByUserId(accountService.getByAccountId(transfer.getAccountTo()).getUserId()).getUserId() + "\n"
                       + "Type: Send \n"
                       + "Status: Approved \n"
                       + "Amount: " + transfer.getAmount());
               break;
           }
       }
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
                System.out.println(user.getId() + "    " + user.getUsername());
            }
            System.out.println("-------------------------------------------");
        }
        ConsoleService console = new ConsoleService();
        // need Integer over int to use equals method in User class
        Integer userTo = console.promptForInt("Enter ID of user you are sending to (0 to cancel):");
       // System.out.println(currentUser.getUser().getId()); //test
        Integer userFrom =  accountService.getByUserId(currentUser.getUser().getId()).getUserId();

        if(userTo == 0) {
            mainMenu();
        } else if(userTo.equals(userFrom)) {
            System.out.println("");
            System.out.println("Transaction cancelled. You cannot seed money to yourself! Please try again");
            System.out.println("");
            sendBucks();


            //work on this, still caught in line 212
            //console service here, custom message prompt if user tries

            System.out.println("userTo userId = " + userTo + " accountId = " + accountService.getByUserId(userTo).getAccountId() + " Balance = " + accountService.getByUserId(userTo).getBalance());
            System.out.println("userFrom userId = " + userFrom + " accountId = " + accountService.getByUserId(userFrom).getAccountId() + " Balance = " + accountService.getByUserId(userFrom).getBalance());

//        while (accountFrom.equals(userTo)) {
//            userTo = console.promptForInt("Cannot send money to self");
//        }
//        will come back to validate above later

            BigDecimal amountToSend = console.promptForBigDecimal("Enter amount:");
            if (amountToSend.compareTo(accountService.getBalance(currentUser)) <= 0 && amountToSend.intValue() > 0) {
                // -1, 0, 1 <-- Less than, equal to, greater than
                //at this point, everything is valid
                transfer.setTransferTypeId(2); //refer to tenmo.sql for numerical value
                transfer.setTransferStatusId(2);
                transfer.setAccountFrom(accountService.getByUserId(userFrom).getAccountId());
                transfer.setAccountTo(accountService.getByUserId(userTo).getAccountId());
                //these 2 lines above need account id, was passing thru user id
                transfer.setAmount(amountToSend);

                transferService.addTransfer(transfer);

                BigDecimal remainingBalance = accountService.getBalance(currentUser).subtract(amountToSend);
                accountService.getByUserId(userFrom).setBalance(remainingBalance);
                System.out.println("Remaining Balance: " + accountService.getBalance(currentUser));

            } else { // can clean this up, doesn't get caught if sent to self, account won't update regardless
                System.out.println("Not enough money in account or money entered must be greater than 0");
            }
            //    }
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
