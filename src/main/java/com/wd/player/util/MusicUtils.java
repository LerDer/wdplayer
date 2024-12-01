package com.wd.player.util;

import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class MusicUtils {

	public static final int DEFAULT_BUFFER_FRAMES = 2048;
	public static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
	public static final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat(44100, 16, 2, true, false);
	public static final DataLine.Info DEFAULT_AUDIO_LINE_INFO = new DataLine.Info(SourceDataLine.class, DEFAULT_AUDIO_FORMAT);
	public static final int SOUND_VOLUME_STEPS = 1024;
	public static final int SOUND_SPEED_STEPS = 4096;

	private MusicUtils() {
		throw new AssertionError("No " + MusicUtils.class.getName() + " instances for you");
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception ignored) {
		}
	}

	public static byte[] readNBytes(InputStream in, int n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("n < 0");
		}
		Objects.requireNonNull(in);

		List<byte[]> bufs = null;
		byte[] result = null;
		int total = 0;
		int remaining = n;
		int nr;
		do {
			byte[] buf = new byte[Math.min(remaining, 8192)];
			int nread = 0;

			// read to EOF which may read more or less than buffer size
			while ((nr = in.read(buf, nread,
					Math.min(buf.length - nread, remaining))) > 0) {
				nread += nr;
				remaining -= nr;
			}

			if (nread > 0) {
				if (MAX_BUFFER_SIZE - total < nread) {
					throw new OutOfMemoryError("Required array size too large");
				}
				if (nread < buf.length) {
					buf = Arrays.copyOfRange(buf, 0, nread);
				}
				total += nread;
				if (result == null) {
					result = buf;
				} else {
					if (bufs == null) {
						bufs = new ArrayList<>();
						bufs.add(result);
					}
					bufs.add(buf);
				}
			}
			// if the last call to read returned -1 or the number of bytes
			// requested have been read then break
		} while (nr >= 0 && remaining > 0);

		if (bufs == null) {
			if (result == null) {
				return new byte[0];
			}
			return result.length == total ?
					result : Arrays.copyOf(result, total);
		}

		result = new byte[total];
		int offset = 0;
		remaining = total;
		for (byte[] b : bufs) {
			int count = Math.min(b.length, remaining);
			System.arraycopy(b, 0, result, offset, count);
			offset += count;
			remaining -= count;
		}

		return result;
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		}
		return value;
	}

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		}
		return value;
	}

	public static void adjustVolume(byte[] samples, int offset, int length, double leftVolume, double rightVolume) {
		short[] buf = new short[4];
		short left, right;
		for (int i = offset; i < offset + length; i += 4) {

			buf[0] = (short) ((samples[i + 1] & 0xFF) << 8);
			buf[1] = (short) (samples[i] & 0xFF);
			buf[2] = (short) ((samples[i + 3] & 0xFF) << 8);
			buf[3] = (short) (samples[i + 2] & 0xFF);

			left = (short) ((buf[0] | buf[1]) * leftVolume);
			right = (short) ((buf[2] | buf[3]) * rightVolume);

			samples[i] = (byte) left;
			samples[i + 1] = (byte) (left >> 8);
			samples[i + 2] = (byte) right;
			samples[i + 3] = (byte) (right >> 8);
		}
	}

	public static AudioInputStream getSupportedAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
		Objects.requireNonNull(sourceStream);
		AudioFormat sourceFormat = sourceStream.getFormat();

		int sampleSizeInBits = sourceFormat.getSampleSizeInBits();
		if (sampleSizeInBits == NOT_SPECIFIED) {
			sampleSizeInBits = targetFormat.getSampleSizeInBits();
		}
		int channels = sourceFormat.getChannels();
		if (channels == NOT_SPECIFIED) {
			channels = targetFormat.getChannels();
		}

		AudioFormat decodedFormat = new AudioFormat(
				sourceFormat.getSampleRate(),
				sampleSizeInBits,
				channels,
				true,
				sourceFormat.isBigEndian()
		);
		return AudioSystem.getAudioInputStream(targetFormat, AudioSystem.getAudioInputStream(decodedFormat, sourceStream));
	}

	public static void fromPcmToAudioBytes(byte[] audioBytes, float[] sourcePcm) {
		if (sourcePcm.length * 2 != audioBytes.length) {
			throw new IllegalArgumentException(
					"Destination array must be exactly twice the length of the source array");
		}

		for (int i = 0, n = sourcePcm.length; i < n; i++) {
			sourcePcm[i] *= 32767;

			audioBytes[i * 2] = (byte) sourcePcm[i];
			audioBytes[i * 2 + 1] = (byte) ((int) sourcePcm[i] >> 8);
		}
	}

}
