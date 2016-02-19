package personal.basedxmppchat.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

import java.util.List;

import personal.basedxmppchat.R;
import personal.basedxmppchat.activity.IActivitySupport;
import personal.basedxmppchat.activity.im.ChatActivity;
import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.model.IMMessage;
import personal.basedxmppchat.model.Notice;
import personal.basedxmppchat.util.DateUtil;

/**
 * 离线信息管理类.
 *
 * @author shimiso
 */
public class OfflineMsgManager {
    private static OfflineMsgManager offlineMsgManager = null;
    private IActivitySupport activitySupport;
    private Context context;

    private OfflineMsgManager(IActivitySupport activitySupport) {
        this.activitySupport = activitySupport;
        this.context = activitySupport.getContext();
    }

    public static OfflineMsgManager getInstance(IActivitySupport activitySupport) {
        if (offlineMsgManager == null) {
            offlineMsgManager = new OfflineMsgManager(activitySupport);
        }

        return offlineMsgManager;
    }

    /**
     * 处理离线消息.
     *
     * @param connection connection
     */
    public void dealOfflineMsg(XMPPTCPConnection connection) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);
        List<Message> list = offlineMessageManager.getMessages();
        Log.i("离线消息数量: ", "" + offlineMessageManager.getMessageCount());
        for (Message message : list) {
            Log.i("收到离线消息", "Received from 【" + message.getFrom()
                    + "】 message: " + message.getBody());
            if (message != null && message.getBody() != null
                    && !message.getBody().equals("null")) {
                IMMessage msg = new IMMessage();
                //在smack4.1.5中Message里面没能找到代表消息的时间,调试模式下也没能看到message信息有时间属性
//                String time = (String) message.getProperty(IMMessage.KEY_TIME);
                msg.setTime(DateUtil.getCurDateStr());
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
                notice.setNoticeTime(DateUtil.getCurDateStr());

                // 历史记录
                IMMessage newMessage = new IMMessage();
                newMessage.setMsgType(0);
                newMessage.setFromSubJid(from);
                newMessage.setContent(message.getBody());
                newMessage.setTime(DateUtil.getCurDateStr());
                MessageManager.getInstance(context).saveIMMessage(
                        newMessage);

                long noticeId = noticeManager.saveNotice(notice);
                if (noticeId != -1) {
                    Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
                    intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
                    intent.putExtra("noticeId", noticeId);
                    context.sendBroadcast(intent);
                    activitySupport.setNotiType(
                            R.drawable.icon,
                            context.getResources().getString(
                                    R.string.new_message),
                            notice.getContent(), ChatActivity.class, from);
                }
            }
        }

    }
}
