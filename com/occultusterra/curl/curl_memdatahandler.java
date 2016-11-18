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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.sun.jna.Pointer;

public class curl_memdatahandler implements curl_lib.DataHandler, AutoCloseable {
	ByteArrayOutputStream data = new ByteArrayOutputStream();

	@Override public int handler(Pointer contents, int size, int nmemb, Pointer userp) {
		int s=size*nmemb;
		byte[] data = contents.getByteArray(0, s);
		try {
			this.data.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.length;
	}
	
	public byte[] getData() {
		return data.toByteArray();
	}
	
	public String getString() {
		return new String(data.toByteArray());
	}
	
	public void reset() {
		data.reset();
	}

	@Override
	public void close() throws Exception {
		data.close();
	}
}
