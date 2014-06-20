package org.tario.enki;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class EnkiApplication
{
	public static void main(String[] args) throws Exception
	{
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(EnkiApplication.class)) {
			context.getBean(Enki.class).run();
		}
	}
}
