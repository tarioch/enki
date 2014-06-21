package org.tario.enki.conf;

import java.io.File;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:enki.properties")
public class Configuration {
	private final Environment env;

	@Autowired
	public Configuration(Environment env) {
		this.env = env;
	}

	public String getDevToken() {
		return env.getProperty("devToken");
	}

	public String getNoteStore() {
		return env.getProperty("noteStore");
	}

	public String getEventNotebook() {
		return env.getProperty("eventNotebook");
	}

	public File getEventFile() {
		return new File(env.getProperty("eventFile"));
	}

	public Pattern getEventDatePattern() {
		return Pattern.compile(env.getProperty("eventDatePattern"));
	}

	public String getEventTimeZone() {
		return env.getProperty("eventTimeZone");
	}
}
