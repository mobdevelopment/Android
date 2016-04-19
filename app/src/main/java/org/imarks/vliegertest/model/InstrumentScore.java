package org.imarks.vliegertest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mark on 8-4-2016.
 */
public class InstrumentScore implements Serializable {
    public int id;
    public Image altitude;
    public Image compass;
    public Image correct;
    public Image given;
    public boolean isCorrect;

    public InstrumentScore()
    {

    }
}
