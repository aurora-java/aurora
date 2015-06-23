package aurora.ide.api.statistics;

public interface SQL {
	String insertProjectSql = "INSERT INTO statistics_project (id,projectName,storer,storeDate,repositoryType,repositoryRevision,repositoryPath) " + " VALUES (?,?,?,sysdate,?,?,?)";

	String insertObjectSql = "INSERT INTO project_object (id,project_id,type,name,path,file_size,script_size) " + " VALUES (?,?,?,?,?,?,?)";

	String insertTagSql = "INSERT INTO object_tag (id,object_id,project_id,type,name,namespace,qName,rawName,prefix,count,tag_size)" + "VALUES (object_tag_s.nextval,?,?,?,?,?,?,?,?,?,?)";

	String insertDependencySql = "INSERT INTO object_dependency (id,object_id,project_id,dependency_object_id)" + "VALUES (object_dependency_s.nextval,?,?,?)";

	String createProjectTableSql = "CREATE TABLE statistics_project (id number,projectName varchar2(100)," + "storer varchar2 (200),storeDate date,repositoryType varchar2(100),repositoryRevision varchar2(100),repositoryPath varchar2(256))";
	String createProjectObjectTableSql = "CREATE TABLE project_object( id number,project_id number," + "type varchar2(100),name varchar2(200),path varchar2(256),file_size number,script_size number)";
	String createProjectS = "CREATE SEQUENCE statistics_project_s";
	String createObjectTagTableSql = "CREATE TABLE object_tag (id number,object_id number,project_id number," + "type varchar2(100),name varchar2(100),namespace varchar2(100),qName varchar2(100),rawName varchar2(100)," + "prefix varchar2(100),count number,tag_size number)";
	String createObjectTagS = "CREATE SEQUENCE object_tag_s";
	String createDependencyTableSql = "CREATE TABLE object_dependency (id number,object_id number," + "project_id number,dependency_object_id number)";
	String createDependencyS = "CREATE SEQUENCE object_dependency_s";

	String dropProjectTable = "DROP TABLE statistics_project";
	String dropObjectTable = "DROP TABLE project_object";
	String dropTagTable = "DROP TABLE object_tag";
	String dropDependencyTable = "DROP TABLE object_dependency";
	String dropProjectS = "DROP SEQUENCE statistics_project_s";
	String dropTagS = "DROP SEQUENCE object_tag_s";
	String dropDependencyS = "DROP SEQUENCE object_dependency_s";

	String selectAllObjectSql = "SELECT * FROM project_object WHERE project_id = ? ";
	String selectAllTagSql = "SELECT * FROM object_tag WHERE project_id = ? ";
	String selectAllDependenciesSql = "SELECT * FROM object_dependency WHERE project_id = ?";
	String selectAllProject = "SELECT * FROM statistics_project";

	String selectProjectID = "SELECT statistics_project_s.nextval from dual ";

}
