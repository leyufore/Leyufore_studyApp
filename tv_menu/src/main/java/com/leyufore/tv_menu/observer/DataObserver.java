package com.leyufore.tv_menu.observer;

/**
 * 观察者模式应用 viewgroup -- adpater
 * 类似android里面的DataSetObserver接口.用于被观察者持有观察者的对象
 * Created by wenrule on 16/3/23.
 */
public interface DataObserver {
    void onChange();
}
