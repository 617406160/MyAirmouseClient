package com.example.myairmouseclient1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

// 异步任务处理消息的发送
public class MessageSender extends AsyncTask<Void, Void, Void> {
	private InetAddress serverAddress;
	private int port;
	List<DatagramPacket> packetsList = new ArrayList<DatagramPacket>();
	private boolean running = true;
	private Object lock = new Object();

	@Override
	protected void onPreExecute() {
		try {
			serverAddress = InetAddress.getByName(LoginInfo.IP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.port = Integer.valueOf(LoginInfo.port);
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while (running) {
			try {
				synchronized (lock) {
					Log.i("main", "send2线程已经被激活");
					Log.i("main", "send 里的packetsList.size() = "+ packetsList.size());
					if (packetsList.size() > 0) {
						Log.i("main", "packetsList.size() > 0");
						Log.e("TAG", "send2");
						socket.send(packetsList.remove(0));
					} else {
						lock.wait();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		socket.close();
		return null;
	}

	public void send(String message) {
		synchronized (lock) {
			byte[] temp = message.getBytes();
			DatagramPacket packet = new DatagramPacket(temp, temp.length,
					serverAddress, port);
			packetsList.add(packet);
			lock.notify();
			Log.e("TAG", "send");
			Log.i("main", "send 里的packetsList.size()" + packetsList.size());
		}
	}

	public void stop() {
		Log.i("main", "stop被启用");
		running = false;
		synchronized (lock) {
			lock.notify();
		}
	}
}

// private void sendMessage(final String message) {
// new Thread(new Runnable() {
//
// @Override
// public void run() {
// try {
// socket = new DatagramSocket();
// // 获取输入的IP和端口号
//
// int port = 8888;// Integer.parseInt(LoginInfo.port);
// System.out.println(serverAddress);
// byte dataes[] = message.getBytes();
// DatagramPacket packet = new DatagramPacket(dataes, dataes.length,
// serverAddress, port);
// socket.send(packet);
// } catch (Exception e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
// }).start();
// }