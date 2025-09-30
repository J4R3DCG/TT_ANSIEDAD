package anxiety.detector.api.service.domain.model;

public class UserRequest {

    private String email;
    private byte[] videoData;
    private String fileName;

    public UserRequest(String email, byte[] videoData, String fileName) {
        this.email = email;
        this.videoData = videoData;
        this.fileName = fileName;
    }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public byte[] getVideoData() {
    return videoData;
  }

  public void setVideoData(byte[] videoData) {
    this.videoData = videoData;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
