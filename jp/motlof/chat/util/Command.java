package jp.motlof.chat.util;

public class Command {
	
	public static String getProperty(String property, String pass) {
		for(String properties : property.split(",")) {
			if(properties.startsWith("%"+pass))
				return properties.split("=")[1];
		}
		return null;
	}
	
	public static boolean hasProperty(String property, String pass) {
		String text = getProperty(property, pass);
		return (text != null && !text.isEmpty());
	}
	
	public static String createProperty(String pass, String param) {
		return "%"+pass+"="+param;
	}
}
