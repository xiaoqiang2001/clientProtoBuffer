package com.amplesky.interactor;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.amplesky.protobuf.CmsgProto.CMsg;
import com.amplesky.protobuf.CmsgProto.CMsgHead;
import com.amplesky.protobuf.CmsgProto.CMsgReg;
import com.google.protobuf.InvalidProtocolBufferException;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		run();
	}

	private static void run() {
	
		try 
		{
			//  socket = new Socket("192.168.1.116", 12345);
			Socket sendSocket = new Socket();

		 
			sendSocket.setSendBufferSize(65536);
		
			sendSocket.setTcpNoDelay(true);
			
			SocketAddress sa = new InetSocketAddress("10.100.86.14", 12345);
		
			
			sendSocket.connect(sa,4000);
			sendSocket.setSoTimeout(4000);
		
			// 得到发送消息的对象
			CMsgHead head = CMsgHead.newBuilder().setMsglen(5).setMsgtype(1)
					.setMsgseq(3).setTermversion(41).setMsgres(5)
					.setTermid("11111111").build();

			// body
			CMsgReg body = CMsgReg.newBuilder().setArea(22).setRegion(33)
					.setShop(44).build();

			// Msg
			CMsg msg = CMsg.newBuilder()
					.setMsghead(head.toByteString().toStringUtf8())
					.setMsgbody(body.toByteString().toStringUtf8()).build();
			// 向服务器发送信息
			byte[] b =msg.toByteArray();
			//msg.writeTo(sendSocket.getOutputStream());
			sendSocket.getOutputStream().write(b);
			// 接受服务器的信息
			InputStream input = sendSocket.getInputStream();

			byte[] by = recvMsg(input);
			setText(CMsg.parseFrom(by));

			input.close();
			sendSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	
	/**
     * 接收server的信息
     * 
     * @return
     * @throws SmsClientException
     * @author fisher
	 * @throws IOException 
     */
    public static  byte[] recvMsg(InputStream inpustream) throws IOException {
        
 
            byte len[] = new byte[1024];
            int count = inpustream.read(len);  
        
            byte[] temp = new byte[count];
            for (int i = 0; i < count; i++) {   
                    temp[i] = len[i];                              
            } 
            return temp;
        }

    /**
     * 得到返回值添加到文本里面
     * 
     * @param g
     * @throws InvalidProtocolBufferException
     */
    public static void setText(CMsg g) throws InvalidProtocolBufferException  {
        CMsgHead h = CMsgHead.parseFrom(g.getMsghead().getBytes());
        StringBuffer sb = new StringBuffer();
        if (h.hasMsglen())
            sb.append("==len===" + h.getMsglen() + "\n");
        if (h.hasMsgres())
            sb.append("==res===" + h.getMsgres() + "\n");
        if (h.hasMsgseq())
            sb.append("==seq===" + h.getMsgseq() + "\n");
        if (h.hasMsgtype())
            sb.append("==type===" + h.getMsgtype() + "\n");
        if (h.hasTermid())
            sb.append("==Termid===" + h.getTermid() + "\n");
        if (h.hasTermversion())
            sb.append("==Termversion===" + h.getTermversion() + "\n");

        CMsgReg bo = CMsgReg.parseFrom(g.getMsgbody().getBytes());
        if (bo.hasArea())
            sb.append("==area==" + bo.getArea() + "\n");
        if (bo.hasRegion())
            sb.append("==Region==" + bo.getRegion() + "\n");
        if (bo.hasShop())
            sb.append("==shop==" + bo.getShop() + "\n");
        if (bo.hasRet())
            sb.append("==Ret==" + bo.getRet() + "\n");
        if (bo.hasTermid())
            sb.append("==Termid==" + bo.getTermid() + "\n");

       System.out.println(sb.toString());
    }

}
