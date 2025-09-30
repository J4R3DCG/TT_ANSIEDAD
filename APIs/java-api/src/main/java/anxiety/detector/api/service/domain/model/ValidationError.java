package anxiety.detector.api.service.domain.model;

public class ValidationError {

  private String title;
  private String details;
  private int code;

    public ValidationError(String title, String details, int code) {
        this.title = title;
        this.details = details;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
