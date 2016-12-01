/*  
  Copyright (C) 2016 William Welna (wwelna@occultusterra.com)
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/

package com.occultusterra.curl;

import java.io.IOException;
import java.util.Map;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;


public class Curl implements AutoCloseable {
	private static curl_lib clib=curl_lib.INSTANCE;
	static int global_err;
	private Pointer curl_handle=Pointer.NULL;
	
	private int last_perform_error_code;
	private boolean multi_flag=false;
	
	private curl_memdatahandler header;
	private curl_memdatahandler body;
	
	private String body_tofile_file = "";
	
	private Pointer header_list = Pointer.NULL;
	
	static {
		global_err = clib.curl_global_init(0);
	}
	
	final public static int OK=curl_errors.CURLE_OK;
	final public static long HTTP_VERSION_NONE=curl_opts.CURL_HTTP_VERSION_NONE;
	final public static long HTTP_VERSION_1_0=curl_opts.CURL_HTTP_VERSION_1_0;
	final public static long HTTP_VERSION_1_1=curl_opts.CURL_HTTP_VERSION_1_1;
	final public static long HTTP_VERSION_2_0=curl_opts.CURL_HTTP_VERSION_2_0;
	
	public Curl() throws curlExceptionEasy {
		if(clib == Pointer.NULL || global_err!=0)
			throw new curlExceptionEasy("Issues Loading libCurl / curl_global_init()");
		curl_handle = clib.curl_easy_init();
		if(curl_handle == Pointer.NULL)
			throw new curlExceptionEasy("Null Pointer returned on curl_easy_init()");
		body = new curl_memdatahandler();
		header = new curl_memdatahandler();
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_HEADERFUNCTION, header);
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_FILETIME, true);
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_WRITEFUNCTION, body);
	}
	
	public int perform() throws curlExceptionEasy, IOException {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		curl_filedatahandler body_tofile=null;
		header.reset();
		body.reset();
		if(body_tofile_file.length()>0) {
			body_tofile = new curl_filedatahandler(body_tofile_file);
			clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_WRITEFUNCTION, body_tofile);
		}
		last_perform_error_code = clib.curl_easy_perform(curl_handle);
		if(last_perform_error_code != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(last_perform_error_code);
		if(body_tofile != null) {
			body_tofile.close();
			clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_WRITEFUNCTION, body);
			return body_tofile.getSize();
		} else
			return body.getSize();
	}
	
	public int perform(String file) throws curlExceptionEasy, IOException {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		curl_filedatahandler tofile = new curl_filedatahandler(file);
		header.reset();
		body.reset();
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_WRITEFUNCTION, tofile);
		last_perform_error_code = clib.curl_easy_perform(curl_handle);
		if(last_perform_error_code != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(last_perform_error_code); 
		tofile.close();
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_WRITEFUNCTION, body);
		return tofile.getSize();
	}
	
	public void setToFile(String file) {
		body_tofile_file = file;
	}
	
	public String getToFile() {
		return body_tofile_file;
	}
	
	/* Set Things */
	
	void setOpt(int value, Object parameter) throws curlExceptionEasy {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		int err = clib.curl_easy_setopt(curl_handle, value, parameter);
		if(err != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(err);
	}
	
	void setOpt(int value, long parameter) throws curlExceptionEasy {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		int err = clib.curl_easy_setopt(curl_handle, value, parameter);
		if(err != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(err);
	}
	
	void setOpt(int value, boolean parameter) throws curlExceptionEasy {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		int err;
		if(parameter == true)
			err=clib.curl_easy_setopt(curl_handle, value, 1l);
		else
			err=clib.curl_easy_setopt(curl_handle, value, 0l);
		if(err != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(err);
	}
	
	String getOptString(int value) throws curlExceptionEasy {
		PointerByReference p_ref = new PointerByReference();
		int err = clib.curl_easy_getinfo(curl_handle, value, p_ref);
		if(err != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(err);
		if(p_ref.getValue() == Pointer.NULL)
			return new String("");
		else
			return new String(p_ref.getValue().getString(0));
	}
	
	long getOptLong(int value) throws curlExceptionEasy {
		LongByReference p_ref = new LongByReference();
		int err = clib.curl_easy_getinfo(curl_handle, value, p_ref);
		if(err != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(err);
		return p_ref.getValue();
	}
	
	double getOptDouble(int value) throws curlExceptionEasy {
		DoubleByReference p_ref = new DoubleByReference();
		int err = clib.curl_easy_getinfo(curl_handle, value, p_ref);
		if(err != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(err);
		return p_ref.getValue();
	}
	
	public String getEffectiveUrl() throws curlExceptionEasy {
		return getOptString(curl_opts.CURLINFO_EFFECTIVE_URL);
	}
	
	public long getResponseCode() throws curlExceptionEasy {
		return getOptLong(curl_opts.CURLINFO_RESPONSE_CODE);
	}
	
	public double getTotalTime() throws curlExceptionEasy {
		return getOptDouble(curl_opts.CURLINFO_TOTAL_TIME);
	}
	
	public long getFileTime() throws curlExceptionEasy {
		return getOptLong(curl_opts.CURLINFO_FILETIME);
	}
	
	public String getRedirectUrl() throws curlExceptionEasy {
		return getOptString(curl_opts.CURLINFO_REDIRECT_URL);
	}
	
	public long getRedirectCount() throws curlExceptionEasy {
		return getOptLong(curl_opts.CURLINFO_REDIRECT_COUNT);
	}
	
	public long getHTTPVersion() throws curlExceptionEasy {
		return getOptLong(curl_opts.CURLINFO_HTTP_VERSION);
	}
	
	public String getContentType() throws curlExceptionEasy {
		return getOptString(curl_opts.CURLINFO_CONTENT_TYPE);
	}
	
	public double getSizeUploaded() throws curlExceptionEasy {
		return getOptDouble(curl_opts.CURLINFO_SIZE_UPLOAD);
	}
	
	public double getSizeDownloaded() throws curlExceptionEasy {
		return getOptDouble(curl_opts.CURLINFO_SIZE_DOWNLOAD);
	}
	
	public double getSpeedUploaded() throws curlExceptionEasy {
		return getOptDouble(curl_opts.CURLINFO_SPEED_UPLOAD);
	}
	
	public double getSpeedDownloaded() throws curlExceptionEasy {
		return getOptDouble(curl_opts.CURLINFO_SPEED_DOWNLOAD);
	}
	
	public boolean getSSLVerified() throws curlExceptionEasy {
		long ret = getOptLong(curl_opts.CURLINFO_SSL_VERIFYRESULT);
		if(ret==1)
			return true;
		else
			return false;
	}
	
	public String getPeerIP() throws curlExceptionEasy {
		return getOptString(curl_opts.CURLINFO_PRIMARY_IP);
	}
	
	public long getPeerPort() throws curlExceptionEasy {
		return getOptLong(curl_opts.CURLINFO_PRIMARY_PORT);
	}
	
	public void setVerbose(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_VERBOSE, bool);
	}
	
	public void setUrl(String url) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_URL, url);
	}
	
	public void setUserAgent(String agent) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_USERAGENT, agent);
	}
	
	public void setNobody(boolean b) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_NOBODY, b);
	}
	
	// Little more... Functionally Descriptive 
	public void setHEADRequest(boolean b) throws curlExceptionEasy {
		setNobody(b);
	}
	
	public void setProxy(String proxy) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_PROXY, proxy);
	}
	
	public void setNoProxy(String noproxy) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_NOPROXY, noproxy);
	}
	
	public void setProxyUsernamePassword(String user_password) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_PROXYUSERPWD, user_password);
	}
	
	public void setHTTPProxyTunnel(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_HTTPPROXYTUNNEL, bool);
	}
	
	public void setInterface(String inter) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_INTERFACE, inter);
	}
	
	public void setLocalPort(long port) throws curlExceptionEasy {
		if(port > 1 && port <= 65535)
			setOpt(curl_opts.CURLOPT_LOCALPORT, port);
	}

	public void setLocalPortRange(long portrange) throws curlExceptionEasy {
		if(portrange > 1 && portrange <= 65535)
			setOpt(curl_opts.CURLOPT_LOCALPORTRANGE, portrange);
	}
	
	public void setPort(long port) throws curlExceptionEasy {
		if(port > 1 && port <= 65535)
			setOpt(curl_opts.CURLOPT_PORT, port);
	}
	
	public void setKeepAlive(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_TCP_KEEPALIVE, bool);
	}
	
	public void setKeepIdle(long seconds) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_TCP_KEEPIDLE, seconds);
	}
	
	public void setUserPassword(String user_password) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_USERPWD, user_password);
	}
	
	public void setAutoReferer(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_AUTOREFERER, bool);
	}
	
	public void setAcceptEncoding(String encoding) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_ACCEPT_ENCODING, encoding);
	}
	
	public void setFollowLocation(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_FOLLOWLOCATION, bool);
	}
	
	public void setFollowLocationAuth(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_UNRESTRICTED_AUTH, bool);
	}
	
	public void setMaxRedirects(long redirects) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_MAXREDIRS, redirects);
	}
	
	public void setReferer(String referer) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_REFERER, referer);
	}
	
	public void setCookie(String cookie) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_COOKIE, cookie);
	}
	
	public void setCookieFile(String cookiefile) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_COOKIEFILE, cookiefile);
	}
	
	public void setCookieJar(String cookiejar) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_COOKIEJAR, cookiejar);
	}
	
	public void setCookieList(String list) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_COOKIELIST, list);
	}
	
	public void setGet(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_HTTPGET, bool);
	}
	
	public void setRange(String range) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_RANGE, range);
	}
	
	public void setResumeFrom(long from) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_RESUME_FROM, from);
	}
	
	public void setTimeout(long timeout) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_TIMEOUT, timeout);
	}
	
	public void setDNSServers(String dns) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_DNS_SERVERS, dns);
	}
	
	public void setSSLCert(String cert_path) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_SSLCERT, cert_path);
	}
	
	public void setSSLKey(String key_path) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_SSLKEY, key_path);
	}
	
	public void setSSLKeyPassword(String password) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_KEYPASSWD, password);
	}
	
	public void setSSLVerifyHost(boolean verify) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_SSL_VERIFYHOST, verify);
	}
	
	public void setSSLVerifyPeer(boolean verify) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_SSL_VERIFYPEER, verify);
	}
	
	public void setCABundle(String ca_path) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_CAINFO, ca_path);
	}
	
	public void setHeaders(Map<String,String> headers) throws curlExceptionEasy {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		for(Map.Entry<String, String> entry : headers.entrySet())
			clib.curl_slist_append(header_list, entry.getKey()+": "+entry.getValue());
		setOpt(curl_opts.CURLOPT_HTTPHEADER, header_list);
	}
	
	public void clearHeaders() throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_HTTPHEADER, Pointer.NULL);
		if(header_list != Pointer.NULL) {
			clib.curl_slist_free_all(header_list);
			header_list = Pointer.NULL;
		}
	}
	
	/* Post Things */
	public void setPost(boolean bool) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_POST, bool);
	}
	
	public void setPostFields(String post) throws curlExceptionEasy {
		setOpt(curl_opts.CURLOPT_COPYPOSTFIELDS, post);
	}
	
	/* public void setPostMulti(Map<String,String> posts) {
		
	} */
	
	/* Get Data/Result Information */
	public String getHeaders() {
		return header.getString();
	}
	
	public String getBody() {
		return body.getString();
	}
	
	public static String getVersion() {
		return clib.curl_version();
	}
	
	public String getError() {
		return clib.curl_easy_strerror(last_perform_error_code);
	}
	
	public int getErrorCode() {
		return last_perform_error_code;
	}
	
	void setErrorCode(int err) {
		last_perform_error_code = err;
	}
	
	/* Maintenance Things */
	
	Pointer getHandle() {
		return curl_handle;
	}
	
	void multiFlag(boolean b) {
		multi_flag = b;
	}
	
	@Override
	public void close() throws Exception, curlExceptionEasy {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		if(header != null) {
			header.close();
			header = null;
		}
		if(body != null) {
			body.close();
			body = null;
		}
		body_tofile_file = "";
		if(header_list != Pointer.NULL) {
			clib.curl_slist_free_all(header_list);
			header_list = Pointer.NULL;
		}
		if(curl_handle != Pointer.NULL) {
			clib.curl_free(curl_handle);
			curl_handle = Pointer.NULL;
		}
	}
	
	public void reset() throws Exception, curlExceptionEasy {
		if(multi_flag)
			throw new curlExceptionEasy("Attached to Multi Handle, can not modify");
		if(header != null)
			header.reset();
		else
			header = new curl_memdatahandler();
		if(body != null)
			body.reset();
		else
			body = new curl_memdatahandler();
		body_tofile_file = "";
		if(curl_handle != Pointer.NULL)
			clib.curl_free(curl_handle);
		curl_handle = clib.curl_easy_init();
		if(curl_handle != Pointer.NULL)
			throw new curlExceptionEasy("Null Pointer returned on curl_easy_init()");
		if(header_list != Pointer.NULL) {
			clib.curl_slist_free_all(header_list);
			header_list = Pointer.NULL;
		}
	}
}
