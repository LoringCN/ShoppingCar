package cn.huace.common.utils;
import cn.huace.common.config.SystemConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 读取网络内容
 * 
 * @author huangdan
 */

public class HttpHandler {
	
	
	private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
	
	 /**
     * Get:访问数据库并返回数据字符串
     *
     * @param url
     * @return String 数据字符串
     * @throws Exception
     */
     public static String doGet(String url) throws Exception{
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        //设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse httpResp = httpclient.execute(httpGet);
        try {
            int statusCode = httpResp.getStatusLine().getStatusCode();
            // 判断是够请求成功
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("状态码:" + statusCode);
                System.out.println("请求成功!");
                // 获取返回的数据
                result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
				System.out.println("result:" + result);
            } else {
                System.out.println("状态码:"
                        + httpResp.getStatusLine().getStatusCode());
                System.out.println("HttpGet方式请求失败!");
            }
        } finally {
            httpResp.close();
            httpclient.close();
        }
        return result;
     }
	
	
     /**
      * Post:访问数据库并返回数据字符串
      *
      * @param params
      *            向服务器端传的参数
      * @param url
      * @return String 数据字符串
      * @throws Exception
      */
     public static String doPost(List<NameValuePair> params, String url) throws Exception {
         String result = null;
         CloseableHttpClient httpclient = HttpClients.createDefault();
         HttpPost httpPost = new HttpPost(url);
         httpPost.setEntity(new UrlEncodedFormEntity(params));
         //设置请求和传输超时时间
         httpPost.setConfig(requestConfig);
         CloseableHttpResponse httpResp = httpclient.execute(httpPost);
         try {
             int statusCode = httpResp.getStatusLine().getStatusCode();
             // 判断是够请求成功
             if (statusCode == HttpStatus.SC_OK) {
                 System.out.println("状态码:" + statusCode);
                 System.out.println("请求成功!");
                 // 获取返回的数据
                 result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
             } else {
                 System.out.println("状态码:"
                         + httpResp.getStatusLine().getStatusCode());
                 System.out.println("HttpPost方式请求失败!");
             }
         } finally {
             httpResp.close();
             httpclient.close();
         }
         return result;
     }
     
     
	public static String post(String uri, String data)  {
		BufferedReader reader=null;
		OutputStream output=null;
     	try {
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(100000);
			connection.setReadTimeout(100000);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setDefaultUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
			((HttpURLConnection) connection).setRequestMethod("POST");
			 output = connection.getOutputStream();
			if (null != data) {
				byte[] b = data.toString().getBytes("utf-8");
				output.write(b, 0, b.length);
			}
			output.flush();
			output.close();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer content = new StringBuffer();
			String lines;
			while ((lines = reader.readLine()) != null) {
				content.append(lines);
			}
			reader.close();
			url = null;
			String msg = content.toString();

			return msg;
		}catch (Exception ex){
			return null;
		}
		finally {
			try {
				if (output != null) {
					output.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private static class TrustAnyTrustManager implements X509TrustManager {


		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}


		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}


		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * post方式请求服务器(https协议)
	 * 
	 * @param url
	 *            请求地址
	 * @param content
	 *            参数
	 * @param charset
	 *            编码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 */
	public static String post(String url, String content, String charset)
			throws NoSuchAlgorithmException, KeyManagementException, IOException {
		if (charset == null)
			charset = "utf-8";
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

		URL console = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		if (content != null)
			out.write(content.getBytes(charset));
		// 刷新、关闭
		out.flush();
		out.close();
		InputStream is = conn.getInputStream();
		if (is != null) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			return new String(outStream.toByteArray());
		}
		return null;
	}
    public static boolean downPicFromHttp(String url, String path) {
        HttpClient httpClient = null;
        try {
			File storeFile=new File(SystemConfig.getInstance().getFileTemp()+path);
			if(!storeFile.getParentFile().exists()){
				storeFile.getParentFile().mkdirs();
			}
            httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                    12 * 1000);
            HttpConnectionParams.setSoTimeout(params, 12 * 1000);

            HttpGet getMethod = new HttpGet(url);
            HttpResponse response = httpClient.execute(getMethod);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                FileOutputStream output = new FileOutputStream(storeFile);
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    
                    byte b[] = new byte[1024];
                    int j = 0;
                    while( (j = instream.read(b))!=-1){
                    output.write(b,0,j);
                    }
                    output.flush();
                    output.close();
                    return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
	public static String  httpsPost(String url,String content)throws Exception{
		String result = null;
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			// 默认信任所有证书
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(content, Charset.forName("UTF-8")));
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				int statusCode = response.getStatusLine().getStatusCode();
				// 判断是够请求成功
				if (statusCode == HttpStatus.SC_OK) {
					System.out.println("状态码:" + statusCode);
					System.out.println("请求成功!");
					// 获取返回的数据
					result = EntityUtils.toString(response.getEntity(), "UTF-8");
					System.out.println("result:" + result);
				} else {
					System.out.println("状态码:"
							+ response.getStatusLine().getStatusCode());
					System.out.println("HttpGet方式请求失败!");
				}

			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return result;
	}

}
