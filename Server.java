import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private List<Integer> deck;
    private List<Integer> playerHand;
    private List<Integer> dealerHand;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for a client...");
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            output = new DataOutputStream(clientSocket.getOutputStream());

            playBlackjack();

            input.close();
            output.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void playBlackjack() {
        try {
            boolean playAgain = true;
            while (playAgain) {
                initializeDeck();
                playerHand = new ArrayList<>();
                dealerHand = new ArrayList<>();
    
                dealInitialCards(playerHand);
                dealInitialCards(dealerHand);
    
                boolean playerTurn = true;
                String command = "";
                while (playerTurn) {
                    output.writeUTF("Your hand: " + playerHand + " (Total: " + calculateScore(playerHand) + ")");
                    output.writeUTF("Hit or Stand?");
                    command = input.readUTF();
                    if (command.equalsIgnoreCase("hit")) {
                        playerHand.add(deck.remove(0));
                        if (calculateScore(playerHand) > 21) {
                            output.writeUTF("Bust! Your hand: " + playerHand + " (Total: " + calculateScore(playerHand) + ")");
                            playerTurn = false;
                        }
                    } else if (command.equalsIgnoreCase("stand")) {
                        playerTurn = false;
                    }
                }
    
                while (calculateScore(dealerHand) < 17) {
                    dealerHand.add(deck.remove(0));
                }
                int playerScore = calculateScore(playerHand);
                int dealerScore = calculateScore(dealerHand);
    
                output.writeUTF("Dealer's hand: " + dealerHand + " (Total: " + dealerScore + ")");
                if (dealerScore > 21 || playerScore > dealerScore) {
                    output.writeUTF("You win!");
                } else if (dealerScore == playerScore) {
                    output.writeUTF("It's a draw!");
                } else {
                    output.writeUTF("Dealer wins!");
                }
    
                output.writeUTF("Do you want to play again? (yes/no)");
                String playAgainInput = input.readUTF();
                if (!playAgainInput.equalsIgnoreCase("yes")) {
                    playAgain = false;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    private void initializeDeck() {
        deck = new ArrayList<>();
        for (int i = 1; i <= 13; i++) { // Cards 1-13 for each suit
            for (int j = 0; j < 4; j++) { // Four suits
                deck.add(i);
            }
        }
        Collections.shuffle(deck);
    }

    private void dealInitialCards(List<Integer> hand) {
        hand.add(deck.remove(0));
        hand.add(deck.remove(0));
    }

    private int calculateScore(List<Integer> hand) {
        int score = 0;
        int aceCount = 0;
        for (int card : hand) {
            if (card == 1) { // Ace
                aceCount++;
                score += 11;
            } else if (card > 10) { // Face cards
                score += 10;
            } else {
                score += card;
            }
        }

        while (score > 21 && aceCount > 0) {
            score -= 10; // Reduce Ace from 11 to 1
            aceCount--;
        }
        return score;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide the port number as an argument.");
        } else {
            int port = Integer.parseInt(args[0]);
            new Server(port);
        }
    }
}
