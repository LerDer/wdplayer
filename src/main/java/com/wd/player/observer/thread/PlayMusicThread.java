package com.wd.player.observer.thread;

import com.wd.player.gui.MainContainer;
import com.wd.player.observer.enums.FileTypeEnum;
import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.publisher.PlayerPublisher;
import com.wd.player.player.MusicPlayer;
import com.wd.player.status.StatusManager;
import com.wd.player.util.MusicUtils;
import java.io.File;
import java.util.Objects;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import lombok.Setter;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 * @author lww
 * @date 2024-10-31 11:11
 */
public class PlayMusicThread extends BaseThread {

	@Setter
	private MusicEvent musicEvent;

	public PlayMusicThread(MusicEvent musicEvent) {
		this.musicEvent = musicEvent;
	}

	@Override
	public void play() {
		try {
			MusicPlayer instance = MusicPlayer.getInstance();
			FileTypeEnum fileType = musicEvent.getFileType();
			if (Objects.requireNonNull(fileType) == FileTypeEnum.local) {
				//System.out.println("本地歌曲");
				String filePath = musicEvent.getFilePath();
				File file = new File(filePath);
				AudioInputStream sourceStream = AudioSystem.getAudioInputStream(file);
				AudioFormat format = sourceStream.getFormat();
				AudioFileFormat sourceFileFormat = AudioSystem.getAudioFileFormat(file);
				Long duration = (Long) sourceFileFormat.properties().get("duration");
				int channels = sourceFileFormat.getFormat().getChannels();
				float frameRate = sourceFileFormat.getFormat().getFrameRate();
				Integer frameRateSize = (Integer) sourceFileFormat.properties().get("mp3.framesize.bytes");
				if (frameRateSize == null) {
					frameRateSize = sourceStream.getFormat().getFrameSize();
				}
				//if (duration == null) {
				//	if (sourceFileFormat.getFrameLength() != NOT_SPECIFIED && sourceFileFormat.getFormat().getFrameRate() != NOT_SPECIFIED) {
				//		this.microsecondsLength.set((long) ((double) sourceFileFormat.getFrameLength() / (double) sourceFileFormat.getFormat().getFrameRate()) * 1_000_000L);
				//	}
				//} else {
				//	this.microsecondsLength.set(duration);
				//}
				AudioInputStream audioInputStream = MusicUtils.getSupportedAudioInputStream(instance.getPlaybackFormat(), sourceStream);
				instance.setAudioInputStream(audioInputStream);
				SourceDataLine sourceDataLine;
				if (instance.getMixer() == null) {
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(instance.getPlaybackInfo());
				} else {
					sourceDataLine = (SourceDataLine) instance.getMixer().getLine(instance.getPlaybackInfo());
				}
				sourceDataLine.open();
				sourceDataLine.start();
				byte[] buffer = new byte[instance.getStreamBufferSize()];
				int read = 0;
				FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
				while (StatusManager.playing && read != -1) {
					read = audioInputStream.read(buffer, 0, buffer.length);
					MusicUtils.adjustVolume(buffer, 0, read, instance.getLeftVolume(), instance.getRightVolume());
					if (read != -1) {
						Complex[] complexData = new Complex[buffer.length / 2];
						for (int i = 0, j = 0; i < buffer.length; i += 2, j++) {
							int value = (buffer[i + 1] << 8) | (buffer[i] & 0xff);
							complexData[j] = new Complex(value, 0);
						}
						Complex[] fftResult = transformer.transform(complexData, TransformType.FORWARD);
						double[] spectrum = new double[fftResult.length];
						double normalizationFactor = Math.sqrt(complexData.length);
						for (int i = 0; i < fftResult.length; i++) {
							spectrum[i] = fftResult[i].abs();
							spectrum[i] /= normalizationFactor;
						}
						int halfLength = spectrum.length / 2;
						double[] half = new double[halfLength];
						for (int i = 1; i < halfLength; i++) {
							//将频谱的右半部分复制到半数组
							half[i] = spectrum[i] * spectrum[i];
						}
						sourceDataLine.write(buffer, 0, read);
						if (MainContainer.spec != null) {
							MainContainer.spec.drawHistogram(half);
						}
					} else {
						MusicEvent event = new MusicEvent(PlayMessageEnum.Finish, musicEvent.getSongId());
						PlayerPublisher.getInstance().notifyObservers(event);
					}
				}
			} else {
				System.out.println("网络歌曲");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
