package aurora.ide.api.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurora.ide.api.statistics.map.StatisticsResult;
import aurora.ide.api.statistics.model.Dependency;
import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.api.statistics.model.StatisticsProject;
import aurora.ide.api.statistics.model.Tag;

public class DatabaseAction implements SQL {
	private StatisticsProject project;
	private List<ProjectObject> poList;
	private StatisticsResult result;
	private Map<Integer, ProjectObject> poMap;
	private int BATCH_SIZE = 10000;
	private Map<String, List<String>> reference = new HashMap<String, List<String>>();
	private Map<String, Integer> referenced = new HashMap<String, Integer>();

	public DatabaseAction(Statistician statistician) {
		this.project = statistician.getProject();
		this.poList = statistician.getPoList();
	}

	private void saveProject(Connection connection) throws SQLException {

		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(insertProjectSql);
			prepareStatement.setInt(1, Integer.valueOf(project.getProjectId()));
			prepareStatement.setString(2, project.getProjectName());
			prepareStatement.setString(3, project.getStorer());
			prepareStatement.setString(4, project.getRepositoryType());
			prepareStatement.setString(5, project.getRepositoryRevision());
			prepareStatement.setString(6, project.getRepositoryPath());
			prepareStatement.execute();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setObjectID(Connection connection, ProjectObject po) {
		String selectProjectID = "SELECT project_object_s.nextval from dual ";
		// project_object
		// object_tag
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(selectProjectID);
			int objectID = prepareStatement.executeQuery().getInt(1);
			po.setObjectId(String.valueOf(objectID));
		} catch (SQLException e) {
			po.setObjectId("-1");
		}
	}

	private void saveObjects(int partSize, Connection connection, int maxObjectID) throws SQLException {
		PreparedStatement prepareStatement = null;
		int count = 0;
		try {
			prepareStatement = connection.prepareStatement(insertObjectSql);
			for (ProjectObject po : poList) {
				po.setObjectId(String.valueOf(maxObjectID++));
				po.setProjectId(project.getProjectId());
				prepareStatement.setInt(1, Integer.valueOf(po.getObjectId()));
				prepareStatement.setInt(2, Integer.valueOf(po.getProjectId()));
				prepareStatement.setString(3, po.getType());
				prepareStatement.setString(4, po.getName());
				prepareStatement.setString(5, po.getPath());
				prepareStatement.setInt(6, po.getFileSize());
				prepareStatement.setInt(7, po.getScriptSize());
				prepareStatement.addBatch();
				count++;
				if (count >= partSize) {
					prepareStatement.executeBatch();
					prepareStatement.close();
					prepareStatement = connection.prepareStatement(insertObjectSql);
					count = 0;
				}
			}
			if (count > 0) {
				prepareStatement.executeBatch();
			}
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveTags(int partSize, Connection connection) throws SQLException {

		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(insertTagSql);
			int count = 0;
			for (ProjectObject po : poList) {
				List<Tag> tags = po.getTags();
				for (Tag tag : tags) {
					prepareStatement.setInt(1, Integer.valueOf(po.getObjectId()));
					prepareStatement.setInt(2, Integer.valueOf(po.getProjectId()));
					prepareStatement.setString(3, tag.getType());
					prepareStatement.setString(4, tag.getName());
					prepareStatement.setString(5, tag.getNamespace());
					prepareStatement.setString(6, tag.getqName());
					prepareStatement.setString(7, tag.getRawName());
					prepareStatement.setString(8, tag.getPrefix());
					prepareStatement.setInt(9, tag.getCount());
					prepareStatement.setInt(10, tag.getSize());
					prepareStatement.addBatch();
					count++;
				}
				if (count >= partSize) {
					prepareStatement.executeBatch();
					prepareStatement.close();
					prepareStatement = connection.prepareStatement(insertTagSql);
					count = 0;
				}
			}
			if (count > 0) {
				prepareStatement.executeBatch();
			}
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					/*
					 * Oracel 10G的JDBC Driver限制最大Batch size是16383条，
					 * 如果addBatch超过这个限制， 那么executeBatch时就会出现 “无效的批值”（Invalid
					 * Batch Value） 异常。
					 */
					e.printStackTrace();
				}
			}
		}

	}

	public void selectAll(Connection connection) throws SQLException {
		String s1 = "select * from statistics_project";
		String s2 = "select * from project_object";
		String s3 = "select * from object_tag";
		String s4 = "select * from object_dependency";

		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(s1);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				String string = executeQuery.getString(5);
				System.out.println("statistics_project index5 : ");
				System.out.println(string);
			}
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			prepareStatement = connection.prepareStatement(s2);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				String string = executeQuery.getString(5);
				System.out.println("Project Object index5 : ");
				System.out.println(string);
			}
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			prepareStatement = connection.prepareStatement(s3);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				String string = executeQuery.getString(5);
				System.out.println("Object tags index5 : ");
				System.out.println(string);
			}
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			prepareStatement = connection.prepareStatement(s4);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				String string = executeQuery.getString(5);
				System.out.println("Object tags index5 : ");
				System.out.println(string);
			}
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void saveDependencies(Connection connection) throws SQLException {
		PreparedStatement prepareStatement = null;

		try {
			prepareStatement = connection.prepareStatement(insertDependencySql);
			for (ProjectObject po : poList) {
				List<Dependency> dependencies = po.getDependencies();
				for (Dependency d : dependencies) {
					prepareStatement.setInt(1, Integer.valueOf(d.getObjectID()));
					prepareStatement.setInt(2, Integer.valueOf(po.getProjectId()));
					prepareStatement.setInt(3, Integer.valueOf(d.getDependencyObjectID()));
					prepareStatement.addBatch();
				}
			}
			prepareStatement.executeBatch();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static public StatisticsProject[] readAllProject(Connection connection) throws SQLException {

		List<StatisticsProject> ps = new ArrayList<StatisticsProject>();

		Statement prepareStatement = null;
		try {
			prepareStatement = connection.createStatement();
			ResultSet rs = prepareStatement.executeQuery(selectAllProject);

			while (rs.next()) {
				int dID = rs.getInt(1);
				String projectName = rs.getString(2);
				String storer = rs.getString(3);
				Date storeDate = rs.getDate(4);
				String repositoryType = rs.getString(5);
				String repositoryRevision = rs.getString(6);
				String repositoryPath = rs.getString(7);

				StatisticsProject project = new StatisticsProject(projectName);

				project.setProjectId(String.valueOf(dID));
				project.setRepositoryPath(repositoryPath);
				project.setRepositoryRevision(repositoryRevision);
				project.setRepositoryType(repositoryType);
				project.setStoreDate(DateFormat.getDateInstance(DateFormat.DEFAULT).format(storeDate));
				project.setStorer(storer);
				ps.add(project);

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ps.toArray(new StatisticsProject[ps.size()]);

	}

	public static void dropTables(Connection connection) {

		Statement prepareStatement = null;
		try {
			prepareStatement = connection.createStatement();
			prepareStatement.addBatch(dropDependencyS);
			prepareStatement.addBatch(dropDependencyTable);
			prepareStatement.addBatch(dropObjectTable);
			prepareStatement.addBatch(dropProjectS);
			prepareStatement.addBatch(dropProjectTable);
			prepareStatement.addBatch(dropTagS);
			prepareStatement.addBatch(dropTagTable);

			prepareStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void createTables(Connection connection) {

		Statement prepareStatement = null;
		try {
			prepareStatement = connection.createStatement();
			prepareStatement.addBatch(createProjectTableSql);
			prepareStatement.addBatch(createProjectObjectTableSql);
			prepareStatement.addBatch(createDependencyTableSql);
			prepareStatement.addBatch(createObjectTagTableSql);
			prepareStatement.addBatch(createProjectS);
			prepareStatement.addBatch(createObjectTagS);
			prepareStatement.addBatch(createDependencyS);

			prepareStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Status saveAll(Connection connection) {

		if (StatisticsProject.NONE_PROJECT.equals(project)) {
			Status warning = new Status(Status.WARNING);
			warning.setMessage("需要绑定 " + "工程后才可以保存统计信息。");
			return warning;
		}
		try {
			setProjectID(connection);
			int maxObjectID = getMaxObjectID(connection);
			this.saveObjects(BATCH_SIZE, connection, maxObjectID);
			this.saveTags(BATCH_SIZE, connection);
			saveDependencies(connection);
		} catch (SQLException e) {
			Status es = new Status(Status.ERROR);
			es.setMessage(e.getMessage());

			e.printStackTrace();
			return es;
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return Status.OK_STATUS;
	}

	private int getMaxObjectID(Connection connection) throws SQLException {
		String selectMaxObjectID = "SELECT max(id) max from project_object ";
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(selectMaxObjectID);
			ResultSet executeQuery = prepareStatement.executeQuery();
			executeQuery.next();
			int maxObjectID = executeQuery.getInt(1);
			return maxObjectID;
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void setProjectID(Connection connection) throws SQLException {
		PreparedStatement prepareStatement = null;

		try {
			prepareStatement = connection.prepareStatement(selectProjectID);
			ResultSet executeQuery = prepareStatement.executeQuery();
			executeQuery.next();
			int projectID = executeQuery.getInt(1);
			project.setProjectId(String.valueOf(projectID));
			this.saveProject(connection);

		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public StatisticsResult readAll(Connection connection) {
		result = new StatisticsResult();
		poMap = new HashMap<Integer, ProjectObject>();
		result.setProject(project);

		readProjectObject(connection);
		readAllTag(connection);

		readAllDependecies(connection);
		for (List<String> list : reference.values()) {
			for (String s : list) {
				referenced.put(s, referenced.get(s) == null ? 1 : referenced.get(s) + 1);
			}
		}
		for (ProjectObject po : result.getProjectObjects()) {
			if (referenced.get(po.getObjectId()) != null) {
				po.setReferenced(referenced.get(po.getObjectId()).intValue());
			}
		}
		return result;
	}

	private void readAllDependecies(Connection connection) {
		String pid = this.project.getProjectId();
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(selectAllDependenciesSql);
			prepareStatement.setInt(1, Integer.valueOf(pid));
			ResultSet rs = prepareStatement.executeQuery();
			int index = 0;
			while (rs.next()) {
				// id,object_id,project_id,dependency_object_id
				int dID = rs.getInt(1);
				int objectID = rs.getInt(2);
				int projectID = rs.getInt(3);
				int dependencyObjectID = rs.getInt(4);

				Dependency d = new Dependency();
				d.setDependencyID(String.valueOf(dID));
				// d.setDependencyObjectID(String.valueOf(dependencyObjectID));
				// d.setObjectID(String.valueOf(objectID));
				d.setProjectId(String.valueOf(projectID));

				ProjectObject po = poMap.get(objectID);
				po.addDependency(d);
				d.setDependencyObject(poMap.get(dependencyObjectID));
				d.setObject(po);
				d.setProject(project);
				if (reference.get(po.getObjectId()) == null) {
					reference.put(po.getObjectId(), new ArrayList<String>());
				}
				reference.get(po.getObjectId()).add(d.getDependencyObject().getObjectId());
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readAllTag(Connection connection) {
		String pid = this.project.getProjectId();
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(selectAllTagSql);
			prepareStatement.setInt(1, Integer.valueOf(pid));
			ResultSet rs = prepareStatement.executeQuery();
			int index = 0;
			while (rs.next()) {
				// id,object_id,project_id,type,name,namespace,qName,rawName,prefix,count,tag_size
				int tagID = rs.getInt(1);
				int objectID = rs.getInt(2);
				int projectID = rs.getInt(3);
				String type = rs.getString(4);
				String name = rs.getString(5);
				String namespace = rs.getString(6);
				String qName = rs.getString(7);
				String rawName = rs.getString(8);
				String prefix = rs.getString(9);
				int count = rs.getInt(10);
				int tagSize = rs.getInt(11);

				Tag t = new Tag();
				t.setCount(count);
				t.setName(name == null ? "" : name);
				t.setNamespace(namespace == null ? "" : namespace);
				t.setObjectId(String.valueOf(objectID));
				t.setPrefix(prefix == null ? "" : prefix);
				t.setProjectId(String.valueOf(projectID));
				t.setqName(qName == null ? "" : qName);
				t.setRawName(rawName == null ? "" : rawName);
				t.setSize(tagSize);
				t.setTagId(String.valueOf(tagID));
				t.setType(type == null ? "" : type);

				ProjectObject po = poMap.get(objectID);
				t.setObject(po);
				t.setProject(project);
				po.addTag(t);

			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readProjectObject(Connection connection) {
		String pid = this.project.getProjectId();
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(selectAllObjectSql);
			prepareStatement.setInt(1, Integer.valueOf(pid));
			ResultSet rs = prepareStatement.executeQuery();
			int index = 0;
			while (rs.next()) {
				// id,project_id,type,name,path,file_size,script_size
				int objectID = rs.getInt(1);
				int projectID = rs.getInt(2);
				String type = rs.getString(3);
				String name = rs.getString(4);
				String path = rs.getString(5);
				int fileSize = rs.getInt(6);
				int scriptSize = rs.getInt(7);

				ProjectObject po = new ProjectObject();
				po.setFileSize(fileSize);
				po.setName(name);
				po.setObjectId(String.valueOf(objectID));
				po.setPath(path);
				po.setProjectId(String.valueOf(projectID));
				po.setScriptSize(scriptSize);
				po.setType(type);
				po.setProject(project);
				result.addProjectObject(po);
				poMap.put(objectID, po);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
