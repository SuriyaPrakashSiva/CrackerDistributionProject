package com.cts.Hacked.CrackerDistribution.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImplementation {

	private JavaMailSender javaMailSender;

	public EmailServiceImplementation(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

	public void sendMail(String toEmail, String subject, String name, String address, int orderno, int quantity, double total) {

		var mailMessage = new SimpleMailMessage();

		mailMessage.setTo(toEmail);
		mailMessage.setSubject(subject);
		mailMessage.setText("Name:"+name+"\n"+"Shipping Address:"+address+"\n"+"Order NO.:" + String.valueOf(orderno) + "\n" + "Quantity:" + String.valueOf(quantity)
				+ "\n" + "Total Price:" + String.valueOf(total));
		mailMessage.setFrom("admin@crackerdistribution.com");

		javaMailSender.send(mailMessage);
	}

}
