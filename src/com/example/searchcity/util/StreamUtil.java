package com.example.searchcity.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	public static void getInputStream(InputStream in, OutputStream out)
			throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = bin.read(buf)) != -1) {
			bout.write(buf, 0, len);
		}
		bout.flush();
	}
}
