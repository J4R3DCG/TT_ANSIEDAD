package anxiety.detector.api.service.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import anxiety.detector.api.service.domain.model.ValidationError;
import anxiety.detector.api.service.framework.exception.ErrorResponse;
import anxiety.detector.api.service.framework.exception.ValidationException;

public final class VideoDurationCalculator {

    private VideoDurationCalculator() { }

    public static long calculateDuration(byte[] videoBytes) {
        File tempFile = null;
        try {
            tempFile = writeTempFile(videoBytes);
            return readDurationWithFfprobe(tempFile);
        } catch (ValidationException ve) {
            throw ve;                     
        } catch (Exception ex) {
            throw new RuntimeException("Failed to calculate video duration", ex);
        } finally {
            if (tempFile != null) tempFile.delete();
        }
    }

    private static File writeTempFile(byte[] data) throws IOException {
        File f = File.createTempFile("temp-video-", ".mp4");
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(data);
        }
        return f;
    }

    private static long readDurationWithFfprobe(File video) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("ffprobe","-v", "error","-show_entries", "format=duration","-of", "default=noprint_wrappers=1:nokey=1", video.getAbsolutePath());

        Process proc = pb.start();
        String output = new String(proc.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        proc.waitFor();

        if (proc.exitValue() != 0 || output.isBlank()) {
            throwValidation("Cannot read or parse the video file with FFprobe", 2000);
        }

        double seconds = Double.parseDouble(output);
        return (long) Math.ceil(seconds);
    }

    private static void throwValidation(String details, int code) {
        ValidationError ve = new ValidationError("Validation Error", details, code);
        throw new ValidationException(ErrorResponse.of(ve));
    }
}