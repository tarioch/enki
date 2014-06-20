package org.tario.enki;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tario.enki.conf.Configuration;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.util.Duration;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

@Component
public class Enki {

	private final Configuration conf;

	@Autowired
	public Enki(Configuration conf) {
		this.conf = conf;
	}

	public void run() throws Exception {
		final EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.PRODUCTION, conf.getDevToken());
		evernoteAuth.setNoteStoreUrl(conf.getNoteStore());
		final ClientFactory clientFactory = new ClientFactory(evernoteAuth);

		final NoteStoreClient noteStoreClient;
		noteStoreClient = clientFactory.createNoteStoreClient();

		final ICalendar ical = new ICalendar();
		ical.setProductId("org.tario.enki");

		final Pattern eventDatePattern = conf.getEventDatePattern();
		final List<Notebook> notebooks = noteStoreClient.listNotebooks();
		for (final Notebook notebook : notebooks) {
			if (conf.getEventNotebook().equals(notebook.getName())) {
				final NoteFilter filter = new NoteFilter();
				filter.setNotebookGuid(notebook.getGuid());
				final NoteList notes = noteStoreClient.findNotes(filter, 0, 9000);
				for (final Note note : notes.getNotes()) {
					final VEvent event = new VEvent();
					final String title = note.getTitle();

					final Matcher matcher = eventDatePattern.matcher(title);
					if (matcher.matches()) {
						final String day = matcher.group("day");
						final String month = matcher.group("month");
						final String year = matcher.group("year");
						final String fromHour = matcher.group("fromHour");
						final String fromMinute = matcher.group("fromMinute");
						final String toHour = matcher.group("toHour");
						final String toMinute = matcher.group("toMinute");

						final LocalDate fromDate = new LocalDate(Integer.parseInt(year), Integer.parseInt(month),
								Integer.parseInt(day));
						if (fromHour != null && fromMinute != null && toHour != null && toMinute != null) {
							final LocalTime fromTime = new LocalTime(Integer.parseInt(fromHour),
									Integer.parseInt(fromMinute));

							final LocalTime toTime = new LocalTime(Integer.parseInt(toHour),
									Integer.parseInt(toMinute));

							event.setDateStart(fromDate.toLocalDateTime(fromTime).toDate());
							event.setDateEnd(fromDate.toLocalDateTime(toTime).toDate());
						} else {
							event.setDateStart(fromDate.toDate());
							event.setDuration(Duration.builder().days(1).build());
						}

						event.setSummary(title);

						ical.addEvent(event);
					}

				}
			}
		}

		Biweekly.write(ical).go(conf.getEventFile());
	}
}
