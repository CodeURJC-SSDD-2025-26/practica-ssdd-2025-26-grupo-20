package es.urjc.utilityservice;

public class EmailRequestDTO {
    private String toEmail;
    private String firstName;

    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
}