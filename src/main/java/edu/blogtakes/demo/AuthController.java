package edu.blogtakes.demo;

import edu.blogtakes.demo.model.fbo.FBOUser;



import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@Controller
public class AuthController {

    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("user", new FBOUser());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(FBOUser fboUser) {
        String code = generateRandomCode();
        sendEmailCode(fboUser.getEmail(), code);
        return "redirect:/email";
    }

    @GetMapping("/email")
    public String getEmail() {

        return "email";
    }

    public String generateRandomCode() {
        SecureRandom rand = new SecureRandom();
        return Integer.toString(rand.nextInt(1000000));
    }
    public void sendEmailCode(String toEmail, String code) {
        SimpleMailMessage msg = genVerificationMessage(toEmail, code);
        System.out.println(Paths.get("src/main/java/edu/credentials.txt").toAbsolutePath().toString());
        try {
            List<String> content = Files.readAllLines(Paths.get("src/main/java/credentials.txt"));
            System.out.println("File read.");
            JavaMailSender mailer = getJavaMailSender(content.get(0), content.get(1));
            System.out.println("Mailer created.");
            mailer.send(msg);

        }
        catch (Exception e) {
            System.out.println("Bad directory");
            e.printStackTrace();
        }

    }

    public SimpleMailMessage genVerificationMessage(String toEmail, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setText("Your BlogTakes email verification code is\n" + code);
        msg.setSubject("BlogTakes Verification");
        return msg;
    }


    public JavaMailSender getJavaMailSender(String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }



}
