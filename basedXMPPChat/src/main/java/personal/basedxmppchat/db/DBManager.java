package personal.basedxmppchat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据库管理类
 *
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 * */
public class DBManager {
    private int version = 1;
    private String databaseName;

    //本地Context对象
    private Context mContext = null;

    private static DBManager dBManager = null;

    private DBManager(Context mContext){
        this.mContext = mContext;
    }

    public static DBManager getInstance(Context mContext, String databaseName){
        if(dBManager == null){
            dBManager = new DBManager(mContext);
        }
        dBManager.databaseName = databaseName;
        return dBManager;
    }

    /**
     * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
     */
    public void closeDatabase(SQLiteDatabase dataBase,Cursor cursor){
        if(null != dataBase){
            dataBase.close();
        }
        if(null != cursor){
            cursor.close();
        }
    }

    /**
     * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
     */
    public SQLiteDatabase openDatabase(){
        return getDatabaseHelper().getWritableDatabase();
    }

    /**
     * 获取DataBaseHelper
     */
    public DataBaseHelper getDatabaseHelper(){
        return new DataBaseHelper(mContext,this.databaseName,null,this.version);
    }
}
