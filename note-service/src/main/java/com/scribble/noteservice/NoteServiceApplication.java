package com.scribble.noteservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.scribble.noteservice.config.JwtConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.net.MalformedURLException;
import java.net.URL;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

@SpringBootApplication
@EnableEurekaClient
public class NoteServiceApplication {

	@Autowired
	private JwtConfiguration jwtConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(NoteServiceApplication.class, args);
	}

	@Bean
	public ConfigurableJWTProcessor<SecurityContext> configurableJWTProcessor() throws MalformedURLException {
		ResourceRetriever resourceRetriever =
				new DefaultResourceRetriever(jwtConfiguration.getConnectionTimeout(),
						jwtConfiguration.getReadTimeout());
		URL jwkSetURL= new URL(jwtConfiguration.getJwkUrl());
		JWKSource<SecurityContext> keySource= new RemoteJWKSet<>(jwkSetURL, resourceRetriever);
		ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> jwtProcessor= new DefaultJWTProcessor<>();
		JWSKeySelector<SecurityContext> keySelector= new JWSVerificationKeySelector<SecurityContext>(RS256, keySource);
		jwtProcessor.setJWSKeySelector(keySelector);
		return jwtProcessor;
	}

}
