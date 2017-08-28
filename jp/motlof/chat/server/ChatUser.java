package jp.motlof.chat.server;

import java.awt.Color;
import java.util.UUID;

public class ChatUser {
	
	private String name;
	private UUID uuid;
	private Color namecolor;
	
	public ChatUser(String name) {
		this.name = name;
		this.uuid = UUID.randomUUID();
		if(name == null || name.isEmpty())
			this.name = "名無しさん@"+uuid.toString().substring(0, 4);
	}
	
	public String getName() {
		return name;
	}
	public UUID getUuid() {
		return uuid;
	}
	public Color getColor() {
		return namecolor;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public void setColor(Color color) {
		this.namecolor = color;
	}
}
