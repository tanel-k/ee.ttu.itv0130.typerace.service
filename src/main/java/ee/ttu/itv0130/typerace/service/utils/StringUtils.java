package ee.ttu.itv0130.typerace.service.utils;

public class StringUtils {
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str.trim()));
	}
}
