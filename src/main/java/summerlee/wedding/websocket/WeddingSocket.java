package summerlee.wedding.websocket;import java.io.IOException;import javax.websocket.CloseReason;import javax.websocket.OnClose;import javax.websocket.OnMessage;import javax.websocket.OnOpen;import javax.websocket.Session;import javax.websocket.server.ServerEndpoint;/** * 车辆信息推送 *  * @file CarMonitorSocket * @author libin * @Create 2017-01-12 */@ServerEndpoint(value = "/wssocket"/*, configurator=WeddingSocketConfigurator.class*/)public class WeddingSocket extends Thread {		private Session session;	/**	 * 连接建立	 * 	 * @param session	 */	@OnOpen	public void onOpen(Session session) {		// 保存Session		Broadcaster.addSocket(session.getId(), this);		// 保存Session		this.session = session;	}	/**	 * 连接关闭	 * 	 * @param session	 */	@OnClose	public void onClose(Session session, CloseReason closeReason) {		System.out.println("OnClose : " + session);		Broadcaster.removeSocket(session.getId());	}	/**	 * 接收到消息	 * 	 * @param message	 */	@OnMessage	public void onMessage(String message) {			}	public void send(String danmu) {		try {			if(session.isOpen()){				session.getBasicRemote().sendText(danmu);			}		} catch (Exception e) {			e.printStackTrace();		}	}}