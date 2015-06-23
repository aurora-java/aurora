package aurora.application.action;

import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.i18n.DatabaseBasedMessageProvider;
import aurora.i18n.IMessageProvider;

public class RefreshPrompts extends AbstractEntry {

	private IObjectRegistry registry;

	public RefreshPrompts(IObjectRegistry registry) {
		this.registry = registry;

	}

	public void run(ProcedureRunner runner) throws Exception {
		IMessageProvider mp = (IMessageProvider) registry
				.getInstanceOfType(IMessageProvider.class);
		if (mp instanceof DatabaseBasedMessageProvider) {
			DatabaseBasedMessageProvider dmp = (DatabaseBasedMessageProvider) mp;
			dmp.reload();
		} else {
           throw new Exception ("mp is not instance of DatabaseBasedMessageProvider");
		}

	}

}
