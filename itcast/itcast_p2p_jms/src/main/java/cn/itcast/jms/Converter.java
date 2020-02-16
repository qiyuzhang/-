package cn.itcast.jms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Converter {
	@SuppressWarnings("unchecked")
	public static List<String> converter(Object obj) {
		List<String> addrList = new ArrayList<String>();
		if (null != obj) {
			if (obj instanceof String) {
				if (!StringUtils.isEmpty((String) obj)) {
					addrList.addAll(Arrays.asList(obj.toString().split(";")));
				}
			} else if (obj instanceof String[]) {
				addrList.addAll(Arrays.asList((String[]) obj));
			} else if (obj instanceof List<?>) {
				addrList = (List<String>) obj;
			}
		}
		return addrList;
	}
}
