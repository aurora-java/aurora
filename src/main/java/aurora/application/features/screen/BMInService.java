package aurora.application.features.screen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import uncertain.schema.Attribute;
import uncertain.schema.ISchemaManager;
import aurora.application.features.cstm.CustomSourceCode;
import aurora.application.sourcecode.SourceCodeUtil;
import aurora.bm.IModelFactory;

public class BMInService {
	private final static QualifiedName bmReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "model");

	public static CompositeMap getContainsBM(IObjectRegistry registry,
			String filePath) throws IOException, SAXException {

		if (registry == null)
			throw new RuntimeException(
					"paramter error. 'registry' can not be null.");
		File webHome = SourceCodeUtil.getWebHome(registry);
		File sourceFile = new File(webHome, filePath);
		// 当传入的页面无效时,返回空的东西(不是null,不抛异常)
		if (sourceFile == null || !sourceFile.exists())
			return createResult(new ArrayList<String>(0), registry);// //////
		CompositeLoader cl = new CompositeLoader();
		CompositeMap source = cl.loadByFullFilePath(sourceFile
				.getCanonicalPath());

		Object schemaManager = registry.getInstanceOfType(ISchemaManager.class);
		if (schemaManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(
					(new CompositeMap()).asLocatable(), ISchemaManager.class,
					CustomSourceCode.class.getCanonicalName());
		ReferenceTypeFinder finder = new ReferenceTypeFinder(bmReference,
				(ISchemaManager) schemaManager);
		source.iterate(finder, true);
		List<MapFinderResult> result = finder.getResult();

		List<String> bmPKGS = new ArrayList<String>();

		for (MapFinderResult mr : result) {
			List<Attribute> attributes = mr.getAttributes();
			if (attributes != null) {
				for (Attribute a : attributes) {
					String pkg = mr.getMap().getString(a.getName());
					if (pkg != null) {
						String[] split = pkg.split("\\?");
						pkg = split[0];
						if (!bmPKGS.contains(pkg)) {
							bmPKGS.add(pkg);
						}
					}
				}
			}
		}
		return createResult(bmPKGS, registry);
	}

	private static CompositeMap createResult(List<String> bmPKGS,
			IObjectRegistry registry) {
		CompositeMap result = new CompositeMap("result");
		IModelFactory factory = (IModelFactory) registry
				.getInstanceOfType(IModelFactory.class);
		for (String pkg : bmPKGS) {
			CompositeMap bm = new CompositeMap("record");
			bm.put("bm", pkg);
			try {
				factory.getModel(pkg);
				bm.put("exist", true);
			} catch (Exception e) {
				bm.put("exist", false);
			}
			result.addChild(bm);
		}
		return result;
	}

}
