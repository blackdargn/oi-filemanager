/*******************************************************
 * @作者: zhaohua
 * @日期: 2011-11-10
 * @描述: 系统消息总线,新增了UI线程执行的接收者
 * @声明: copyrights reserved by Petfone 2007-2011
 *******************************************************/

package org.openintents.filemanager.util;
import org.openintents.filemanager.util.MessageBus.MMessage;

import android.os.Handler;
import android.os.Message;

public class MessageBus extends AbstractBus<MMessage, Integer, MessageBus.MessageStrategy>
{
	private static final String KEY = "MessageBus";
	
	/** UI处理句柄*/
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			@SuppressWarnings("unchecked")
			Receiver<MMessage> receiver = (Receiver<MMessage>) msg.getData().get(KEY);
			msg.getData().remove(KEY);
			receiver.onReceive(new MMessage(msg));
		};
	};
	
	public MessageBus()
	{
		super();
		setStrategy(new MessageStrategy());
	}
	
	public MMessage createMessage(int what)
	{
		return new MMessage(what);
	}
	
	/** UIReceiver的就在UI线程中处理，否则默认处理 */
	protected void onReceive(
			Receiver<MMessage> receiver,
			final MMessage message)
	{
		if(receiver instanceof UIReceiver)
		{
			Message msg = message.toMessage();
			msg.getData().putSerializable(KEY, receiver);
			handler.sendMessage(msg);
		}else
		{
			receiver.onReceive(message.clone());
		}
	}
	
	/** 发送消息*/
	public void send(MMessage msg)
	{
		if(msg == null)
		{
			return;
		}
		send(msg.what, msg);
	}
	
	/**
	 * UI线程消息接收者
	 * @param <M>
	 */
	public static abstract class UIReceiver extends Receiver<MMessage>
	{
		private static final long serialVersionUID = 8595418220936525439L;
	}
	
	/** 消息策略：由过滤器 filter == message.what 匹配*/
	static class MessageStrategy implements AbstractBus.Strategy<MMessage, Integer>
	{
		public boolean isMatch(MMessage message, Integer filter)
		{
			return (filter != null && message != null && filter.equals(message.what));
		}
	}

	/** 单列模式 */
	private static MessageBus instance = new MessageBus();
	public static MessageBus getBusFactory()
	{
		return instance;
	}
	
	/** 系统 Message的替身，为了防止出现， Message in use 异常*/
	public class MMessage
	{
		public int what;
		public Object obj;
		public int arg1;
		public int arg2;
		
		public MMessage()
		{
			
		}
		
		public MMessage(int what)
		{
			this.what = what;
		}
		
		public MMessage(Message message)
		{
			what = message.what;
			obj = message.obj;
			arg1 = message.arg1;
			arg2 = message.arg2;
		}
		
		public Message toMessage()
		{
			Message msg = handler.obtainMessage(what);
			
			msg.obj = obj;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			
			return msg;
		}
	
		public MMessage clone()
		{
			MMessage one = new MMessage(what);
			
			one.obj = obj;
			one.arg1 = arg1;
			one.arg2 = arg2;
			
			return one;
		}
	}
}
