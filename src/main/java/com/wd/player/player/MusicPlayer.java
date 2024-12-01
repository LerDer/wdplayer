package com.wd.player.player;

import com.wd.player.util.MusicUtils;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import lombok.Getter;
import lombok.Setter;

public class MusicPlayer {

	private static final MusicPlayer INSTANCE = new MusicPlayer(null, null, Thread.MAX_PRIORITY, MusicUtils.DEFAULT_BUFFER_FRAMES);

	public static MusicPlayer getInstance() {
		return INSTANCE;
	}

	@Getter
	@Setter
	private Mixer mixer;

	@Getter
	private AudioFormat playbackFormat;

	@Getter
	private DataLine.Info playbackInfo;

	@Setter
	private AudioInputStream audioInputStream;

	@Setter
	@Getter
	private int threadPriority;

	@Setter
	@Getter
	private int streamBufferSize;

	@Getter
	private volatile Double leftVolume = 1.0;
	@Getter
	private volatile Double rightVolume = 1.0;
	//@Setter
	//@Getter
	//private volatile static boolean playing;
	//@Setter
	//@Getter
	//private volatile static boolean paused;
	//private static Thread thread = null;
	//@Setter
	//@Getter
	//private volatile static SourceDataLine sourceDataLine = null;
	//@Setter
	//@Getter
	//private volatile static AudioInputStream audioInputStream = null;
	//private final AtomicLong microsecondsLength = new AtomicLong(NOT_SPECIFIED);
	//private static final AtomicInteger NEXT_SERIAL_NUMBER = new AtomicInteger();
	//private volatile static float frameRate;
	//private volatile static Integer frameRateSize;
	//private volatile static int channels;

	private MusicPlayer(Mixer mixer, AudioFormat playbackFormat, int threadPriority, int streamBufferSize) {
		if (streamBufferSize < 0) {
			throw new ArrayIndexOutOfBoundsException(streamBufferSize);
		}
		if (playbackFormat == null) {
			this.playbackFormat = MusicUtils.DEFAULT_AUDIO_FORMAT;
			this.playbackInfo = MusicUtils.DEFAULT_AUDIO_LINE_INFO;
		} else {
			this.playbackFormat = playbackFormat;
			this.playbackInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
		}
		this.mixer = mixer;
		this.streamBufferSize = streamBufferSize * this.playbackFormat.getFrameSize();
		this.threadPriority = MusicUtils.clamp(threadPriority, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);
	}

	//private void setLeftVolume(double leftVolume) {
	//	this.leftVolume = MusicUtils.clamp(leftVolume, 0, 1);
	//}

	//private void setRightVolume(double rightVolume) {
	//	this.rightVolume = MusicUtils.clamp(rightVolume, 0, 1);
	//}

	//private void setVolume(double leftVolume, double rightVolume) {
	//	this.setLeftVolume(leftVolume);
	//	this.setRightVolume(rightVolume);
	//}

	//public void setVolume(double volume) {
	//	this.setVolume(volume, volume);
	//}

	//public void seekToSecond(long second) {
	//	try {
	//		//audioInputStream.reset();
	//		//秒数 * 每秒播放和录制的帧数 * 每个具有此格式的声音帧包含的字节数。
	//		audioInputStream.skip((long) (second * channels * frameRate * frameRateSize));
	//	} catch (IOException e) {
	//		throw new RuntimeException(e);
	//	}
	//}

	public void seekToSecond(long second) {
		try {
			// 计算要跳过的字节数  每秒的采样数 * 个采样的字节数 * 要跳过的秒数 = 要跳过的字节数
			long bytesToSkip = (long) (second * this.audioInputStream.getFormat().getFrameRate() * this.audioInputStream.getFormat().getFrameSize());
			// 调用skip方法
			audioInputStream.skip(second);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	//public synchronized void play(MusicEvent musicEvent) throws Exception {
	//	this.reset();
	//	FileTypeEnum fileType = musicEvent.getFileType();
	//	switch (fileType) {
	//		case local:
	//			this.paly(musicEvent);
	//			break;
	//		case remote:
	//			String fileUrl = musicEvent.getFileUrl();
	//			//this.paly(ClassLoader.getSystemClassLoader(), fileUrl);
	//			break;
	//	}
	//}

	//private synchronized void paly(MusicEvent musicEvent) throws Exception {
	//	String filePath = musicEvent.getFilePath();
	//	File file = new File(filePath);
	//	AudioInputStream sourceStream = AudioSystem.getAudioInputStream(file);
	//	AudioFileFormat sourceFileFormat = AudioSystem.getAudioFileFormat(file);
	//	Long duration = (Long) sourceFileFormat.properties().get("duration");
	//	channels = sourceFileFormat.getFormat().getChannels();
	//	frameRate = sourceFileFormat.getFormat().getFrameRate();
	//	frameRateSize = (Integer) sourceFileFormat.properties().get("mp3.framesize.bytes");
	//	if (frameRateSize == null) {
	//		frameRateSize = sourceStream.getFormat().getFrameSize();
	//	}
	//	if (duration == null) {
	//		if (sourceFileFormat.getFrameLength() != NOT_SPECIFIED && sourceFileFormat.getFormat().getFrameRate() != NOT_SPECIFIED) {
	//			this.microsecondsLength.set((long) ((double) sourceFileFormat.getFrameLength() / (double) sourceFileFormat.getFormat().getFrameRate()) * 1_000_000L);
	//		}
	//	} else {
	//		this.microsecondsLength.set(duration);
	//	}
	//	audioInputStream = MusicUtils.getSupportedAudioInputStream(this.playbackFormat, sourceStream);
	//
	//	sourceDataLine = (SourceDataLine) (this.mixer == null ? AudioSystem.getLine(this.playbackInfo) : this.mixer.getLine(this.playbackInfo));
	//	sourceDataLine.open();
	//
	//	MusicPlayer.playing = true;
	//	MusicPlayer.paused = false;
	//	thread = new Thread(() -> {
	//		sourceDataLine.start();
	//		byte[] buffer = new byte[this.getStreamBufferSize()];
	//		int read = 0;
	//		boolean playFinish = false;
	//		audioInputStream.mark(1);
	//		while (MusicPlayer.playing && read != -1) {
	//			if (!MusicPlayer.paused) {
	//				try {
	//					read = audioInputStream.read(buffer, 0, buffer.length);
	//				} catch (IOException e) {
	//					e.printStackTrace();
	//					this.reset();
	//					return;
	//				}
	//				MusicUtils.adjustVolume(buffer, 0, read, this.getLeftVolume(), this.getRightVolume());
	//				if (read != -1) {
	//					sourceDataLine.write(buffer, 0, read);
	//				} else {
	//					playFinish = true;
	//				}
	//			}
	//		}
	//		if (playFinish) {
	//			musicEvent.setPlayMessage(PlayMessageEnum.Finish);
	//			//PlayerPublisher.getInstance().notifyObservers(musicEvent);
	//			this.reset(musicEvent);
	//		} else {
	//			this.reset();
	//		}
	//	}, "MusicPlayer-" + NEXT_SERIAL_NUMBER.getAndIncrement());
	//	thread.setPriority(this.getThreadPriority());
	//	thread.setDaemon(true);
	//	thread.start();
	//}

	//public void pause() {
	//	if (MusicPlayer.playing) {
	//		MusicPlayer.paused = true;
	//	}
	//}

	//public void recover() {
	//	if (MusicPlayer.playing) {
	//		MusicPlayer.paused = false;
	//	}
	//}

	//private synchronized void reset() {
	//	try {
	//		if (sourceDataLine != null) {
	//			sourceDataLine.stop();
	//			sourceDataLine.drain();
	//			if (audioInputStream != null) {
	//				audioInputStream.close();
	//			}
	//			if (thread != null) {
	//				try {
	//					thread.stop();
	//				} catch (Exception e) {
	//					throw new RuntimeException(e);
	//				}
	//				thread = null;
	//			}
	//		}
	//	} catch (IOException e) {
	//		e.printStackTrace();
	//		throw new RuntimeException(e);
	//	}
	//}

	//private synchronized void reset(MusicEvent musicEvent) {
	//	try {
	//		if (musicEvent != null) {
	//			PlayerPublisher.getInstance().notifyObservers(musicEvent);
	//		}
	//		if (sourceDataLine != null) {
	//			sourceDataLine.stop();
	//			sourceDataLine.drain();
	//			if (audioInputStream != null) {
	//				audioInputStream.close();
	//			}
	//			if (thread != null) {
	//				thread.stop();
	//				thread = null;
	//			}
	//		}
	//	} catch (IOException e) {
	//		e.printStackTrace();
	//		throw new RuntimeException(e);
	//	}
	//}

	//@Override
	//public void close() throws IOException {
	//	this.reset();
	//}
	//
	//public void last(MusicEvent musicEvent) throws Exception {
	//	this.reset();
	//	//this.play(musicEvent);
	//}

}
