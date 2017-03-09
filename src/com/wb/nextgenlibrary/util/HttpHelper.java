package com.wb.nextgenlibrary.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.network.SecureFlxHttpClient;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class HttpHelper {
	public static final int SOCKET_TIMEOUT_LONG = 180000;
	public static final int TIMEOUT_HTTP_POST = 40000;
	private static final int TIMEOUT_CONNECTION = 10000;
	private static final int TIMEOUT_HEADER = 500;
	private static final int TIMEOUT_READ = 60000;

	private static final String ACCEPT_HEADER_VALUE = "application/vnd.fv-v2.6+json";//"application/json";


	public static String fetchUrl(URL url, boolean checkCache, boolean shouldCache) throws IOException {
		byte[] responseBody = fetchUrlBytes(url, checkCache, shouldCache, false);
		String responseString = new String(responseBody, "UTF-8"); // hmmm don't think so
//		String responseString = new String(responseBody, "windows-1252"); // Microsoft!...0x92 == stylized ' in Windows
		return responseString;
	}

	public static String fetchFileLastModifiedDateFromUrl(URL url) throws IOException {
		NextGenLogger.d(F.TAG_API, "HttpHelper.fetchFileLastModifiedDateFromUrl " + url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(TIMEOUT_HEADER);
		connection.setReadTimeout(TIMEOUT_HEADER);
		String lastModified = connection.getHeaderField("Last-Modified");
		connection.disconnect();
		return lastModified;
	}

	public static String fetchUrl(URL url, boolean checkCache, boolean shouldCache, boolean shouldAuth)
			throws IOException {
		byte[] responseBody = fetchUrlBytes(url, checkCache, shouldCache, false, shouldAuth);
//		String responseString = new String(responseBody, "windows-1252"); // Microsoft!...0x92 == stylized ' in Windows
		String responseString = new String(responseBody, "UTF-8");
		//Log.d("response",responseString);
		return responseString;
	}
	
	protected static byte[] fetchUrlBytes(URL url, boolean checkCache, boolean shouldCache, boolean useFlixsterUa)
			throws IOException {
		return fetchUrlBytes(url, checkCache, shouldCache, useFlixsterUa, false);
	}
	
	/* Protected for HttpImageHelper */
	protected static byte[] fetchUrlBytes(URL url, boolean checkCache, boolean shouldCache, boolean useFlixsterUa,
			boolean shouldAuth) throws IOException {
		NextGenLogger.d(F.TAG_API, "HttpHelper.fetchUrlBytes " + (checkCache ? "checkCache " : "")
				+ (shouldCache ? "shouldCache " : "") + url);
		byte[] result;
		if (checkCache) {
			result = NextGenExperience.sCacheManager.get(url.hashCode());
			if (result != null && result.length > 0) {
				//Logger.v(F.TAG_API, "HttpHelper.fetchUrlBytes cache hit");
				return result;
			}
			//Logger.v(F.TAG_API, "HttpHelper.fetchUrlBytes cache miss");
		}
		HttpURLConnection connection = null;
		if (url.getProtocol().toLowerCase().equals("https")) {
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			connection = https;
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		connection.setRequestProperty(F.ACCEPT, ACCEPT_HEADER_VALUE);
		if (shouldAuth)
			connection.setRequestProperty(F.AUTHORIZATION, getAuthHeader());
		try {
			result = fetchUrlBytes(connection, useFlixsterUa);
		} finally {
			connection.disconnect();
		}
		if (shouldCache) {
			NextGenExperience.sCacheManager.put(url.hashCode(), result);
		}
		
		return result;
	}

	/**
	 * Public for Netflix to call using oauth "signed" connections. Caller is responsible for calling disconnect() to
	 * free all resources held by the connection
	 */
	public static byte[] fetchUrlBytes(HttpURLConnection connection) throws IOException {
		return fetchUrlBytes(connection, false);
	}
	
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
			
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				
			}
			
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				
			}
		} };
		
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			SecureRandom sr = SecureRandom.getInstance( "SHA1PRNG", "Crypto" );
			sc.init(null, trustAllCerts, sr);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] fetchUrlBytes(HttpURLConnection connection, boolean useFlixsterUa) throws IOException {
		String userAgent = NextGenExperience.getUserAgent();
		connection.setRequestProperty(HTTP.USER_AGENT, userAgent);
		//connection.setRequestProperty("Accept", "application/vnd.dc2-v2.3.0+json");
		connection.setConnectTimeout(TIMEOUT_CONNECTION);
		connection.setReadTimeout(TIMEOUT_READ);
		
		String contentEncoding = connection.getContentEncoding();
		InputStream tempStream = null;
		try {
			tempStream = connection.getInputStream();
		} catch (IOException ioe) {
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
				//NextGenExperience.logout(true);
//				throw new HttpUnauthorizedException(connection.getResponseMessage(), ioe);
				
			} else {
				tempStream = connection.getErrorStream();
				//Logger.d(F.TAG_API, "Throwing " + ioe.getCause() + ", responseCode: " + responseCode);
				//throw ioe;
			}
		} catch (IllegalStateException ise) {
			throw new IOException(ise.getMessage());
		}
		if (tempStream == null) {
			throw new IOException("HttpHelper.fetchUrlBytes connection stream null");
		}
		tempStream = "gzip".equalsIgnoreCase(contentEncoding) ? new GZIPInputStream(tempStream) : tempStream;
		return streamToByteArray(tempStream);
	}
	
	public static String postToUrl(String url, List<NameValuePair> parameters) throws IOException {
		NextGenLogger.d(F.TAG_API, "HttpHelper.postToUrl url:" + url + " postValues:" + parameters);
		InputStream content = null;
		try {
			HttpClient httpClient = isSecureFlxUrl(url) ? new SecureFlxHttpClient()
					: new DefaultHttpClient();
			HttpParams clientParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(clientParameters, TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(clientParameters, TIMEOUT_HTTP_POST);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(parameters));
			HttpResponse response = httpClient.execute(post);
			StringBuilder responseBuilder = new StringBuilder();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line).append("\n");
				}
			}
			NextGenLogger.d(F.TAG_API, "HttpHelper.postToUrl response code:" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				//FlixsterApplication.logout(true);
//				throw new HttpUnauthorizedException(responseBuilder.toString().trim());
				
			}
			return responseBuilder.toString().trim();
		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
					/* Ignored */
				}
			}
		}
	}
	
	public static String postJSONToUrl(String url, JSONObject json, boolean shouldAuth) throws IOException {
		String jsonString = StringHelper.unescapeForwardSlash(json.toString());
		return postJSONToUrl(url, jsonString, shouldAuth, TIMEOUT_HTTP_POST);
	}
	
	
	public static String postJSONToUrl(String url, String requestString, boolean shouldAuth) throws IOException {
		return postJSONToUrl(url, requestString, shouldAuth, TIMEOUT_HTTP_POST);
	}
	
	public static String postJSONToUrl(String url, String requestString, boolean shouldAuth, int timeoutInMillis) throws IOException {

		NextGenLogger.d(F.TAG_API, "HttpHelper.postToUrl url:" + url + " postValues:" + requestString);
		InputStream content = null;
		try {
			HttpClient httpClient = isSecureFlxUrl(url) ? new SecureFlxHttpClient()
					: new DefaultHttpClient();
			
			HttpParams clientParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(clientParameters, TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(clientParameters, timeoutInMillis);
			HttpPost post = new HttpPost(url);
			StringEntity entityJson = new StringEntity(requestString, "UTF-8");		//accept non-unicode characters
			entityJson.setContentType("application/json");
			if (shouldAuth)
				post.setHeader(F.AUTHORIZATION, getAuthHeader());
			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
			post.setHeader(HTTP.USER_AGENT, NextGenExperience.getUserAgent());
			post.setHeader(F.ACCEPT, ACCEPT_HEADER_VALUE);
			//post.setHeader("Accept", "application/json");
			//post.setHeader(FlixsterApplication.PREFS_FLIX_USER_COUNTRY, FlixsterApplication.getUserCountry());
			//post.setHeader(F.X_FORWARDED_FOR, "23.72.0.0");		// default US ip
			//post.setHeader(F.X_FORWARDED_FOR, "0.0.0.0");		// default GB ip
			/*if (!StringHelper.isEmpty(NextGenExperience.getClientCountryCode())){
				post.setHeader(F.CLIENT_COUNTRY, NextGenExperience.getClientCountryCode());
			}

			post.setHeader(F.CLIENT_LANGUAGE, NextGenExperience.getLocale().getLanguage());*/
			
			post.setEntity(entityJson);
			
			HttpResponse response = httpClient.execute(post);
			StringBuilder responseBuilder = new StringBuilder();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line).append("\n");
				}
			}
			NextGenLogger.d(F.TAG_API, "HttpHelper.postToUrl response code:" + response.getStatusLine().getStatusCode());
			NextGenLogger.d(F.TAG_API, "HttpHelper.postToUrl response body:" + responseBuilder.toString().trim());
			
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				//NextGenExperience.logout(true);
	//			throw new HttpUnauthorizedException(responseBuilder.toString().trim());
			}
			return responseBuilder.toString().trim();
		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
					/* Ignored */
				}
			}
		}
	}

	public static String getFromUrl(String url, List <NameValuePair> params) throws IOException {
		return  getFromUrl(url, params, null, true, true);
	}
	
	public static String getFromUrl(String url, List <NameValuePair> params, List <NameValuePair> headerValues, boolean checkCache, boolean shouldCache) throws IOException {
		String result;


		NextGenLogger.d(F.TAG_API, "HttpHelper.getFromUrl url:" + url);
		InputStream content = null;
		HttpClient httpClient = null;
		try {
			httpClient = isSecureFlxUrl(url) ? new SecureFlxHttpClient()
					: new DefaultHttpClient();

			HttpParams clientParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(clientParameters, TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(clientParameters, TIMEOUT_HTTP_POST);

			if (params != null)
				url += "?" + URLEncodedUtils.format(params, "utf-8");

			if (checkCache) {
				byte[] cacheResult = NextGenExperience.sCacheManager.get(new URL(url).hashCode());
				try {
					if (cacheResult != null && cacheResult.length > 0)
						return new String(cacheResult);
				}catch (Exception ex){
					// not valid cache
				}
				//Logger.v(F.TAG_API, "HttpHelper.fetchUrlBytes cache miss");
			}

			HttpGet get = new HttpGet(url);
			//StringEntity entityJson = new StringEntity(json.toString());
			//entityJson.setContentType("application/json");
			/*if (shouldAuth)
				get.setHeader(F.AUTHORIZATION, getAuthHeader());*/
					get.setHeader(HTTP.CONTENT_TYPE, "application/json");
			get.setHeader(HTTP.USER_AGENT, NextGenExperience.getUserAgent());
			get.setHeader(F.ACCEPT, ACCEPT_HEADER_VALUE);

			if (headerValues != null && headerValues.size() > 0){
				for(NameValuePair pair: headerValues){
					get.setHeader(pair.getName(), pair.getValue());
				}
			}

			HttpResponse response = httpClient.execute(get);
			StringBuilder responseBuilder = new StringBuilder();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line).append("\n");
				}
			}
			//NextGenLogger.d(F.TAG_API, "HttpHelper.getFromUrl response code:" + response.getStatusLine().getStatusCode());
			//NextGenLogger.d(F.TAG_API, "HttpHelper.getFromUrl response body:" + responseBuilder.toString().trim());
			
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				//NextGenExperience.logout(true);
	//			throw new HttpUnauthorizedException(responseBuilder.toString().trim());
			}
			result =  responseBuilder.toString().trim();


			if (shouldCache) {

				NextGenExperience.sCacheManager.put(new URL(url).hashCode(), result.getBytes());
			}
			return result;


		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
					/* Ignored */
				}
			}
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	
	public static String deleteFromUrl(String url) throws IOException {
		return deleteFromUrl(url, false);
	}
	
	public static String deleteFromUrl(String url, boolean shouldAuth) throws IOException {
		NextGenLogger.d(F.TAG_API, "HttpHelper.deleteFromUrl url:" + url);
		InputStream content = null;
		try {
			HttpClient httpClient = isSecureFlxUrl(url) ? new SecureFlxHttpClient()
					: new DefaultHttpClient();
			
			HttpParams clientParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(clientParameters, TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(clientParameters, TIMEOUT_HTTP_POST);
			HttpDelete delete = new HttpDelete(url);
			if (shouldAuth)
				delete.setHeader(F.AUTHORIZATION, getAuthHeader());
			delete.setHeader(F.ACCEPT, ACCEPT_HEADER_VALUE);
			
			HttpResponse response = httpClient.execute(delete);
			StringBuilder responseBuilder = new StringBuilder();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line).append("\n");
				}
			}
			NextGenLogger.d(F.TAG_API, "HttpHelper.deleteFromUrl response code:" + response.getStatusLine().getStatusCode());
			
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				//NextGenExperience.logout(true);
				//throw new HttpUnauthorizedException(responseBuilder.toString().trim());
				
			}
			return responseBuilder.toString().trim();
		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
					/* Ignored */
				}
			}
		}
	}
	
	public static String putJSONToUrl(String url, JSONObject json, boolean shouldAuth) throws IOException {
		NextGenLogger.d(F.TAG_API, "HttpHelper.putToUrl url:" + url + " postValues:" + json);
		InputStream content = null;
		try {
			HttpClient httpClient = isSecureFlxUrl(url) ? new SecureFlxHttpClient()
					: new DefaultHttpClient();
			
			HttpParams clientParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(clientParameters, TIMEOUT_CONNECTION);
			HttpConnectionParams.setSoTimeout(clientParameters, TIMEOUT_HTTP_POST);
			HttpPut put = new HttpPut(url);
			StringEntity entityJson = new StringEntity(json.toString());
			if (shouldAuth)
				put.setHeader(F.AUTHORIZATION, getAuthHeader());
			put.setHeader(HTTP.CONTENT_TYPE, "application/json");
			put.setHeader(HTTP.USER_AGENT, NextGenExperience.getUserAgent());
			put.setHeader(F.ACCEPT, ACCEPT_HEADER_VALUE);
			//put.setHeader(FlixsterApplication.PREFS_FLIX_USER_COUNTRY, FlixsterApplication.getUserCountry());
			//put.setHeader("X-Forwarded-For", "62.143.129.19");
			put.setEntity(entityJson);
			
			HttpResponse response = httpClient.execute(put);
			StringBuilder responseBuilder = new StringBuilder();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line).append("\n");
				}
			}
			NextGenLogger.d(F.TAG_API, "HttpHelper.putToUrl response code:" + response.getStatusLine().getStatusCode());
			NextGenLogger.d(F.TAG_API, "HttpHelper.putToUrl response body:" + responseBuilder.toString().trim());
			
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				//NextGenExperience.logout(true);
	//			throw new HttpUnauthorizedException(responseBuilder.toString().trim());
			}
			return responseBuilder.toString().trim();
		} finally {
			if (content != null) {
				try {
					content.close();
				} catch (IOException e) {
					/* Ignored */
				}
			}
		}
	}
	
	private static String getAuthHeader() {
		return "";//NextGenExperience.getAuthToken() + ":" + NextGenExperience.getDeviceID();
	}
	
	public static long getRemoteFileSize(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		long filesize = 0;
		try {
			filesize = Long.parseLong(urlConnection.getHeaderField("content-length"));
		} catch (NumberFormatException ex) {}
		return filesize;
	}
	
	/* Helper methods */
	private static final int CHUNKSIZE = 8192; // size of fixed chunks
	private static final int BUFFERSIZE = 1024; // size of reading buffer

	private static final Object syncObject = new Object();
	public static byte[] streamToByteArray(InputStream is) throws IOException {
		synchronized (syncObject){
			int bytesRead = 0;
			byte[] buffer = new byte[BUFFERSIZE]; // initialize buffer
			byte[] fixedChunk = new byte[CHUNKSIZE]; // initialize 1st chunk
			ArrayList<byte[]> BufferChunkList = new ArrayList<byte[]>(); // List of chunk data
			int spaceLeft = CHUNKSIZE;
			int chunkIndex = 0;

			while ((bytesRead = is.read(buffer)) != -1) { // loop until the
				// DataInputStream is completed
				if (bytesRead > spaceLeft) {
					// copy to end of current chunk
					System.arraycopy(buffer, 0, fixedChunk, chunkIndex, spaceLeft);
					BufferChunkList.add(fixedChunk);

					// create a new chunk, and fill in the leftover
					fixedChunk = new byte[CHUNKSIZE];
					chunkIndex = bytesRead - spaceLeft;
					System.arraycopy(buffer, spaceLeft, fixedChunk, 0, chunkIndex);
				} else {
					// plenty of space, just copy it in
					System.arraycopy(buffer, 0, fixedChunk, chunkIndex, bytesRead);
					chunkIndex = chunkIndex + bytesRead;
				}
				spaceLeft = CHUNKSIZE - chunkIndex;
			}
			is.close();

			int responseSize = (BufferChunkList.size() * CHUNKSIZE) + chunkIndex;
			byte[] responseBody = new byte[responseSize];
			int index = 0;
			for (byte[] b : BufferChunkList) {
				System.arraycopy(b, 0, responseBody, index, CHUNKSIZE);
				index = index + CHUNKSIZE;
			}
			System.arraycopy(fixedChunk, 0, responseBody, index, chunkIndex);
			return responseBody;
		}
	}
	
	private static final String SECURE_BASE_URL = "https://";

	public static boolean isSecureFlxUrl(String url) {
	    return url.startsWith(SECURE_BASE_URL);
	}
}
