package org.csclub.android.tictactoe;

import java.util.Random;
import java.util.Vector;

public class AI {
    private static final int ALPHA_DEFAULT = -9999;
    private static final int BETA_DEFAULT = 9999;
    private int m_iThinkStringCounter = 0;

    public String getThinkString() {
        m_iThinkStringCounter++;
        m_iThinkStringCounter &= 3;

        switch (m_iThinkStringCounter) {
            case 0:
                return "Thinking";
            case 1:
                return "Thinking.";
            case 2:
                return "Thinking..";
            case 3:
                return "Thinking...";
            default:
                return "Тхинкинг";
        }
    }

    public int getMove(char[] board, char token) {
        return alphaBeta(board, token);
    }

    int getWinner(char[] board) {
        char winner = '-';

        for (int i = 0; i < 3; i++) {
            //Horizontal Checks
            char player = board[(i * 3)];
            if (player == board[(i * 3) + 1] &&
                    player == board[(i * 3) + 2] &&
                    player != '-') {
                winner = player;
            }

            //Vertical Checks
            player = board[i];
            if (player == board[i + 3] &&
                    player == board[i + 6] &&
                    player != '-') {
                winner = player;
            }
        }

        //Diagonal Checks
        char player = board[2];
        if (player == board[4] &&
                player == board[6] &&
                player != '-') {
            winner = player;
        }

        player = board[0];
        if (player == board[4] &&
                player == board[8] &&
                player != '-') {
            winner = player;
        }

        switch (winner) {
            case 'X':
                return 1;
            case 'O':
                return -1;
            default:
                return 0;
        }
    }

    boolean contains(char[] board, char a) {
        for (char c : board) {
            if (c == a) return true;
        }
        return false;
    }

    int alphaBeta(char[] board, char turn, int alpha, int beta) {
        char[] childBoard = new char[9];
        char nextTurn;

        int win = getWinner(board);
        int bestVal = 0;
        int result;

        if (win != 0 || !contains(board, '-')) {
            return win;
        }

        if (turn == 'X') {
            nextTurn = 'O';
            alpha = -9999;
            for (int i = 0; i < 9; i++) {
                if (board[i] == '-') {
                    System.arraycopy(board, 0, childBoard, 0, board.length);
                    childBoard[i] = 'X';
                    result = alphaBeta(childBoard, nextTurn, alpha, beta);
                    if (result > alpha) {
                        alpha = result;
                        bestVal = alpha;
                    }
                    if (beta <= alpha) break;
                }
            }
        } else {
            nextTurn = 'X';
            beta = 9999;
            for (int i = 0; i < 9; i++) {
                if (board[i] == '-') {
                    System.arraycopy(board, 0, childBoard, 0, board.length);
                    childBoard[i] = 'O';
                    result = alphaBeta(childBoard, nextTurn, alpha, beta);
                    if (result < beta) {
                        beta = result;
                        bestVal = beta;
                    }
                    if (beta <= alpha) break;
                }
            }
        }
        return bestVal;
    }


    int alphaBeta(char[] board, char turn) {
        int val = (turn == 'X') ? -9999 : 9999;
        char nextTurn = (turn == 'X') ? 'O' : 'X';
        char[] childBoard = new char[9];

        Vector<Integer> bestMoves = new Vector<>();
        Random random = new Random();

        for (int i = 0; i < board.length; i++) {
            if (board[i] == '-') {
                System.arraycopy(board, 0, childBoard, 0, board.length);
                childBoard[i] = turn;
                if (getWinner(childBoard) != 0) bestMoves.add(i);
                int temp = alphaBeta(childBoard, nextTurn, ALPHA_DEFAULT, BETA_DEFAULT);

                if (temp == val) {
                    bestMoves.add(i);
                }
                if ((turn == 'X' && temp > val) || (turn == 'O' && temp < val)) {
                    val = temp;
                    bestMoves.clear();
                    bestMoves.add(i);
                }
            }
        }
        if (bestMoves.size() == 0) {
            return -1;
        }
        return bestMoves.elementAt(random.nextInt(bestMoves.size()));
    }
}
