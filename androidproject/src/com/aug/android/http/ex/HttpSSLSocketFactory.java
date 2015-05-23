package com.aug.android.http.ex;

import com.aug.android.http.utils.LogUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 和库提供的MySSLSocketFactory稍微有点不同：
 * 特殊解决方案，解决android 2.3 SSL不能重用session问题
 */
public class HttpSSLSocketFactory extends javax.net.ssl.SSLSocketFactory {

	protected static final String TAG = "HTTPSSLSocketFactory";

	public SSLContext mSSLContext;

	TrustManager mTm = new X509TrustManager() {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	public HttpSSLSocketFactory() {
		super();
		try {
			mSSLContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e1) {
			LogUtils.v(e1.toString());
		}

		try {
			mSSLContext.init(null, new TrustManager[] { mTm }, null);
		} catch (KeyManagementException e) {
			LogUtils.v(e.toString());
		}
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {

		InetAddress address = InetAddress.getByName(host);

		Socket sk = mSSLContext.getSocketFactory().createSocket(socket, host,
				port, autoClose);

		// 特殊解决方案，解决android 2.3 SSL不能重用session问题
		try {
			Field implFiled = Socket.class.getDeclaredField("impl");
			implFiled.setAccessible(true);
			SocketImpl impl = (SocketImpl) implFiled.get(sk);

			Field addFiled = SocketImpl.class.getDeclaredField("address");
			addFiled.setAccessible(true);
			addFiled.set(impl, address);

			Field portFiled = SocketImpl.class.getDeclaredField("port");
			portFiled.setAccessible(true);
			portFiled.set(impl, port);

		} catch (NoSuchFieldException e) {
			LogUtils.v(e.toString());
		} catch (IllegalArgumentException e) {
			LogUtils.v(e.toString());
		} catch (IllegalAccessException e) {
			LogUtils.v(e.toString());
		}

		return sk;
	}

	public SSLContext getSSLContext() {
		return mSSLContext;
	}

	@Override
	public Socket createSocket() throws IOException {
		return mSSLContext.getSocketFactory().createSocket();
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return mSSLContext.getSocketFactory().getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return mSSLContext.getSocketFactory().getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException {

		return null;
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return null;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost,
			int localPort) throws IOException, UnknownHostException {
		return null;
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
			int arg3) throws IOException {
		return null;
	}

}
