package org.imarks.vliegertest.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mark on 8-4-2016.
 */
public class ListeningScore implements Serializable {

    public List<Integer> played;
    public List<Integer> heared;
    public boolean isCorrect;

    public ListeningScore()
    {

    }
}
