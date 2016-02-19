package personal.basedxmppchat.manager;

import android.content.Context;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by wenrule on 16/1/31.
 */
public class UserManager {
    private static UserManager userManager = null;

    private UserManager(){

    }

    public static UserManager getInstance(Context context){
        if(userManager == null){
            userManager = new UserManager();
        }
        return userManager;
    }

    /**
     * vcard-电子名片,类似IQ等,也是一种传递的信息类型.用VCardManager管理
     * 获取用户的vcard信息 .
     */
    public VCard getUserVCard(String jid){
        XMPPTCPConnection xmppConn = XmppConnectionManager.getInstance().getConnection();
        VCard vcard = new VCard();
        try {
            vcard = VCardManager.getInstanceFor(xmppConn).loadVCard(jid);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return vcard;
    }

    /**
     * 保存用户的vcard信息. 注：修改vcard时，头像会丢失，此处为asmack.jar的bug，目前还无法修复
     */
    public VCard saveUserVCard(VCard vcard){
        XMPPTCPConnection xmppConn = XmppConnectionManager.getInstance().getConnection();
        try {
            VCardManager.getInstanceFor(xmppConn).saveVCard(vcard);
            return getUserVCard(vcard.getJabberId());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * 获取用户头像信息 .
     *
     * @param
     * @param jid
     * @return
     * @author shimiso
     * @update 2013-4-16 下午1:31:52
     */
    public InputStream getUserImage(String jid) {
        XMPPConnection connection = XmppConnectionManager.getInstance()
                .getConnection();
        InputStream ic = null;
        try {
            System.out.println("获取用户头像信息: " + jid);
            VCard vcard = new VCard();
            vcard.load(connection, jid);

            if (vcard == null || vcard.getAvatar() == null) {
                return null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    vcard.getAvatar());
            return bais;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ic;
    }
}
