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

import com.sun.jna.*;
import com.sun.jna.ptr.*;

interface curl_lib extends Library {
		curl_lib INSTANCE = (curl_lib)Native.loadLibrary("libCurl.dll", curl_lib.class);
		interface DataHandler extends Callback {
			int handler(Pointer contents, int size, int nmemb, Pointer userp);
		}
		int curl_global_init(long flags);
		/* Misc API */
		String curl_version();
		String curl_easy_escape(Pointer Handle, String string, int length);
		String curl_easy_unescape(Pointer Handle, String url, int inlength, IntByReference outlength ); 
		int curl_easy_getinfo(Pointer curl, int info, Object... arguments);
		/* Linked Things */
		int curl_formadd(PointerByReference firstitem, PointerByReference lastitem, Object... arguments);
		void curl_formfree(Pointer form);
		Pointer curl_slist_append(Pointer list, String string); 
		void curl_slist_free_all(Pointer list);
		/* Easy API */
		Pointer curl_easy_init();
		int curl_easy_cleanup(Pointer Handle);
		int curl_easy_setopt(Pointer Handle, int option, Object parameter);
		int curl_easy_setopt(Pointer Handle, int option, boolean bool);
		int curl_easy_perform(Pointer Handle);
		String curl_easy_strerror(int errorNum);
		void curl_free(Pointer p);
		/* Multi API */
		Pointer curl_multi_init();
		int curl_multi_setopt(Pointer multi_handle, int option, Object parameter);
		int curl_multi_setopt(Pointer multi_handle, int option, boolean bool); 
		int curl_multi_add_handle(Pointer multi_handle, Pointer easy_handle);
		int curl_multi_remove_handle(Pointer multi_handle, Pointer easy_handle);
		int curl_multi_perform(Pointer multi_handle, IntByReference running_handles);
		int curl_multi_cleanup(Pointer multi_handle);
		CURLMsg curl_multi_info_read(Pointer multi_handle, IntByReference msgs_in_queue); 
		String curl_multi_strerror(int errornum);
}
