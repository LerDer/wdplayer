package com.wd.player.util;

import com.wd.player.database.vo.SongInfoVO;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

/**
 * @author lww
 * @date 2024-07-28 4:33 PM
 */
public class SongUtil {

	public static void setTag(File songFile, String lyric,
			String title, String artist,
			String album, String year, byte[] image) throws Exception {
		AudioFile audioFile = AudioFileIO.read(songFile);
		Tag tag = audioFile.getTag();
		if (tag != null) {
			if (StringUtils.isNotEmpty(title)) {
				tag.deleteField(FieldKey.TITLE);
				tag.addField(FieldKey.TITLE, title);
			}
			if (StringUtils.isNotEmpty(artist)) {
				tag.deleteField(FieldKey.ARTIST);
				tag.addField(FieldKey.ARTIST, artist);
			}
			if (StringUtils.isNotEmpty(album)) {
				tag.deleteField(FieldKey.ALBUM);
				tag.setField(FieldKey.ALBUM, album);
			}
			if (StringUtils.isNotEmpty(year)) {
				tag.deleteField(FieldKey.YEAR);
				tag.setField(FieldKey.YEAR, year);
			}
			if (StringUtils.isNotEmpty(lyric)) {
				tag.deleteField(FieldKey.LYRICS);
				tag.setField(FieldKey.LYRICS, lyric);
			}
			if (image != null) {
				AbstractID3v2Tag tag2 = (AbstractID3v2Tag) audioFile.getTag();
				AbstractID3v2Frame frame = (AbstractID3v2Frame) tag2.getFrame("APIC");
				FrameBodyAPIC frameBody = (FrameBodyAPIC) frame.getBody();
				frameBody.setImageData(image);
			}
			audioFile.setTag(tag);
			audioFile.commit();
		}
	}

	public static String getImageType(String path) {
		return path.substring(path.lastIndexOf(".") + 1);
	}

	public static byte[] imageToByteArray(Image image, String imageType) throws IOException {
		if (image instanceof BufferedImage) {
			// 如果是BufferedImage，可以直接写入ByteArrayOutputStream
			BufferedImage bufferedImage = (BufferedImage) image;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, imageType, byteArrayOutputStream); // 可以根据需要更改图像格式
			return byteArrayOutputStream.toByteArray();
		} else {
			// 如果不是BufferedImage，需要先创建一个BufferedImage
			BufferedImage bufferedImage = new BufferedImage(
					image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			bufferedImage.getGraphics().drawImage(image, 0, 0, null);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, imageType, byteArrayOutputStream); // 可以根据需要更改图像格式
			return byteArrayOutputStream.toByteArray();
		}
	}

	/**
	 * 获取MP3文件内信息，歌名，歌手，专辑名，歌曲时长，歌曲图片
	 *
	 * @param songFile
	 * @return
	 */
	public static SongInfoVO getSongInfo(File songFile) throws Exception {
		if (hasTag(songFile)) {
			AudioFile file = AudioFileIO.read(songFile);
			String album = "";
			String albumArtist = "";
			String artist = "";
			String language = "";
			String title = "";
			String track = "";
			String year = "";
			String lyric = "";
			try {
				album = file.getTag().getFirst(FieldKey.ALBUM);
				albumArtist = file.getTag().getFirst(FieldKey.ALBUM_ARTIST);
				artist = file.getTag().getFirst(FieldKey.ARTIST);
				language = file.getTag().getFirst(FieldKey.LANGUAGE);
				title = file.getTag().getFirst(FieldKey.TITLE);
				track = file.getTag().getFirst(FieldKey.TRACK);
				year = file.getTag().getFirst(FieldKey.YEAR);
				lyric = file.getTag().getFirst(FieldKey.LYRICS);
			} catch (Exception e) {
				System.out.println("SongUtil_getSongInfo_e:" + e);
			}

			AudioHeader header = file.getAudioHeader();
			int trackLength = header.getTrackLength();
			String encodingType = header.getEncodingType();
			//String bitRate = header.getBitRate();
			long bitRate = header.getBitRateAsNumber();
			//String sampleRate = header.getSampleRate();
			int sampleRate = header.getSampleRateAsNumber();
			String format = header.getFormat();
			String channels = header.getChannels();

			SongInfoVO vo = new SongInfoVO();
			vo.setTitle(title);
			vo.setArtist(artist);
			vo.setAlbum(album);
			vo.setLyric(lyric);

			if (file instanceof MP3File) {
				//String COVER_ART = file.getTag().getFirst(FieldKey.COVER_ART);
				try {
					MP3File mp3File = (MP3File) file;
					AbstractID3v2Tag tag = mp3File.getID3v2Tag();
					AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
					FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
					byte[] imageData = body.getImageData();
					Image img = Toolkit.getDefaultToolkit().createImage(imageData);
					ImageIcon icon = new ImageIcon(img);
					Image oldLogo = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
					ImageIcon newLogo = new ImageIcon(oldLogo);
					//压缩后的
					vo.setCover(newLogo);
				} catch (Exception ignored) {
				}
				//getImg(newLogo);
				//FileOutputStream fos = new FileOutputStream("test1.jpg");
				//fos.write(imageData);
				//fos.close();
			}
			vo.setTrackLengthAsString(gettrackLengthString(trackLength));
			vo.setTrackLength(trackLength);
			vo.setYear(year);
			vo.setEncodingType(encodingType);
			vo.setSampleRate((sampleRate / 1000.0) + "kHZ");
			vo.setFormat(format);
			vo.setChannels(channels);
			vo.setBitRate(bitRate + "Kbps");
			DecimalFormat df = new DecimalFormat("#.##");
			vo.setSongSize(df.format(songFile.length() / 1024F / 1024F) + "MB");

			vo.setFileName(songFile.getName());
			vo.setAlbumArtist(albumArtist);
			vo.setTrack(track);
			vo.setLanguage(language);
			vo.setFilePath(songFile.getAbsolutePath());
			return vo;
		} else {
			SongInfoVO vo = new SongInfoVO();
			vo.setFileName(songFile.getName());
			vo.setFilePath(songFile.getAbsolutePath());
			return vo;
		}
	}

	public static String gettrackLengthString(int trackLength) {
		int hour = trackLength / 3600;
		int minute = (trackLength - hour * 3600) / 60;
		int second = (trackLength - hour * 3600 - minute * 60);
		StringBuilder sb = new StringBuilder();
		sb.append(hour > 0 ? (hour > 10 ? hour + ":" : "0" + hour + ":") : "");
		sb.append(minute > 0 ? (minute > 10 ? minute + ":" : "0" + minute + ":") : "00:");
		sb.append(second > 9 ? second : "0" + second);
		return sb.toString();
	}

	private static void getImg(ImageIcon img) {
		JFrame f = new JFrame();
		JLabel l = new JLabel();
		l.setIcon(img);
		l.setVisible(true);
		f.add(l);
		f.setSize(300, 300);
		f.setVisible(true);
	}

	private static int toInt(byte[] b) {
		return ((b[3] << 24) + (b[2] << 16) + (b[1] << 8) + (b[0] << 0));
	}

	private static short toShort(byte[] b) {
		return (short) ((b[1] << 8) + (b[0] << 0));
	}

	private static byte[] read(RandomAccessFile rdf, int pos, int length) throws IOException {
		rdf.seek(pos);
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = rdf.readByte();
		}
		return result;
	}

	public static boolean isMusic(File f) {
		return f.getName().toLowerCase().endsWith(".aac")
				|| f.getName().toLowerCase().endsWith(".aifc")
				|| f.getName().toLowerCase().endsWith(".aiff")
				|| f.getName().toLowerCase().endsWith(".ape")
				|| f.getName().toLowerCase().endsWith(".au")
				|| f.getName().toLowerCase().endsWith(".flac")
				|| f.getName().toLowerCase().endsWith(".mp2")
				|| f.getName().toLowerCase().endsWith(".mp3")
				|| f.getName().toLowerCase().endsWith(".shn")
				|| f.getName().toLowerCase().endsWith(".wav")
				|| f.getName().toLowerCase().endsWith(".wma")
				|| f.getName().toLowerCase().endsWith(".wv")
				|| f.getName().toLowerCase().endsWith(".m4a")
				|| f.getName().toLowerCase().endsWith(".ogg");
	}

	public static boolean isPic(File f) {
		return f.getName().toLowerCase().endsWith(".jpg")
				|| f.getName().toLowerCase().endsWith(".png")
				|| f.getName().toLowerCase().endsWith(".jpeg");
	}

	public static boolean hasTag(File f) {
		return f.getName().toLowerCase().endsWith(".ogg")
				|| f.getName().toLowerCase().endsWith(".flac")
				|| f.getName().toLowerCase().endsWith(".mp3")
				|| f.getName().toLowerCase().endsWith(".mp4")
				|| f.getName().toLowerCase().endsWith(".m4a")
				|| f.getName().toLowerCase().endsWith(".m4p")
				|| f.getName().toLowerCase().endsWith(".m4b")
				|| f.getName().toLowerCase().endsWith(".wav");
	}

}
