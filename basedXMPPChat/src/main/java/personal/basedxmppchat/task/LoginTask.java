package personal.basedxmppchat.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.Collection;

import personal.basedxmppchat.R;
import personal.basedxmppchat.activity.GuideViewActivity;
import personal.basedxmppchat.activity.IActivitySupport;
import personal.basedxmppchat.activity.MainActivity;
import personal.basedxmppchat.comm.Constant;
import personal.basedxmppchat.manager.XmppConnectionManager;
import personal.basedxmppchat.model.LoginConfig;

/**
 * 
 * 登录异步任务.
 * 
 * @author shimiso
 */
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private ProgressDialog pd;
	private Context context;
	private IActivitySupport activitySupport;
	private LoginConfig loginConfig;

	public LoginTask(IActivitySupport activitySupport, LoginConfig loginConfig) {
		this.activitySupport = activitySupport;
		this.loginConfig = loginConfig;
		this.pd = activitySupport.getProgressDialog();
		this.context = activitySupport.getContext();
	}

	@Override
	protected void onPreExecute() {
		pd.setTitle("请稍等");
		pd.setMessage("正在登录...");
		pd.show();
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return login();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		pd.dismiss();
		switch (result) {
		case Constant.LOGIN_SECCESS: // 登录成功
			Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			if (loginConfig.isFirstStart()) {// 如果是首次启动
				intent.setClass(context, GuideViewActivity.class);
				loginConfig.setFirstStart(false);
			} else {
				intent.setClass(context, MainActivity.class);
			}
			activitySupport.saveLoginConfig(loginConfig);// 保存用户配置信息
			activitySupport.startService(); // 初始化各项服务
			context.startActivity(intent);
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_invalid_username_password),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_server_unavailable),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.LOGIN_ERROR:// 未知异常
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.unrecoverable_error), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		super.onPostExecute(result);
	}

	// 登录
	private Integer login() {
        String username = loginConfig.getUsername();
        String password = loginConfig.getPassword();
        try {
            XMPPTCPConnection connection = XmppConnectionManager.getInstance()
                    .getConnection();
            connection.connect();
            connection.login(username, password); // 登录
            // OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//处理离线消息
            connection.sendStanza(new Presence(Presence.Type.available));
            if (loginConfig.isNovisible()) {// 隐身登录
                Presence presence = new Presence(Presence.Type.unavailable);
                Collection<RosterEntry> rosters = Roster.getInstanceFor(connection).getEntries();
                for (RosterEntry rosterEntry : rosters) {
                    presence.setTo(rosterEntry.getUser());
                    connection.sendStanza(presence);
                }
            }
            loginConfig.setUsername(username);
            if (loginConfig.isRemember()) {// 保存密码
                loginConfig.setPassword(password);
            } else {
                loginConfig.setPassword("");
            }
            loginConfig.setOnline(true);
            return Constant.LOGIN_SECCESS;
        } catch (Exception xee) {
            xee.printStackTrace();
            if (xee instanceof XMPPException.XMPPErrorException) {
                XMPPException.XMPPErrorException xe = (XMPPException.XMPPErrorException) xee;
                final XMPPError error = xe.getXMPPError();
                //未找到相应属性替代
	            /*
	            int errorCode = 0;
				if (error != null) {
					errorCode = error.getCode();
				}
				if (errorCode == 401) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				}else if (errorCode == 403) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				} else {
					return Constant.SERVER_UNAVAILABLE;
				}*/
                return Constant.LOGIN_ERROR;
            } else {
                return Constant.LOGIN_ERROR;
            }
        }
    }
}
