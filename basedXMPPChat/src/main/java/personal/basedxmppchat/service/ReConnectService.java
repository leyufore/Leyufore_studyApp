package personal.basedxmppchat.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.manager.XmppConnectionManager;


/**
 * 重连接服务.
 *
 * @author shimiso
 */
public class ReConnectService extends Service {
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    @Override
    public void onCreate() {
        context = this;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(reConnectionBroadcastReceiver, mFilter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(reConnectionBroadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver reConnectionBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d("mark", "网络状态已经改变");
                connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                XMPPTCPConnection connection = XmppConnectionManager.getInstance()
                        .getConnection();
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    if (!connection.isConnected()) {
                        reConnect(connection);
                    } else {
                        sendInentAndPre(Constant.RECONNECT_STATE_SUCCESS);
                        Toast.makeText(context, "用户已上线!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    sendInentAndPre(Constant.RECONNECT_STATE_FAIL);
                    Toast.makeText(context, "网络断开,用户已离线!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        }

    };

    /**
     * 递归重连，直连上为止.
     *
     * @author shimiso
     */
    public void reConnect(AbstractXMPPConnection connection) {
        try {
            connection.connect();
            if (connection.isConnected()) {
                Presence presence = new Presence(Presence.Type.available);
                connection.sendStanza(presence);
                Toast.makeText(context, "用户已上线!", Toast.LENGTH_LONG).show();
            }
        } catch (XMPPException e) {
            Log.e("ERROR", "XMPP连接失败!", e);
            reConnect(connection);
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInentAndPre(boolean isSuccess) {
        Intent intent = new Intent();
        SharedPreferences preference = getSharedPreferences(Constant.LOGIN_SET,
                0);
        // 保存在线连接信息
        preference.edit().putBoolean(Constant.IS_ONLINE, isSuccess).apply();
        intent.setAction(Constant.ACTION_RECONNECT_STATE);
        intent.putExtra(Constant.RECONNECT_STATE, isSuccess);
        sendBroadcast(intent);
    }

}
