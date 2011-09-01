package aurora.schema;

import java.io.IOException;
import java.net.URISyntaxException;

import org.xml.sax.SAXException;

import uncertain.composite.QualifiedName;
import uncertain.pkg.PackageManager;
import uncertain.schema.Element;
import uncertain.schema.SchemaManager;

public class AuroraSchemaManager {

	private static AuroraSchemaManager asm;
	private SchemaManager schemaManager;
	private AuroraSchemaManager(){
		
	}
	private void loadBuiltInSchema() throws IOException, SAXException, URISyntaxException {
		schemaManager = SchemaManager.getDefaultInstance();
		PackageManager pkgManager = new PackageManager();
		String uncertinSchema = "uncertain_builtin_package/uncertain.builtin";
		pkgManager.loadPackgeFromClassPath(uncertinSchema);
		String[] packages = new String[] { "aurora_builtin_package/aurora.base/",
				"aurora_builtin_package/aurora.database/", "aurora_builtin_package/aurora.presentation/" };
		for (int i = 0; i < packages.length; i++) {
			String packageName = packages[i];
			pkgManager.loadPackgeFromClassPath(packageName);
		}
		schemaManager.addAll(pkgManager.getSchemaManager());
	}

	public static void main(String[] args) {
		AuroraSchemaManager auroraSchemaM = AuroraSchemaManager.getInstance();
		QualifiedName qn = new QualifiedName("http://www.aurora-framework.org/application", "grid");
		Element ele = auroraSchemaM.getSchemaManager().getElement(qn);
		System.out.println(ele);
		System.out.println(auroraSchemaM.getSchemaManager().getAllTypes());
		
	}

	public static synchronized AuroraSchemaManager getInstance() {
		if (asm != null)
			return asm;
		asm = new AuroraSchemaManager();
		try {
			asm.loadBuiltInSchema();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return asm;
	}
	public SchemaManager getSchemaManager() {
		return schemaManager;
	}
	public void setSchemaManager(SchemaManager schemaManager) {
		this.schemaManager = schemaManager;
	}
	
}
