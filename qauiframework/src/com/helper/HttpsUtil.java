package com.helper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class HttpsUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpsUtil.class);
	public static int serverTimeout;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final static String CR_LF = "\r\n";
	private final static String TWO_DASHES = "--";
	public static boolean HttpsDebug = true;
	public final static String JSONOBJECT = "jsonObject";
	public final static String RESULT = "result";
	public final static String RESPOSETIME = "resposetime";
	public final static String RESPOSECODE = "resposeCode";

	/**
	 * 发送POST请求
	 * 
	 * @param server
	 * @param api
	 * @param params
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendPostData(String server, String api, Object params,
			Map<String, String> headers) {
		Map<String, Object> resultMAP = new HashMap<>();
		String result = null;
		InputStream inptStream = null;
		InputStream errorStream = null;
		OutputStream outputStream = null;
		URL url = null;
		long Stime = 0, Etime = 0;
		int resposeCode = -1;
		JSONObject jsonObject = null;
		// exception
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			jsonObject = new JSONObject("{\"status\":-102,\"message\":\"*Exception\"}");
			if (server.toLowerCase().startsWith("https")) {
				url = new URL(server + api);
				HttpsURLConnection httpsURLConnection = HttpsObj(url, "POST", headers);
				byte[] data = null;
				if (params instanceof String) {
					data = ((String) params).getBytes("UTF-8");
					httpsURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
				} else {
					data = getRequestData((Map<String, Object>) params).getBytes("UTF-8");
					httpsURLConnection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded;charset=utf-8");
				}
				httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
				Stime = new Date().getTime();// 开始时间
				outputStream = httpsURLConnection.getOutputStream();
				outputStream.write(data);
				outputStream.flush();
				resposeCode = httpsURLConnection.getResponseCode();
				Etime = new Date().getTime();// 结束时间
				String encode = httpsURLConnection.getContentEncoding();
				if (resposeCode == HttpsURLConnection.HTTP_OK) {
					inptStream = httpsURLConnection.getInputStream();
					result = dealResponseResult(inptStream, encode);
					// resposetimeout
					jsonObject = isResposeTimeout(api, Stime - Etime);
					if (jsonObject == null) {
						jsonObject = new JSONObject(result);
					}
				} else {
					errorStream = httpsURLConnection.getErrorStream();
					result = dealResponseResult(errorStream, encode);
					jsonObject = new JSONObject("{\"status\":-100,\"message\":\"*HttpFail=" + resposeCode + "\"}");
				}
				httpsURLConnection.disconnect();
			} else {
				url = new URL(server + api);
				HttpURLConnection httpURLConnection = HttpObj(url, "POST", headers);
				byte[] data = null;
				if (params instanceof String) {// 发送数据类型
					data = ((String) params).getBytes("UTF-8");
					httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
				} else {
					data = getRequestData((Map<String, Object>) params).getBytes("UTF-8");
					httpURLConnection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded;charset=utf-8");
				}
				httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
				Stime = new Date().getTime();
				outputStream = httpURLConnection.getOutputStream();
				outputStream.write(data);
				outputStream.flush();
				resposeCode = httpURLConnection.getResponseCode();
				Etime = new Date().getTime();
				String encode = httpURLConnection.getContentEncoding();
				if (resposeCode == HttpsURLConnection.HTTP_OK) {
					inptStream = httpURLConnection.getInputStream();
					result = dealResponseResult(inptStream, encode);
					// resposetimeout
					jsonObject = isResposeTimeout(api, Stime - Etime);
					if (jsonObject == null) {
						jsonObject = new JSONObject(result);
					}
				} else {
					errorStream = httpURLConnection.getErrorStream();
					result = dealResponseResult(errorStream, encode);
					jsonObject = new JSONObject("{\"status\":-100,\"message\":\"*HttpFail=" + resposeCode + "\"}");
				}
				httpURLConnection.disconnect();
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} finally {
			try {
				if (inptStream != null) {
					inptStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (errorStream != null) {
					errorStream.close();
				}
				if (sw != null) {
					sw.close();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (IOException e) {
				logger.error("Exception", e);
			}

		}
		HttpsDebug(server, api, "sendPostData", result, resposeCode, Etime - Stime);
		resultMAP.put(JSONOBJECT, jsonObject);
		resultMAP.put(RESULT, result);
		resultMAP.put(RESPOSETIME, (Etime - Stime) + "");
		resultMAP.put(RESPOSECODE, resposeCode);
		return resultMAP;
	}

	/**
	 * 发送GET请求
	 * 
	 * @param server
	 * @param api
	 * @param params
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendGetData(String server, String api, Map<String, Object> params,
			Map<String, String> headers) {
		Map<String, Object> resultMAP = new HashMap<>();
		String result = null;
		InputStream inptStream = null;
		InputStream errorStream = null;
		OutputStream outputStream = null;
		URL url = null;
		long Stime = 0, Etime = 0;
		int resposeCode = -1;
		JSONObject jsonObject = null;
		// exception
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			jsonObject = new JSONObject("{\"status\":-102,\"message\":\"*Exception\"}");
			if (server.toLowerCase().startsWith("https")) {
				if (params == null) {
					url = new URL(server + api);
				} else {
					url = new URL(server + api + "?" + getRequestData(params));
				}
				HttpsURLConnection httpsURLConnection = HttpsObj(url, "GET", headers);
				httpsURLConnection.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
				Stime = new Date().getTime();// 开始时间
				httpsURLConnection.connect();
				resposeCode = httpsURLConnection.getResponseCode();
				Etime = new Date().getTime();// 结束时间
				String encode = httpsURLConnection.getContentEncoding();
				if (resposeCode == HttpsURLConnection.HTTP_OK) {
					inptStream = httpsURLConnection.getInputStream();
					result = dealResponseResult(inptStream, encode);
					// resposetimeout
					jsonObject = isResposeTimeout(api, Stime - Etime);
					if (jsonObject == null) {
						jsonObject = new JSONObject(result);
					}
				} else {
					errorStream = httpsURLConnection.getErrorStream();
					result = dealResponseResult(errorStream, encode);
					jsonObject = new JSONObject("{\"status\":-100,\"message\":\"*HttpFail=" + resposeCode + "\"}");
				}
				httpsURLConnection.disconnect();
			} else {
				if (params == null) {
					url = new URL(server + api);
				} else {
					url = new URL(server + api + "?" + getRequestData(params));
				}
				HttpURLConnection httpURLConnection = HttpObj(url, "GET", headers);
				httpURLConnection.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
				Stime = new Date().getTime();
				httpURLConnection.connect();
				resposeCode = httpURLConnection.getResponseCode();
				Etime = new Date().getTime();
				String encode = httpURLConnection.getContentEncoding();
				if (resposeCode == HttpsURLConnection.HTTP_OK) {
					inptStream = httpURLConnection.getInputStream();
					result = dealResponseResult(inptStream, encode);
					// resposetimeout
					jsonObject = isResposeTimeout(api, Stime - Etime);
					if (jsonObject == null) {
						jsonObject = new JSONObject(result);
					}
				} else {
					errorStream = httpURLConnection.getErrorStream();
					result = dealResponseResult(errorStream, encode);
					jsonObject = new JSONObject("{\"status\":-100,\"message\":\"*HttpFail=" + resposeCode + "\"}");
				}
				httpURLConnection.disconnect();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} finally {
			try {
				if (inptStream != null) {
					inptStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (errorStream != null) {
					errorStream.close();
				}
			} catch (IOException e) {
				logger.error("Exception", e);
			}

		}
		HttpsDebug(server, api, "sendGETData", result, resposeCode, Etime - Stime);
		resultMAP.put(JSONOBJECT, jsonObject);
		resultMAP.put(RESULT, result);
		resultMAP.put(RESPOSETIME, (Etime - Stime) + "");
		resultMAP.put(RESPOSECODE, resposeCode);
		return resultMAP;
	}

	/**
	 * 发送表单请求,上传文件
	 * 
	 * @param server
	 * @param api
	 * @param formdata
	 * @param uploadfile
	 * @param headers
	 * @return
	 */
	public static Map<String, Object> sendFormFile(String server, String api, String formdata, String uploadfile,
			Map<String, String> headers) {
		Map<String, Object> resultMAP = new HashMap<>();
		String BOUNDARY = setBOUNDARY();
		String result = null;
		InputStream inptStream = null;
		InputStream errorStream = null;
		OutputStream outputStream = null;
		FileInputStream fStream = null;
		URL url = null;
		long Stime = 0, Etime = 0;
		int resposeCode = -1;
		JSONObject jsonObject = null;
		// exception
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			jsonObject = new JSONObject("{\"status\":-102,\"message\":\"*Exception\"}");
			if (server.toLowerCase().startsWith("https")) {
				url = new URL(server + api);
				HttpsURLConnection httpsURLConnection = HttpsObj(url, "POST", headers);
				httpsURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY + "; charset=UTF-8");
				Stime = new Date().getTime();// 开始时间
				// 表单
				outputStream = httpsURLConnection.getOutputStream();
				StringBuffer form = new StringBuffer();
				form.append(TWO_DASHES + BOUNDARY + CR_LF);
				form.append(formdata);
				form.append(CR_LF);
				// form.append(TWO_DASHES + BOUNDARY + CR_LF);
				// form.append("Content-Disposition:form-data;name=\"upload\";filename=\"test.jpg\""
				// + CR_LF);
				// form.append("Content-Type:image/jpeg; charset=UTF-8"+ CR_LF);
				// form.append("Content-Transfer-Encoding: binary"+ CR_LF);
				// form.append(CR_LF);
				outputStream.write(form.toString().getBytes("UTF-8"));
				fStream = new FileInputStream(uploadfile);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.write(CR_LF.getBytes("UTF-8"));
				outputStream.write((TWO_DASHES + BOUNDARY + TWO_DASHES + CR_LF).getBytes("UTF-8"));
				outputStream.flush();
				resposeCode = httpsURLConnection.getResponseCode(); // 响应码
				Etime = new Date().getTime();// 结束时间
				String encode = httpsURLConnection.getContentEncoding();
				if (resposeCode == HttpsURLConnection.HTTP_OK) {
					inptStream = httpsURLConnection.getInputStream();
					result = dealResponseResult(inptStream, encode); // 获取返回值
					// resposetimeout
					jsonObject = isResposeTimeout(api, Stime - Etime);
					if (jsonObject == null) {
						jsonObject = new JSONObject(result);
					}
				} else {
					errorStream = httpsURLConnection.getErrorStream();
					result = dealResponseResult(errorStream, encode);
					jsonObject = new JSONObject("{\"status\":-100,\"message\":\"*HttpFail=" + resposeCode + "\"}");
				}
				httpsURLConnection.disconnect();
			} else {
				url = new URL(server + api);
				HttpURLConnection httpURLConnection = HttpObj(url, "POST", headers);

				httpURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY + "; charset=UTF-8");
				Stime = new Date().getTime();// 开始时间
				// 表单
				outputStream = httpURLConnection.getOutputStream();
				StringBuffer form = new StringBuffer();
				form.append(TWO_DASHES + BOUNDARY + CR_LF);
				form.append(formdata);
				form.append(CR_LF);
				outputStream.write(form.toString().getBytes("UTF-8"));
				fStream = new FileInputStream(uploadfile);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.write(CR_LF.getBytes("UTF-8"));
				outputStream.write((TWO_DASHES + BOUNDARY + TWO_DASHES + CR_LF).getBytes("UTF-8"));
				outputStream.flush();
				resposeCode = httpURLConnection.getResponseCode();
				Etime = new Date().getTime();// 结束时间
				String encode = httpURLConnection.getContentEncoding();
				if (resposeCode == HttpsURLConnection.HTTP_OK) {
					inptStream = httpURLConnection.getInputStream();
					result = dealResponseResult(inptStream, encode);
					// resposetimeout
					jsonObject = isResposeTimeout(api, Stime - Etime);
					if (jsonObject == null) {
						jsonObject = new JSONObject(result);
					}
				} else {
					errorStream = httpURLConnection.getErrorStream();
					result = dealResponseResult(errorStream, encode);
					jsonObject = new JSONObject("{\"status\":-100,\"message\":\"*HttpFail=" + resposeCode + "\"}");
				}
				httpURLConnection.disconnect();
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			e.printStackTrace(pw);
			result = sw.toString();
		} finally {
			try {
				if (inptStream != null) {
					inptStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (fStream != null) {
					fStream.close();
				}
				if (errorStream != null) {
					errorStream.close();
				}
				if (sw != null) {
					sw.close();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (IOException e) {
				logger.error("Exception", e);
			}

		}
		HttpsDebug(server, api, "sendFormData", result, resposeCode, Etime - Stime);
		resultMAP.put(JSONOBJECT, jsonObject);
		resultMAP.put(RESULT, result);
		resultMAP.put(RESPOSETIME, (Etime - Stime) + "");
		resultMAP.put(RESPOSECODE, resposeCode);
		return resultMAP;
	}

	// URL参数拼接
	public static String getRequestData(Map<String, Object> params) {
		StringBuffer stringBuffer = new StringBuffer(); //
		try {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue() + "", "UTF-8")).append("&");
			}
			if (stringBuffer.length() > 0) {
				stringBuffer.deleteCharAt(stringBuffer.length() - 1); //
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.info(stringBuffer.toString());
		return stringBuffer.toString();
	}

	// 返回结果转化
	public static String dealResponseResult(InputStream inputStream, String encode) {
		if (inputStream != null) {
			String resultData = null; //
			GZIPInputStream gZIPInputStream = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;
			try {
				if (encode != null && encode.equals("gzip")) {
					gZIPInputStream = new GZIPInputStream(inputStream);// gzip
					while ((len = gZIPInputStream.read(data)) != -1) {
						byteArrayOutputStream.write(data, 0, len);
					}
				} else {
					while ((len = inputStream.read(data)) != -1) {
						byteArrayOutputStream.write(data, 0, len);
					}
				}
			} catch (IOException e) {
				logger.error("Exception", e);
			} finally {
				try {
					if (byteArrayOutputStream != null) {
						byteArrayOutputStream.close();
					}
					if (gZIPInputStream != null) {
						gZIPInputStream.close();
					}
				} catch (IOException e) {
					logger.error("Exception", e);
				}
			}
			try {
				resultData = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
				byteArrayOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
			return resultData;
		} else {
			return null;
		}
	}

	// MYX509协议
	public static class MyX509TrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	// HTTP随机码
	public static String setBOUNDARY() {
		char[] MULTIPART_CHARS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder buffer = new StringBuilder();
		Random rand = new Random();
		int count = rand.nextInt(11) + 30; // a random size from 30 to 40
		for (int i = 0; i < count; i++) {
			buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		String BOUNDARY = buffer.toString();
		logger.info("BOUNDARY= " + BOUNDARY);
		return BOUNDARY;
	}

	public static JSONObject isResposeTimeout(String api, long resposetime) {
		JSONObject jsonObject = null;
		return jsonObject;
	}

	public static void HttpsDebug(String server, String api, String method, String result, int resposeCode,
			long resposetime) {
		if (HttpsDebug) {
			logger.info("Server=" + server + ",API=" + api);
			logger.info("API=" + api + ",Resposetime=" + resposetime + "ms,Code=" + resposeCode + ",type=" + method);
			logger.info("API=" + api + ",result=" + result);
		}
	}

	public static HttpsURLConnection HttpsObj(URL url, String RequestMethod, Map<String, String> headers)
			throws IOException, NoSuchAlgorithmException, KeyManagementException {
		HttpsURLConnection httpsURLConnection = null;
		httpsURLConnection = (HttpsURLConnection) url.openConnection();
		httpsURLConnection.setConnectTimeout(serverTimeout); // 连接超时
		httpsURLConnection.setReadTimeout(serverTimeout);
		httpsURLConnection.setDoInput(true); // http正文内，因此需要设为true, 默认情况下是false;
		httpsURLConnection.setDoOutput(true); // 设置是否从httpUrlConnection读入，默认情况下是true;
		httpsURLConnection.setRequestMethod(RequestMethod);
		httpsURLConnection.setUseCaches(false); // Post 请求不能使用缓存
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		// SSLContext
		TrustManager[] tm = { new MyX509TrustManager() };
		SSLContext sslContext;
		sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// SSLSocketFactory
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		httpsURLConnection.setSSLSocketFactory(ssf);
		return httpsURLConnection;
	}

	public static HttpURLConnection HttpObj(URL url, String RequestMethod, Map<String, String> headers)
			throws IOException {
		HttpURLConnection httpURLConnection = null;
		httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(serverTimeout);
		httpURLConnection.setReadTimeout(serverTimeout);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod(RequestMethod);
		httpURLConnection.setUseCaches(false);
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return httpURLConnection;
	}

}
