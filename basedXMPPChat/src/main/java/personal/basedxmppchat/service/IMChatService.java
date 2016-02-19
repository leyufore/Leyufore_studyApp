package personal.basedxmppchat.service;

import java.util.Calendar;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import personal.basedxmppchat.R;
import personal.basedxmppchat.activity.im.ChatActivity;
import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.manager.MessageManager;
import personal.basedxmppchat.manager.NoticeManager;
import personal.basedxmppchat.manager.XmppConnectionManager;
import personal.basedxmppchat.model.IMMessage;
import personal.basedxmppchat.model.Notice;
import personal.basedxmppchat.util.DateUtil;

/**
 * 聊天服务.
 *
 * @author shimiso
 */
public class IMChatService extends Service {
    private Context context;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initChatManager();
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
        super.onDestroy();
    }

    private void initChatManager() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        XMPPTCPConnection conn = XmppConnectionManager.getInstance()
                .getConnection();
        conn.addAsyncStanzaListener(pListener, MessageTypeFilter.CHAT);
    }

    StanzaListener pListener = new StanzaListener() {
        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            Message message = (Message) packet;
            if (message != null && message.getBody() != null
                    && !message.getBody().equals("null")) {
                IMMessage msg = new IMMessage();
                // String time = (String)
                // message.getProperty(IMMessage.KEY_TIME);
                String time = DateUtil.date2Str(Calendar.getInstance(),
                        Constant.MS_FORMART);
                msg.setTime(time);
                msg.setContent(message.getBody());
                if (Message.Type.error == message.getType()) {
                    msg.setType(IMMessage.ERROR);
                } else {
                    msg.setType(IMMessage.SUCCESS);
                }
                String from = message.getFrom().split("/")[0];
                msg.setFromSubJid(from);

                // 生成通知
                NoticeManager noticeManager = NoticeManager
                        .getInstance(context);
                Notice notice = new Notice();
                notice.setTitle("会话信息");
                notice.setNoticeType(Notice.CHAT_MSG);
                notice.setContent(message.getBody());
                notice.setFrom(from);
                notice.setStatus(Notice.UNREAD);
                notice.setNoticeTime(time);

                // 历史记录
                IMMessage newMessage = new IMMessage();
                newMessage.setMsgType(0);
                newMessage.setFromSubJid(from);
                newMessage.setContent(message.getBody());
                newMessage.setTime(time);
                MessageManager.getInstance(context).saveIMMessage(newMessage);
                long noticeId = -1;

                noticeId = noticeManager.saveNotice(notice);

                if (noticeId != -1) {
                    Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
                    intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
                    intent.putExtra("notice", notice);
                    sendBroadcast(intent);
                    setNotiType(R.drawable.icon,
                            getResources().getString(R.string.new_message),
                            notice.getContent(), ChatActivity.class, from);

                }

            }
        }
    };

    /**
     * 发出Notification的method.
     *
     * @param iconId       图标
     * @param contentTitle 标题
     * @param contentText  你内容
     * @param activity
     * @author shimiso
     * @update 2012-5-14 下午12:01:55
     */
    private void setNotiType(int iconId, String contentTitle,
                             String contentText, Class activity, String from) {

		/*
         * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        Intent notifyIntent = new Intent(this, activity);
        notifyIntent.putExtra("to", from);
        // notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(this, 0,
                notifyIntent, 0);

        /* 使用Builder创建Notication，并设置相关参数. */
        Notification myNoti = new Notification.Builder(this)
                // 点击自动消失(实质上设置flags的值为FLAG_AUTO_CANCEL.
                .setAutoCancel(true)
                /* 设置statusbar显示的文字信息 */
                .setContentTitle(contentTitle)
                /* 设置statusbar显示的文字信息 */
                .setContentText(contentText)
                /* 设置statusbar显示的icon */
                .setSmallIcon(iconId)
                /* 设置notification发生时同时发出默认声音 */
                .setDefaults(Notification.DEFAULT_SOUND)
                /*从api文档中发现,setAutoCancel与setDeleteIntent配套使用*/
                .setDeleteIntent(appIntent)
                .build();
                /* 送出Notification */
        /*Post a notification to be shown in the status bar. If a notification with
        * the same id has already been posted by your application and has not yet been canceled, it
        * will be replaced by the updated information.
        * */
        notificationManager.notify(0, myNoti);
    }
}
