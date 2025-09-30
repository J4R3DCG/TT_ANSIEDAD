package anxiety.detector.api.service.domain.model;

import java.util.List;
import java.util.UUID;

public class AnxietyResult {

    private UUID videoId;
    private List<WindowResult> windows;
    private double overall;
    private int framesAnalyzed;
    private int discardedWindows;
    private String version;
    private String sparklineB64;

    private String status;      
    private PythonError error; 

    public AnxietyResult() { }

    public AnxietyResult(UUID videoId, List<WindowResult> windows, double overall, int framesAnalyzed, int discardedWindows, String version, String sparklineB64, String status, PythonError error) {
        this.videoId = videoId;
        this.windows = windows;
        this.overall = overall;
        this.framesAnalyzed = framesAnalyzed;
        this.discardedWindows = discardedWindows;
        this.version = version;
        this.sparklineB64 = sparklineB64;
        this.status = status;
        this.error = error;
    }

    public UUID getVideoId(){ 
        return videoId; 
    }
    public void setVideoId(UUID videoId){ 
        this.videoId = videoId; 
    }
    public List<WindowResult> getWindows(){ 
        return windows; 
    }
    public void setWindows(List<WindowResult> windows){ 
        this.windows = windows; 
    }
    public double getOverall(){ 
        return overall; 
    }
    public void setOverall(double overall){ 
        this.overall = overall; 
    }
    public int getFramesAnalyzed(){ 
        return framesAnalyzed; 
    }
    public void setFramesAnalyzed(int framesAnalyzed){ 
        this.framesAnalyzed = framesAnalyzed; 
    }
    public int getDiscardedWindows(){ 
        return discardedWindows; 
    }
    public void setDiscardedWindows(int discardedWindows){ 
        this.discardedWindows = discardedWindows; 
    }
    public String getVersion(){ 
        return version; 
    }
    public void setVersion(String version){ 
        this.version = version; 
    }
    public String getSparklineB64() {
        return sparklineB64;
    }
    public void setSparklineB64(String sparklineB64) {
        this.sparklineB64 = sparklineB64;
    }

    public String getStatus(){ 
        return status; 
    }
    public void setStatus(String status){ 
        this.status = status; 
    }
    public PythonError getError(){ 
        return error; 
    }
    public void setError(PythonError error){ 
        this.error = error; 
    }

    public boolean isError() {
        return status != null && status.equalsIgnoreCase("ERROR");
    }

    @Override
    public String toString() {
        return "AnxietyResult{" +
               "videoId=" + videoId +
               ", overall=" + overall +
               ", framesAnalyzed=" + framesAnalyzed +
               ", discardedWindows=" + discardedWindows +
               ", status=" + status +
               '}';
    }

    public static class WindowResult {
        private int id;
        private int start;
        private int end;
        private double anxiety;

        public WindowResult() { }

        public WindowResult(int id, int start, int end, double anxiety) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.anxiety = anxiety;
        }

        public int getId(){ 
            return id; 
        }
        public void setId(int id){ 
            this.id = id; 
        }
        public int getStart(){ 
            return start; 
        }
        public void setStart(int start){ 
            this.start = start; 
        }
        public int getEnd(){ 
            return end; 
        }
        public void setEnd(int end){ 
            this.end = end; 
        }
        public double getAnxiety(){ 
            return anxiety; 
        }
        public void setAnxiety(double anxiety){ 
            this.anxiety = anxiety; 
        }

        @Override
        public String toString() {
            return "WindowResult{id=" + id +
                   ", start=" + start +
                   ", end=" + end +
                   ", anxiety=" + anxiety + '}';
        }
    }

    public static class PythonError {
        private String code;
        private String detail;

        public PythonError() { }

        public PythonError(String code, String detail) {
            this.code = code;
            this.detail = detail;
        }

        public String getCode(){ 
            return code; 
        }
        public void setCode(String code){ 
            this.code = code; 
        }
        public String getDetail(){ 
            return detail; 
        }
        public void setDetail(String detail){ 
            this.detail = detail; 
        }

        @Override
        public String toString() {
            return "PythonError{code='" + code + "', detail='" + detail + "'}";
        }
    }
}
