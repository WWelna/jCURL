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

class curl_opts {
	static final int CURLOPT_VERBOSE=41;
	static final int CURLOPT_HEADER=42;
	static final int CURLOPT_WRITEFUNCTION=20011;
	static final int CURLOPT_WRITEDATA=10001;
	static final int CURLOPT_READFUNCTION=20012;
	static final int CURLOPT_READDATA=10009;
	static final int CURLOPT_HEADERFUNCTION=20079;
	static final int CURLOPT_HEADERDATA=10029;
	static final int CURLOPT_URL=10002;
	static final int CURLOPT_PROXY=10004;
	static final int CURLOPT_NOPROXY=10177;
	static final int CURLOPT_HTTPPROXYTUNNEL=61;
	static final int CURLOPT_INTERFACE=10062;
	static final int CURLOPT_LOCALPORT=139;
	static final int CURLOPT_LOCALPORTRANGE=140;
	static final int CURLOPT_PORT=3;
	static final int CURLOPT_TCP_KEEPALIVE=213;
	static final int CURLOPT_TCP_KEEPIDLE=214;
	static final int CURLOPT_USERPWD=10005;
	static final int CURLOPT_PROXYUSERPWD=10006;
	static final int CURLOPT_AUTOREFERER=58;
	static final int CURLOPT_ACCEPT_ENCODING=10102;
	static final int CURLOPT_FOLLOWLOCATION=52;
	static final int CURLOPT_UNRESTRICTED_AUTH=105;
	static final int CURLOPT_MAXREDIRS=68;
	static final int CURLOPT_POST=47;
	static final int CURLOPT_POSTFIELDS=10015;
	static final int CURLOPT_POSTFIELDSIZE=60;
	static final int CURLOPT_COPYPOSTFIELDS=10165;
	static final int CURLOPT_HTTPPOST=10024;
	static final int CURLOPT_REFERER=10016;
	static final int CURLOPT_USERAGENT=10018;
	static final int CURLOPT_HTTPHEADER=10023;
	static final int CURLOPT_PROXYHEADER=10228;
	static final int CURLOPT_COOKIE=10022;
	static final int CURLOPT_COOKIEFILE=10031;
	static final int CURLOPT_COOKIEJAR=10082;
	static final int CURLOPT_COOKIELIST=10135;
	static final int CURLOPT_HTTPGET=80;
	static final int CURLOPT_RANGE=10007;
	static final int CURLOPT_RESUME_FROM=21;
	static final int CURLOPT_TIMEOUT=13;
	static final int CURLOPT_CONNECTTIMEOUT=78;
	static final int CURLOPT_DNS_SERVERS=10211;
	static final int CURLOPT_SSLCERT=10025;
	static final int CURLOPT_SSLCERTTYPE=10086;
	static final int CURLOPT_SSLKEY=10087;
	static final int CURLOPT_SSLKEYTYPE=10088;
	static final int CURLOPT_KEYPASSWD=10026;
	static final int CURLOPT_SSL_VERIFYHOST=81;
	static final int CURLOPT_SSL_VERIFYPEER=64;
	static final int CURLOPT_CAINFO=10065;
	static final int CURLOPT_CAPATH=10097;
	static final int CURLOPT_CRLFILE=10169;
}