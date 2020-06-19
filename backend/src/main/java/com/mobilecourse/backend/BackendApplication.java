package com.mobilecourse.backend;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.websocket.server.WsSci;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@MapperScan(basePackages = "com.mobilecourse.backend.dao")
public class BackendApplication extends SpringBootServletInitializer {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createSslConnector()); // 添加http
        return tomcat;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            File keystore = new ClassPathResource("server.keystore").getFile();
            /*File truststore = new ClassPathResource("sample.jks").getFile();*/
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(8080);
            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass("11112222");
            protocol.setKeyPass("11112222");
            return connector;
        }
        catch (IOException ex) {
            throw new IllegalStateException("can't access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }

    // 程序入口
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

    @Bean
    public TomcatContextCustomizer tomcatContextCustomizer() {
        System.out.println("TOMCATCONTEXTCUSTOMIZER INITILIZED");
        return context -> context.addServletContainerInitializer(new WsSci(), null);
    }
}
