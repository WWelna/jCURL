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

import java.util.Map;
import com.sun.jna.Pointer;


public class Curl implements AutoCloseable {
	private static curl_lib clib=curl_lib.INSTANCE;
	static int global_err;
	private Pointer curl_handle=Pointer.NULL;
	
	private int last_perform_error_code;
	
	private curl_memdatahandler header;
	private curl_memdatahandler body;
	
	private Pointer header_list = Pointer.NULL;
	
	static {
		global_err = clib.curl_global_init(0);
	}
	
	final public static int CURL_OK=curl_errors.CURLE_OK;
	
	public Curl() throws curlExceptionEasy {
		if(clib == Pointer.NULL || global_err!=0)
			throw new curlExceptionEasy("Issues Loading libCurl / curl_global_init()");
		curl_handle = clib.curl_easy_init();
		if(curl_handle == Pointer.NULL)
			throw new curlExceptionEasy("Null Pointer returned on curl_easy_init()");
		body = new curl_memdatahandler();
		header = new curl_memdatahandler();
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_WRITEFUNCTION, body);
		clib.curl_easy_setopt(curl_handle, curl_opts.CURLOPT_HEADERFUNCTION, header);
	}
	
	public int perform() throws curlExceptionEasy {
		header.reset();
		body.reset();
		last_perform_error_code = clib.curl_easy_perform(curl_handle);
		if(last_perform_error_code != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(last_perform_error_code); 
		return last_perform_error_code;
	}
	
	/* Set Things */
	
	public void setOpt(int value, Object parameter) throws curlExceptionEasy {
		int ret = clib.curl_easy_setopt(curl_handle, value, parameter);
		if(ret != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(ret);
	}
	
	public void setOpt(int value, boolean parameter) throws curlExceptionEasy {
		int ret = clib.curl_easy_setopt(curl_handle, value, parameter);
		if(ret != curl_errors.CURLE_OK)
			throw new curlExceptionEasy(ret);
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
	
	public String getVersion() {
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
	
	@Override
	public void close() throws Exception {
		if(header != null) {
			header.close();
			header = null;
		}
		if(body != null) {
			body.close();
			body = null;
		}
		if(curl_handle != Pointer.NULL) {
			clib.curl_free(curl_handle);
			curl_handle = Pointer.NULL;
		}
		if(header_list != Pointer.NULL) {
			clib.curl_slist_free_all(header_list);
			header_list = Pointer.NULL;
		}
	}
	
	public void reset() throws Exception, curlExceptionEasy {
		if(header != null)
			header.reset();
		else
			header = new curl_memdatahandler();
		if(body != null)
			body.reset();
		else
			body = new curl_memdatahandler();
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
