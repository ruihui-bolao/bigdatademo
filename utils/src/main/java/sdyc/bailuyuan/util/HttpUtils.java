package sdyc.bailuyuan.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class HttpUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);    //日志记录

	public static CloseableHttpClient getHttpClient() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
				.<ConnectionSocketFactory> create();
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);
		// 指定信任密钥存储对象和连接套接字工厂
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			// 信任任何链接
			TrustStrategy anyTrustStrategy = new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] x509Certificates,
						String s) {
					return true;
				}
			};
			SSLContext sslContext = SSLContexts.custom().useTLS()
					.loadTrustMaterial(trustStore, anyTrustStrategy).build();
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(
					sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			registryBuilder.register("https", sslSF);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		// 设置连接管理器
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
				registry);
		// connManager.setDefaultConnectionConfig(connConfig);
		// connManager.setDefaultSocketConfig(socketConfig);
		// 构建客户端
		return HttpClientBuilder.create().setConnectionManager(connManager)
				.build();
	}

	/**
	 * get请求
	 * @param urlWithParams  请求url
	 * @throws Exception
     */
	public static void requestGet(String urlWithParams) throws Exception {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		HttpGet httpget = new HttpGet(urlWithParams);

		//配置请求的超时设置
		/*RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(500)
				.setConnectTimeout(500)
				.setSocketTimeout(500).build();
		httpget.setConfig(requestConfig);*/

		CloseableHttpResponse response = httpclient.execute(httpget);

		logger.info("StatusCode -> " + response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		String jsonStr = EntityUtils.toString(entity, "utf-8");

		logger.info(jsonStr);

		httpget.releaseConnection();
	}

	/**
	 * post请求
	 * @param urlWithParams  请求url
	 * @throws Exception
     */
	public static void requestPost(String urlWithParams) throws Exception {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		HttpPost httppost = new HttpPost(urlWithParams);

		CloseableHttpResponse response = httpclient.execute(httppost);

		logger.info(response.toString());

		HttpEntity entity = response.getEntity();
		String jsonStr = EntityUtils.toString(entity, "utf-8");

		logger.info(jsonStr);

		httppost.releaseConnection();
	}

	public static void main(String[] args) {

		try {

			//post请求
			requestPost("http://localhost:8521/webserver/td/queryUserByPost");

			//get请求
			//requestGet("http://localhost:8521/webserver/td/receive?taskid=134058&isSuccess=true");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
