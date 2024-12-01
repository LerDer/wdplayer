package com.wd.player.observer.enums;

/**
 * @author lww
 * @date 2024-10-15 18:41
 */
public enum PlayTypeEnum {
	ONE_LOOP("/image/repeat_one.png","/image/repeat_one_enter.png", 0),
	ALL_LOOP("/image/repeat.png","/image/repeat_enter.png", 1),
	RANDOM("/image/shuffle.png","/image/shuffle_enter.png", 2),
	SEQUENCE("/image/sequence.png","/image/sequence_enter.png", 3),
	;

	private String img;
	private String enterImg;
	private int code;

	PlayTypeEnum(String img, String enterImg, int code) {
		this.img = img;
		this.enterImg = enterImg;
		this.code = code;
	}

	public String getImg() {
		return img;
	}

	public String getEnterImg() {
		return enterImg;
	}

	public int getCode() {
		return code;
	}

	public static PlayTypeEnum getByCode(Integer code) {
		for (PlayTypeEnum type : PlayTypeEnum.values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
