package org.csclub.android.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameScreen extends Activity implements IBoardListener, ITextListener, IWinListener {
    private TicTacToe m_TTT;
    private AI m_AI;
    private SharedPreferences m_Prefs;
    private char m_cPlayerToken;
    private String m_sBoardState;
    private View[] m_TokenButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        Intent intent = getIntent();
        m_cPlayerToken = intent.getCharExtra("player_token", '-');
        m_sBoardState = "---------";

        m_Prefs = getSharedPreferences("TTTPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = m_Prefs.edit();
        editor.putInt("player_token", m_cPlayerToken);
        editor.putString("board_state", m_sBoardState);
        editor.commit();

        m_TokenButtons = new View[]{
                findViewById(R.id.token_button0),
                findViewById(R.id.token_button1),
                findViewById(R.id.token_button2),
                findViewById(R.id.token_button3),
                findViewById(R.id.token_button4),
                findViewById(R.id.token_button5),
                findViewById(R.id.token_button6),
                findViewById(R.id.token_button7),
                findViewById(R.id.token_button8)
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences mPrefs = getSharedPreferences("TTTPrefs", MODE_PRIVATE);
        m_cPlayerToken = (char) mPrefs.getInt("player_token", '-');
        m_sBoardState = mPrefs.getString("board_state", "---------");

        if (m_TTT == null) {
            m_TTT = new TicTacToe(m_cPlayerToken, m_sBoardState);
            m_TTT.addBoardListener(this);
            m_TTT.addTextListener(this);
            m_TTT.addWinListener(this);
            m_TTT.initialise();
        }
        if (m_AI == null) {
            m_AI = new AI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mPrefs = getSharedPreferences("TTTPrefs", MODE_PRIVATE);
        m_cPlayerToken = (char) mPrefs.getInt("player_token", '-');
        m_sBoardState = mPrefs.getString("board_state", "---------");

        if (m_TTT == null) {
            m_TTT = new TicTacToe(m_cPlayerToken, m_sBoardState);
            m_TTT.addBoardListener(this);
            m_TTT.addTextListener(this);
            m_TTT.addWinListener(this);
            m_TTT.initialise();
        }
        if (m_AI == null) {
            m_AI = new AI();
        }
    }

    @Override
    protected void onPause() {
        super.onResume();
        SharedPreferences.Editor ed = m_Prefs.edit();
        ed.putInt("player_token", m_cPlayerToken);
        ed.putString("board_state", m_sBoardState);
        ed.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor ed = m_Prefs.edit();
        ed.putInt("player_token", m_cPlayerToken);
        ed.putString("board_state", m_sBoardState);
        ed.apply();
    }

    public void buttonClicked(View view) {
        int clicked = -1;
        for (int i = 0; i < m_TokenButtons.length; i++) {
            if (view == m_TokenButtons[i]) clicked = i;
        }
        m_TTT.makeMove(TicTacToe.HUMAN_PLAYER, clicked);
    }

    public void boardHasChanged() {
        m_sBoardState = m_TTT.toString();
        for (int i = 0; i < m_sBoardState.length(); i++) {
            setTokenButtonSymbol(m_TokenButtons[i], m_sBoardState.charAt(i));
            m_TokenButtons[i].invalidate();
        }
    }

    public void textHasChanged(String text) {
        ((TextView) findViewById(R.id.game_turn)).setText(text);
        findViewById(android.R.id.content).invalidate();
    }

    public void notifyWin(char winner) {
        int winString;
        if (winner == m_cPlayerToken) {
            winString = R.string.game_human_win;
        } else if (winner == '-') {
            winString = R.string.game_tie;
        } else {
            winString = R.string.game_computer_win;
        }
        AlertDialog.Builder adBuild = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        adBuild.setTitle(winString);
        adBuild.setMessage(R.string.game_win_replay_message);
        adBuild.setPositiveButton(R.string.game_win_replay_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setResult(RESULT_OK);
                finish();
            }
        });
        adBuild.setNegativeButton(R.string.game_win_quit_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        AlertDialog ad = adBuild.create();
        ad.show();
    }

    private void setTokenButtonSymbol(View view, char token) {
        int token_resource;
        switch (token) {
            case 'X':
                token_resource = R.drawable.cross;
                break;
            case 'O':
                token_resource = R.drawable.nought;
                break;
            default:
                token_resource = 0;
        }
        ((ImageButton) view).setImageResource(token_resource);
    }
}
