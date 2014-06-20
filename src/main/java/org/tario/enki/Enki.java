package org.tario.enki;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tario.enki.conf.Configuration;

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
		// evernoteAuth.setNoteStoreUrl(conf.getNoteStore());
		final ClientFactory clientFactory = new ClientFactory(evernoteAuth);

		final NoteStoreClient noteStoreClient;
		noteStoreClient = clientFactory.createNoteStoreClient();

		final List<Notebook> notebooks = noteStoreClient.listNotebooks();
		for (final Notebook notebook : notebooks) {
			if (conf.getEventNotebook().equals(notebook.getName())) {
				final NoteFilter filter = new NoteFilter();
				filter.setNotebookGuid(notebook.getGuid());
				final NoteList notes = noteStoreClient.findNotes(filter, 0, 9000);
				for (final Note note : notes.getNotes()) {
					System.out.println(note.getTitle());
				}
			}
		}

	}
}