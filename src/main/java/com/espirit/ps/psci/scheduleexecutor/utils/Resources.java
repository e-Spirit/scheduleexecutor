package com.espirit.ps.psci.scheduleexecutor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Streams;

public class Resources {

	private Resources() {}


	public static String asString(String pathToFile, Class<?> clazz) {
		InputStream inputStream = null;
		try {
			inputStream = clazz.getResourceAsStream(pathToFile);
			return Streams.toString(inputStream, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			Logging.logError("io-fehler beim laden des gomsources", e, Resources.class);
			return null;
		} finally {
			if (inputStream != null) {
				Streams.close(inputStream);
			}
		}
	}
}
