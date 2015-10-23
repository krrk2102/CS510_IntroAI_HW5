package cs380.othello;

import java.util.Scanner;

/**
 * CS 510 Introduction to Artificial Intelligence
 * Homework 5
 * Shangqi Wu
 * Implementation for Minimax algorithm with better evaluation function
 * It is implemented in the modified OthelloState.java, named as eval
 * Implementation for Monte Carlo Tree Search algorithm
 * Implementation for extra credit part 1: Monte Carlo algorithm tournament
 * 
 * original @author santi
 */
public class Test {
    
    
    public static void main(String args[]) {
    	
    	Scanner user_input = new Scanner(System.in);
        // Create the game state with the initial position for an 8x8 board:
        OthelloState state = setState(user_input);
        OthelloPlayer[] players = new OthelloPlayer[2];
        players = setPlayers(user_input);
        user_input.close();
        
        int counter = 0;
        do{
        	counter++;
            // Display the current state in the console:
        	System.out.println("\nStep number: " + counter);
            System.out.println("Current state, " + OthelloState.PLAYER_NAMES[state.nextPlayerToMove] + " to move:");
            System.out.print(state);
            
            // Get the move from the player:
            OthelloMove move = players[state.nextPlayerToMove].getMove(state);            
            System.out.println(move);
            state = state.applyMoveCloning(move);
        } while(!state.gameOver());

        // Show the result of the game:
        System.out.println("\nFinal state with score: " + state.score());
        System.out.println(state);
        System.out.println("Total step number is: " + counter);
        if (state.score() > 0) System.out.println("Player 1 wins.");
        else if (state.score() == 0) System.out.println("2 Player tie.");
        else System.out.println("Player 2 wins.");
    }
    
    private static OthelloState setState(Scanner state_input) {
    	int board_size = 2;
    	while (board_size <= 2) {
    		System.out.println("Please enter a number as board size you preferred:\n(The number must be greater than 2, a size of 8 is recommended)");
    		board_size = state_input.nextInt();
    	}
    	return new OthelloState(board_size);
    }
    
    private static OthelloPlayer[] setPlayers(Scanner input_players) {
    	OthelloPlayer[] _players = new OthelloPlayer[2];
    	int player_type = -1;
        while (player_type < 1 || player_type > 5) {
        	System.out.println("Please select which player you expect Player 1 to be:\nPress 1 for Random Player\nPress 2 for regular Minimax Player\nPress 3 for Minimax Player with better eval\nPress 4 for regular Monte Carlo Tree Search\nPress 5 for Monte Carlo Tree Search Tournament");
        	player_type = input_players.nextInt();
        }
        _players[0] = setOnePlayer(input_players, player_type, 0);
        player_type = -1;
        while (player_type < 1 || player_type > 5) {
        	System.out.println("Please select which player you expect Player 1 to be:\nPress 1 for Random Player\nPress 2 for regular Minimax Player\nPress 3 for Minimax Player with better eval\nPress 4 for regular Monte Carlo Tree Search\nPress 5 for Monte Carlo Tree Search Tournament");
        	player_type = input_players.nextInt();
        }
        _players[1] = setOnePlayer(input_players, player_type, 1);
    	return _players;
    }
    
    private static OthelloPlayer setOnePlayer(Scanner input_player, int _player_type, int _player_number) {
    	switch (_player_type) {
    		case 1: return new OthelloRandomPlayer();
    		case 2: {
    			int depth = -1;
    			while (depth < 0) {
    				System.out.println("You have chosen regular Minimax Player, please enter a number as minimax algorithm search depth:\n(Please note depth must be a positive number, 5 is recommended)");
    				depth = input_player.nextInt();
    			}
    			return new OthelloMinimaxPlayer_ShangqiWu(depth, _player_number);
    		}
    		case 3: {
    			int depth = -1;
    			while (depth < 0) {
    				System.out.println("You have chosen Minimax Player with better eval, please enter a number as minimax algorithm search depth:\n(Please note depth must be a positive number, 5 is recommended)");
    				depth = input_player.nextInt();
    			}
    			return new OthelloEvalPlayer_ShangqiWu(depth, _player_number);
    		}
    		case 4: {
    			int iteration = -1;
    			while (iteration < 0) {
    				System.out.println("You have chosen regular Monte Carlo Tree Search, please enter a number as iteration boundary for the player:\n(Please note iteration must be a positive number. 2000 is recommended, which has very good performance, too large numbers will cause the program run slowly.)");
    				iteration = input_player.nextInt();
    			}
    			return new OthelloMCTSPlayer_ShangqiWu(_player_number, iteration);
    		}
    		case 5: {
    			long time = -1;
    			while (time <= 0) {
    				System.out.println("You have chosen Tournament Monte Carlo Tree Search, please enter a number as time boundary for the player:\n(Please note time must be a positive number, defining the max calculation time (in ms) for this player, 500 is recommended.)");
    				time = input_player.nextInt();
    			}
    			return new OthelloMCTSPlayerTournament_ShangqiWu(_player_number, time);
    		}
    		default: return null;
    	}
    }
    
}
