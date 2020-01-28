package com.pmandcode.ldapdemo.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.util.FileCopyUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}

	@Bean
	public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider()
			throws IOException {
		
		File jks = File.createTempFile("cacerts", "jks");
		jks.deleteOnExit();
		
		try (InputStream fromJks = WebSecurityConfig.class.getResource("/cacerts.jks").openStream()) {
			FileCopyUtils.copy(FileCopyUtils.copyToByteArray(fromJks), jks);
		}

		System.setProperty("javax.net.ssl.trustStore", jks.getPath());
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

		ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider = 
				new ActiveDirectoryLdapAuthenticationProvider("domain.name", "ldaps://domaincontroller1:3269/, ldaps://domaincontroller2:3269/");
		
		return activeDirectoryLdapAuthenticationProvider;
	}
}