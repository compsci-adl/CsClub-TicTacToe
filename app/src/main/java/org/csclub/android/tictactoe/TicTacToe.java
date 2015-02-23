package org.csclub.android.tictactoe;

import android.os.AsyncTask;

import java.util.Random;
import java.util.Vector;

public class TicTacToe implements IBoardListener, ITextListener, IWinListener {
    static final int COMPUTER_PLAYER = 0;
    static final int HUMAN_PLAYER = 1;
    static char[] PLAYER_TOKENS = new char[2];
    static int PLAYER_TURN;

    private Vector<IBoardListener> boardListeners = new Vector<>();
    private Vector<ITextListener> textListeners = new Vector<>();
    private Vector<IWinListener> winListeners = new Vector<>();
    private char[] board;

    private AI ai = new AI();

    public TicTacToe(char humanToken, String boardState) {
        PLAYER_TOKENS[HUMAN_PLAYER] = humanToken;
        PLAYER_TOKENS[COMPUTER_PLAYER] = (humanToken == 'X') ? 'O' : 'X';

        board = new char[]{ '-', '-', '-',
                            '-', '-', '-',
                            '-', '-', '-'};

        boardState.getChars(0, 9, board, 0);

        int turns = 0;
        for (char c : board) {
            if (c != '-') {
                turns++;
            }
        }
        if ((turns & 1) == 0) {
            PLAYER_TURN = (PLAYER_TOKENS[HUMAN_PLAYER] == 'X') ? HUMAN_PLAYER : COMPUTER_PLAYER;
        } else {
            PLAYER_TURN = (PLAYER_TOKENS[HUMAN_PLAYER] == 'X') ? COMPUTER_PLAYER : HUMAN_PLAYER;
        }
    }

    public void initialise() {
        if (PLAYER_TURN == COMPUTER_PLAYER) {
            aiTurn();
        }
    }

    public void addBoardListener(IBoardListener toAdd) {
        boardListeners.add(toAdd);
    }

    public void boardHasChanged() {
        for (IBoardListener l : boardListeners) {
            l.boardHasChanged();
        }
    }

    public void addTextListener(ITextListener toAdd) {
        textListeners.add(toAdd);
    }

    public void textHasChanged(String text) {
        for (ITextListener l : textListeners) {
            l.textHasChanged(text);
        }
    }

    public void addWinListener(IWinListener toAdd) {
        winListeners.add(toAdd);
    }

    public void notifyWin(char winner) {
        for (IWinListener l : winListeners) {
            l.notifyWin(winner);
        }
    }

    public int makeMove(int turn, int position) {
        if (position == -1) return -1;
        if (turn != PLAYER_TURN || board[position] != '-') return 0;
        board[position] = PLAYER_TOKENS[turn];
        boardHasChanged();

        char winner = checkWin();
        if (winner != '-') {
            notifyWin(winner);
            return 1;
        } else if (!contains(board, '-')) {
            notifyWin('-');
            return 1;
        }

        PLAYER_TURN = (PLAYER_TURN == HUMAN_PLAYER) ? COMPUTER_PLAYER : HUMAN_PLAYER;
        if (PLAYER_TURN == COMPUTER_PLAYER) aiTurn();
        return 1;
    }

    public char checkWin() {
        for (int i = 0; i < 3; ++i) {
            //Horizontal Checks
            if (board[i * 3] != '-' &&
                    board[i * 3] == board[i * 3 + 1] &&
                    board[i * 3] == board[i * 3 + 2]) return board[i * 3];
            //Vertical Checks
            if (board[i] != '-' &&
                    board[i] == board[i + 3] &&
                    board[i] == board[i + 6]) return board[i];
        }
        //Diagonal Checks
        if (board[0] != '-' &&
                board[0] == board[4] &&
                board[0] == board[8]) return board[0];

        if (board[2] != '-' &&
                board[2] == board[4] &&
                board[2] == board[6]) return board[2];

        return '-';
    }

    boolean contains(char[] board, char a) {
        for (char c : board) {
            if (c == a) return true;
        }
        return false;
    }

    public String toString() {
        return new String(board);
    }

    public void aiTurn() {
        StringUpdateTask strut = new StringUpdateTask();
        strut.execute();
    }

    private class StringUpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            Random random = new Random();
            int sleepTime = random.nextInt(2500);
            for (int i = 0; i < (sleepTime / 300) + 1; i++) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
                publishProgress(ai.getThinkString());
            }
            return "Choose a space";
        }

        @Override
        protected void onProgressUpdate(String... result) {
            textHasChanged(result[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            textHasChanged(result);
            makeMove(COMPUTER_PLAYER, ai.getMove(board, PLAYER_TOKENS[COMPUTER_PLAYER]));
        }
    }
}