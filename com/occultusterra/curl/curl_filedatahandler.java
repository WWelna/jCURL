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

import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.jna.Pointer;

class curl_filedatahandler implements curl_lib.DataHandler, AutoCloseable {
	FileOutputStream f;
	int written=0;
	
	curl_filedatahandler(String file) throws IOException {
		f = new FileOutputStream(file, false);
	}
	
	@Override
	public int handler(Pointer contents, int size, int nmemb, Pointer userp) {
		if(f == null)
			return 0;
		try {
			f.write(contents.getByteArray(0, size*nmemb));
			written += size*nmemb;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return size*nmemb;
	}
	
	public int getSize() {
		return written;
	}

	@Override
	public void close() {
		try {
			f.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
