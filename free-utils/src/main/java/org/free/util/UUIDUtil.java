package org.free.util;

import java.util.UUID;

public class UUIDUtil {
	public static String getUUID() {
		return UUID.randomUUID().toString().trim();
	}

	public static String get32UUID() {
		return getUUID().replaceAll("-", "");
	}
}
