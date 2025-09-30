package anxiety.detector.api.service.domain.model;

public class EmailTemplate {

    private String subject;
    private String body;
    private byte[] infographic;  
    private byte[] sparkline;

    public EmailTemplate() { }

    public EmailTemplate(String subject, String body, byte[] infographic, byte[] sparkline) {
        this.subject = subject;
        this.body = body;
        this.infographic = infographic;
        this.sparkline = sparkline;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getInfographic() {
        return infographic;
    }

    public void setInfographic(byte[] infographic) {
        this.infographic = infographic;
    }

    public byte[] getSparkline() {
        return sparkline;
    }

    public void setSparkline(byte[] sparkline) {
        this.sparkline = sparkline;
    }
}
