package br.com.pierre.pierre.identidade;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class EmailTemplateConfig {

    @Bean
    SpringTemplateEngine templateEngine() {
        // Separar os padrões impede que o resolver HTML interprete o template de texto.
        ClassLoaderTemplateResolver htmlResolver = new ClassLoaderTemplateResolver();
        htmlResolver.setPrefix("templates/");
        htmlResolver.setSuffix(".html");
        htmlResolver.setTemplateMode(TemplateMode.HTML);
        htmlResolver.setCharacterEncoding("UTF-8");
        htmlResolver.setResolvablePatterns(Set.of("email/recibo-html"));

        ClassLoaderTemplateResolver textoResolver = new ClassLoaderTemplateResolver();
        textoResolver.setPrefix("templates/");
        textoResolver.setSuffix(".txt");
        textoResolver.setTemplateMode(TemplateMode.TEXT);
        textoResolver.setCharacterEncoding("UTF-8");
        textoResolver.setResolvablePatterns(Set.of("email/recibo-texto"));

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolvers(Set.of(htmlResolver, textoResolver));
        return templateEngine;
    }
}