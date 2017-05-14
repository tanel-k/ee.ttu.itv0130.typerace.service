package ee.ttu.itv0130.typerace.service.model;

import org.springframework.data.annotation.Id;

public class Word {
	@Id
	private String id;
	private String chars;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChars() {
		return chars;
	}

	public void setChars(String chars) {
		this.chars = chars;
	}
}
