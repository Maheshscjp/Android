package com.example.login.email;

import android.util.Log;
import android.widget.Toast;

import com.example.login.ApplyLeaveActivity;
import com.example.login.util.ApplicationContext;
import com.example.login.util.ConfigProperties;
import com.example.login.util.Dailog;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;

import javax.mail.PasswordAuthentication;

import javax.mail.Session;

import javax.mail.Transport;

import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeMessage;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;


public class MailSender {

    private final String mailhost = ConfigProperties.getProperty("mailhost", ApplicationContext.getAppContext());
    private final String user = ConfigProperties.getProperty("email", ApplicationContext.getAppContext());
    private final String password = ConfigProperties.getProperty("password", ApplicationContext.getAppContext());
    private Session session;

    /*static {
        Security.addProvider(new JSSEProvider());
    }*/

    public MailSender() {
        Security.addProvider(new JSSEProvider());
        Properties props = new Properties();

        props.put("mail.host", mailhost);

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.starttls.enable", "true");



        session = Session.getInstance(props,  new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(user, password);

            }

        });

    }

    public synchronized void sendMail(String subject, String body, String recipients) {

        try {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(user));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            /*message.setText(body);*/
            Log.d("", "sendMail: message ::" + message.toString());
            //send the message
            Transport transport = session.getTransport("smtp");
            transport.send(message);
            transport.close();
            Log.d("", "sendMail: message ::" + message.toString());
            Toast.makeText(ApplicationContext.getAppContext(), "Mail sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("", "sendMail: error ::" + e.getMessage());
            Toast.makeText(ApplicationContext.getAppContext(), "Mail service is not working.", Toast.LENGTH_SHORT).show();

        }

    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}

