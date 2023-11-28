package edu.blogtakes.demo;

import com.sun.mail.iap.Response;
import edu.blogtakes.demo.model.fbo.FBOCode;
import edu.blogtakes.demo.model.fbo.FBOUser;


import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

@Controller
public class AuthController {

    @Autowired
    ConnectionFactory connFactory;
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("user", new FBOUser());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(FBOUser fboUser, WebSession session) {
        String code = generateRandomCode();
        sendEmailCode(fboUser.getEmail(), code);
        storeEmailCode(fboUser.getEmail(), code);
        session.getAttributes().put("email", fboUser.getEmail());
        return "redirect:/verify";
    }

    private String generateRandomCode() {
        SecureRandom rand = new SecureRandom();
        return String.format("%06d", rand.nextInt(1000000));
    }

    private void sendEmailCode(String toEmail, String code) {
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
    private void storeEmailCode(String email, String code) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connFactory);

        template.getDatabaseClient()
                .sql("INSERT INTO auth.codes(email, code) VALUES (:email, :code)")
                .bind("email", email)
                .bind("code", code)

                .fetch()
                .rowsUpdated()
                .subscribe(System.out::println);
    }
    @GetMapping("/verify")
    public String getVerify(Model model) {
        model.addAttribute("code", new FBOCode());
        return "verify";
    }

    @PostMapping("/verify")
    public Mono<ResponseEntity<Object>> postVerify(@ModelAttribute FBOCode codeObject, WebSession session) {
        String code = codeObject.getCode();
        Mono<Object> codeMono = getCode(session.getAttribute("email"));
        return routePostVerify(codeMono, code);
    }

    //Returns a Row containing the current user's authentication code from the database
    private Mono<Object> getCode(String email) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connFactory);
        Mono<Object> dbCode = template.getDatabaseClient()
                .sql("SELECT code FROM auth.codes WHERE email = :email;")
                .bind("email", email)
                .fetch()
                .one()
                .map(m -> m.get("code"));
        return dbCode.subscribeOn(Schedulers.boundedElastic());
    }

    //Returns a Mono<ResponseEntity> which contains the appropriate route based on the given inputCode
    private Mono<ResponseEntity<Object>> routePostVerify(Mono<Object> dbCodeWrap, String inputCode) {
        return dbCodeWrap.map(m -> {
            if (m.equals(inputCode)) {
                System.out.println("confirmed route");
                return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/confirmed")).build();
            }
            else {
                System.out.println("error route");
                return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/verify")).build();
            }
        });
    }

    @GetMapping("/confirmed")
    public String getConfirmed() {
        return "confirmed";
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
