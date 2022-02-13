package tfm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

public class FileUtils {

	public static StreamTokenizer getTokens(File file) throws FileNotFoundException {
		InputStream in = new FileInputStream(file);

		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StreamTokenizer token = new StreamTokenizer(br);
		token.wordChars('_', '_');
		token.wordChars('/', '/');

		return token;
	}
}
