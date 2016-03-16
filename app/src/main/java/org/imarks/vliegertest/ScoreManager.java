package org.imarks.vliegertest;

import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 1-3-2016.
 */
public class ScoreManager {

    private List<Integer> numbers   = new ArrayList<Integer>();
    private boolean roundFailed     = false;

    public void addNumber(final int number){
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                numbers.remove(number);
                roundFailed = true;
            }
        }.start();
        numbers.add(number);
    }


}
