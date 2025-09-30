package anxiety.detector.api.service.framework.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import anxiety.detector.api.service.domain.model.ValidationError;
import anxiety.detector.api.service.framework.exception.ErrorResponse;
import anxiety.detector.api.service.framework.exception.ValidationException;

public final class VideoValidator {

    private VideoValidator() { }

    public static void validateExtension(String fileName) {
        if (fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".mp4")) {
            fail("Invalid video format. Only .mp4 files are allowed", 1001);
        }
    }

    public static void validateDurationMax(long seconds) {
        if (seconds > 300) {
            long min = seconds / 60;
            long sec = seconds % 60;
            String details = String.format("Video duration is %d min %02d s. Only videos shorter than 5 minutes are allowed.",min, sec);
            fail(details, 1002);
        }
    }

    public static void validateDurationMin(long seconds){
        if (seconds < 4) {
            long min = seconds / 60;
            long sec = seconds % 60;
            String details = String.format("Video duration is %d min %02d s. Only videos above 4 seconds are allowed.",min, sec);
            fail(details, 1005);
        }
    }

    public static void validateFFmpeg() {
        try {
            Process p = new ProcessBuilder("ffmpeg", "-version").start();
            if (p.waitFor() != 0) {
                throw new IOException("FFmpeg exit != 0");
            }
        } catch (Exception e) {
            fail("FFmpeg is not installed or accessible", 1003);
        }
    }

    public static void validateSize(long bytes) {
        long limit = 1500 * 1024 * 1024; 
        if (bytes > limit) {
            String details = String.format("The uploaded file is %.2f MB. The maximum allowed is 1.5 GB and 5 minutes in duration.", bytes / 1024.0 / 1024.0);
            fail(details, 1004);
        }
    }

    public static void validateTimestamps(File video) {
        ProcessBuilder pb = new ProcessBuilder(
            "ffprobe",
            "-v", "error",
            "-select_streams", "v:0",
            "-show_entries", "frame=pts_time",
            "-of", "csv=p=0",
            video.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        try {
            Process proc = pb.start();
            try (Scanner scanner = new Scanner(proc.getInputStream())) {
                double prev = -1;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;
                    double pts = Double.parseDouble(line);
                    if (prev >= 0 && pts <= prev) {
                        fail("The video contains non-monotonic timestamps in the frames. Please re-encode the video before uploading.", 1006);
                    }
                    prev = pts;
                }
            }
            int exit = proc.waitFor();
            if (exit != 0) {
                fail("Could not execute ffprobe to validate timestamps.", 1007);
            }
        } catch (IOException | InterruptedException ex) {
            fail("Error validating timestamps with ffprobe: " + ex.getMessage(), 1008);
        }
    }

    private static void fail(String details, int code) {
        ValidationError ve = new ValidationError("Validation Error", details, code);
        throw new ValidationException(ErrorResponse.of(ve));
    }
}
