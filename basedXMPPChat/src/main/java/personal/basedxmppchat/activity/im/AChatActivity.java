package personal.basedxmppchat.activity.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import personal.basedxmppchat.activity.ActivitySupport;
import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.manager.MessageManager;
import personal.basedxmppchat.manager.NoticeManager;
import personal.basedxmppchat.manager.XmppConnectionManager;
import personal.basedxmppchat.model.IMMessage;
import personal.basedxmppchat.model.Notice;
import personal.basedxmppchat.util.DateUtil;

/**
 * 
 * 聊天对话.
 * 
 * @author shimiso
 */
public abstract class AChatActivity extends ActivitySupport {

	private Chat chat = null;
	private List<IMMessage> message_pool = null;
	protected String to;// 聊天人
	private static int pageSize = 10;
	private List<Notice> noticeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		to = getIntent().getStringExtra("to");
		if (to == null)
			return;
		chat = ChatManager.getInstanceFor(XmppConnectionManager.getInstance().getConnection())
				.createChat(to,null);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 第一次查询
		message_pool = MessageManager.getInstance(context)
				.getMessageListByFrom(to, 1, pageSize);
		if (null != message_pool && message_pool.size() > 0)
			Collections.sort(message_pool);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		registerReceiver(receiver, filter);

		// 更新某人所有通知
		NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
		super.onResume();

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				IMMessage message = intent
						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
				message_pool.add(message);
				receiveNewMessage(message);
				refreshMessage(message_pool);
			}
		}

	};

	protected abstract void receiveNewMessage(IMMessage message);

	protected abstract void refreshMessage(List<IMMessage> messages);

	protected List<IMMessage> getMessages() {
		return message_pool;
	}

	protected void sendMessage(String messageContent) throws Exception {

		String time = DateUtil.date2Str(Calendar.getInstance(),
				Constant.MS_FORMART);
		Message message = new Message();
		//message.setProperty(IMMessage.KEY_TIME, time);
		message.setBody(messageContent);
		chat.sendMessage(message);

		IMMessage newMessage = new IMMessage();
		newMessage.setMsgType(1);
		newMessage.setFromSubJid(chat.getParticipant());
		newMessage.setContent(messageContent);
		newMessage.setTime(time);
		message_pool.add(newMessage);
		MessageManager.getInstance(context).saveIMMessage(newMessage);
		// MChatManager.message_pool.add(newMessage);

		// 刷新视图
		refreshMessage(message_pool);

	}

	/**
	 * 下滑加载信息,true 返回成功，false 数据已经全部加载，全部查完了，
	 * 
	 * @param message
	 */
	protected Boolean addNewMessage() {
		List<IMMessage> newMsgList = MessageManager.getInstance(context)
				.getMessageListByFrom(to, message_pool.size(), pageSize);
		if (newMsgList != null && newMsgList.size() > 0) {
			message_pool.addAll(newMsgList);
			Collections.sort(message_pool);
			return true;
		}
		return false;
	}

	protected void resh() {
		// 刷新视图
		refreshMessage(message_pool);
	}

}
