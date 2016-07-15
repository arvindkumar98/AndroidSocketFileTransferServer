package com.example.androidsocketfiletransferserver;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends ActionBarActivity {

	TextView infoIp, infoPort;

	static final int SocketServerPORT = 8080;
	ServerSocket serverSocket;
	
	ServerSocketThread serverSocketThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoIp = (TextView) findViewById(R.id.infoip);
		infoPort = (TextView) findViewById(R.id.infoport);

		infoIp.setText(getIpAddress());
		
		serverSocketThread = new ServerSocketThread();
		serverSocketThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "SiteLocalAddress: "
								+ inetAddress.getHostAddress() + "\n";
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}
	
	public class ServerSocketThread extends Thread {

		@Override
		public void run() {
			Socket socket = null;
			
			try {
//				serverSocket = new ServerSocket(SocketServerPORT);
//				MainActivity.this.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						infoPort.setText("I'm waiting here: "
//							+ serverSocket.getLocalPort());
//					}});
//
//				while (true) {
//					socket = serverSocket.accept();
//					FileTxThread fileTxThread = new FileTxThread(socket);
//					fileTxThread.start();
//				}

				File file = new File(
						Environment.getExternalStorageDirectory(),
						"recording.mp3");

				if(!file.exists())
					file.createNewFile();


				ServerSocket serverSocket = null;

				try {
					serverSocket = new ServerSocket(8081);
				} catch (IOException ex) {
					System.out.println("Can't setup server on this port number. ");
				}



				try {
					while (true) {
					socket = serverSocket.accept();
					FileTxThread fileTxThread = new FileTxThread(socket);
					fileTxThread.start();
				}
				} catch (IOException ex) {
					System.out.println("Can't accept client connection. ");
				}




			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	public class FileTxThread extends Thread {
		Socket socket;
		InputStream in = null;
		OutputStream out = null;
		
		FileTxThread(Socket socket){
			this.socket= socket;
		}

		@Override
		public void run() {
			File file = new File(
					Environment.getExternalStorageDirectory(), 
					"recording.mp3");


			if(!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

			try {
				in = socket.getInputStream();


			try {
				out = new FileOutputStream(file.getAbsolutePath());
			} catch (FileNotFoundException ex) {
				System.out.println("File not found. ");
			}

			byte[] bytes = new byte[16*1024];

			int count;
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
			}

			out.close();
			in.close();
//			socket.close();
//			serverSocket.close();


//			byte[] bytes = new byte[(int) file.length()];
//			if(!file.exists())
//				try {
//					file.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			BufferedInputStream bis;
//			try {
//				if(!file.exists())
//					return;
//				bis = new BufferedInputStream(new FileInputStream(file));
//				bis.read(bytes, 0, bytes.length);
//				OutputStream os = socket.getOutputStream();
//				os.write(bytes, 0, bytes.length);
//				os.flush();
//				socket.close();
//
//				final String sentMsg = "File received from to: " + socket.getInetAddress();
//				MainActivity.this.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						Toast.makeText(MainActivity.this,
//								sentMsg,
//								Toast.LENGTH_LONG).show();
//					}});
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
