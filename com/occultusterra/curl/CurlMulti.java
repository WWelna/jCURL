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

import java.util.ArrayList;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class CurlMulti implements AutoCloseable {
	private static curl_lib clib=curl_lib.INSTANCE;
	private Pointer multi_handle=Pointer.NULL;
	private ArrayList<Curl> handles = new ArrayList<>();
	
	public CurlMulti() throws curlExceptionMulti {
		do_things();
	}
	
	public CurlMulti(ArrayList<Curl> handles) throws curlExceptionMulti {
		do_things();
		addCurl(handles);
	}
	
	void do_things() throws curlExceptionMulti {
		if(clib == Pointer.NULL || Curl.global_err!=0)
			throw new curlExceptionMulti("Issues Loading libCurl / curl_global_init()");
		multi_handle = clib.curl_multi_init();
		if(multi_handle == Pointer.NULL)
			throw new curlExceptionMulti("Null Pointer returned on curl_multi_init()");
	}
	
	public void addCurl(ArrayList<Curl> handles) throws curlExceptionMulti {
		for(Curl c: handles)
			addCurl(c);
	}

	public void addCurl(Curl c) throws curlExceptionMulti {
		int err = clib.curl_multi_add_handle(multi_handle, c.getHandle());
		if(err!=curl_errors.CURLM_OK)
			throw new curlExceptionMulti(err);
		handles.add(c);
		
	}
	
	public void removeCurl(Curl c) throws curlExceptionMulti {
		int err = clib.curl_multi_remove_handle(multi_handle, c.getHandle());
		if(err!=curl_errors.CURLM_OK)
			throw new curlExceptionMulti(err);
		handles.remove(c);
	}
	
	public void removeCurl(ArrayList<Curl> handles) throws curlExceptionMulti {
		for(Curl c: handles)
			removeCurl(c);
	}
	
	public boolean perform() throws curlExceptionMulti {
		IntByReference left = new IntByReference();
		int err = clib.curl_multi_perform(multi_handle, left);
		if(err!=curl_errors.CURLM_OK)
			throw new curlExceptionMulti(err);
		if(left.getValue()==0) {
			// When everything is finished, set the error codes
			CURLMsg msg;
			IntByReference msgs_in_queue = new IntByReference();
			do {
				msg=clib.curl_multi_info_read(multi_handle, msgs_in_queue);
				if(msg != null) {
					for(Curl c: handles)
						if(c.getHandle().equals(msg.handle)) {
							if(msg.msg == curl_errors.CURLMSG_DONE)
								c.setErrorCode(msg.data.result);
							break;
						}
				}
			} while(msg != null );
			return false;
		}
		else
			return true;
	}
	
	public final ArrayList<Curl> getHandles() {
		return handles;
	}
	
	public void closeHandles() throws Exception {
		for(Curl c: handles) {
			clib.curl_multi_remove_handle(multi_handle, c.getHandle());
			c.close();
		}
		handles.clear();
	}
	
	@Override
	public void close() {
		if(multi_handle != Pointer.NULL) {
			clib.curl_multi_cleanup(multi_handle);
			multi_handle = Pointer.NULL;
		}
	}

}
