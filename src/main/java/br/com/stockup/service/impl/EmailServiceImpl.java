package br.com.stockup.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigoRecuperacao(String email, String codigo) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(email);
        mensagem.setSubject("Recuperação de senha - StockUp");
        mensagem.setText(
                "Olá!\n\n" +
                        "Seu código para redefinir a senha é: " + codigo +
                        "\n\nO código expira em 10 minutos."
        );

        mailSender.send(mensagem);
    }
}