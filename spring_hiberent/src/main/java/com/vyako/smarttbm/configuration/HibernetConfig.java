package com.vyako.smarttbm.configuration;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vyako.smarttbm.constants.CredentialConstants;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
@ComponentScan(basePackages = { "com.vyako.smarttbm" })
public class HibernetConfig {

	@Autowired
	private Environment env;

	@Bean
	public HibernateTemplate hibernateTemplate() {
		return new HibernateTemplate(sessionFactory());
	}

	@Bean
	public SessionFactory sessionFactory() {
		LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
		lsfb.setDataSource(getDataSource());
		lsfb.setPackagesToScan("com.vyako.smarttbm");
		lsfb.setHibernateProperties(hibernateProperties());
		try {
			lsfb.afterPropertiesSet();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lsfb.getObject();
	}

	@Bean
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("database.driver"));
		dataSource.setUrl(env.getProperty("database.url"));
		dataSource.setUsername(env.getProperty("database.root"));
		dataSource.setPassword(env.getProperty("database.password"));
		return dataSource;
	}

	@Bean
	public HibernateTransactionManager hibTransMan() {
		return new HibernateTransactionManager(sessionFactory());
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect",
				env.getProperty("hibernate.dialect"));
		properties.put("hibernate.hbm2ddl.auto",
				env.getProperty("hibernate.hbm2ddl.auto"));
		properties.put("hibernate.show_sql",
				env.getProperty("hibernate.show_sql"));
		return properties;
	}

	@Bean
	public SimpleMailMessage simpleMailMessage() {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		return simpleMailMessage;
	}

	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		// javaMailSender.setHost("mail.syslogic.in");
		//
		// javaMailSender.setHost("mail.vyako.com");
		// javaMailSender.setPort(25);
		// javaMailSender.setUsername("siddharth.yadav@vyako.com");
		// javaMailSender.setPassword("siddharth@1234");
		//
		// // javaMailSender.setHost("172.16.1.22");
		// // javaMailSender.setPort(25);
		// //
		// // // javaMailSender.setProtocol("smtp");
		// // javaMailSender.setUsername("kalyani.waghmare@syslogic.in");
		// // javaMailSender.setPassword("kalyani");
		//
		// Properties javaMailProperties = new Properties();
		// javaMailProperties.put("mail.transport.protocol", "smtp");
		// javaMailProperties.put("mail.smtp.auth", true);
		// javaMailProperties.put("mail.smtp.starttls.enable", true);
		// // javaMailProperties.put("mail.smtp.ssl.trust", "mail.vyako.com");
		// javaMailProperties.put("mail.smtp.ssl.trust", "172.16.1.22");
		// javaMailProperties.put("java.net.preferIPv4Stack", "true");
		//
		// javaMailSender.setJavaMailProperties(javaMailProperties);

		// javaMailSender.setHost("smtp.gmail.com");
		// javaMailSender.setPort(587);
		// javaMailSender.setUsername("smarttbm.vyako@gmail.com");
		// javaMailSender.setPassword("k@vudmnt");
		//
		// Properties javaMailProperties = new Properties();
		// javaMailProperties.put("mail.transport.protocol", "smtp");
		// javaMailProperties.put("mail.smtp.auth", true);
		// javaMailProperties.put("mail.smtp.starttls.enable", true);
		//
		// // add this line extra to send mail
		// javaMailProperties.put("mail.smtp.quitwait", true);
		//
		// javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		// javaMailProperties.put("java.net.preferIPv4Stack", "true");
		// javaMailProperties.put("mail.debug", "true");

		// Using gmail
//		javaMailSender.setHost(CredentialConstants.EMAIL_HOST);
//		javaMailSender.setPort(CredentialConstants.EMAIL_PORT);
//		javaMailSender.setUsername(CredentialConstants.ADMIN_EMAIL_ID);
//		javaMailSender.setPassword(CredentialConstants.ADMIN_EMAIL_PWD);

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");

		// this is the port number of local network
		javaMailProperties.put("mail.smtp.socketFactory.port", "25");

		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.debug", "true");// Prints out everything on
														// screen

		javaMailSender.setJavaMailProperties(javaMailProperties);

		return javaMailSender;
	}

}
