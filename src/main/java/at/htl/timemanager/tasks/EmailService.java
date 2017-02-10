package at.htl.timemanager.tasks;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailService {

    private static final String ADDRESS = "met.timemanager@gmail.com";
    private static final String PASSWORD = "met1q2w3e4r";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ADDRESS.split("@")[0], PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ADDRESS, "Time Manager"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("registrierkassaapp@gmail.com"));
            message.setSubject("Tasks");
            message.setText("Bitte folgende Tasks erledigen:\n"
                    + "\n02.07.2016   1300   Rechnung erstellen"
                    + "\n04.07.2016   0017   E-Mail an BMD: Pflichtenheft + Terminvereinbarung");

            Transport.send(message);

            System.out.println("Done");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}