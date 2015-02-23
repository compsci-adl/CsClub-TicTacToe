package org.csclub.android.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TitleScreen extends Activity {

    static final int TITLE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);
    }

    public void tokenSelect(View view) {
        char mTokenChosen;
        if (view == findViewById(R.id.title_cross)) {
            mTokenChosen = 'X';
        } else {
            mTokenChosen = 'O';
        }

        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("player_token", mTokenChosen);
        startActivityForResult(intent, TITLE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TITLE_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                quit();
            }
        }
    }

    public void quit() {
        finish();
        System.exit(0);
    }
}