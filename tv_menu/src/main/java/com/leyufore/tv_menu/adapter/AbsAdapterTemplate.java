package com.leyufore.tv_menu.adapter;

import com.leyufore.tv_menu.observer.DataObserver;

/**
 * Created by wenrule on 16/3/23.
 */
public abstract class AbsAdapterTemplate implements AdapterTemplate{

    private DataObserver mDataObserver;

    public void notifyDataSetChange(){
        if(mDataObserver != null){
            mDataObserver.onChange();
        }
    }

    public void setDataObserver(DataObserver dataObserver){
        this.mDataObserver = dataObserver;
    }

}
