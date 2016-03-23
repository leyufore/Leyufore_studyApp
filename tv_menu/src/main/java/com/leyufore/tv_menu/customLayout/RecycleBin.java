package com.leyufore.tv_menu.customLayout;

import android.view.View;

import com.leyufore.tv_menu.util.LogU;

import java.util.Deque;
import java.util.LinkedList;

public class RecycleBin {
    private Deque<View> deque = new LinkedList();

    public View pop() {
        if (this.deque.size() == 0) {
            LogU.logE("RecycleBin deque size = 0");
            return null;
        }
        return this.deque.removeFirst();
    }

    public void push(View view) {
        this.deque.add(view);
    }

    public int size() {
        return this.deque.size();
    }

    public void clear() {
        deque.clear();
    }
}