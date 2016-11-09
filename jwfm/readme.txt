安装oracle驱动到本地maven库
mvn install:install-file -DgroupId=oracle -DartifactId=ojdbc6 -Dversion=11.1 -Dpackaging=jar -Dfile=D:\oracle\product\11.2.0\dbhome_1\jdbc\lib\ojdbc6.jar

在context.xml中配置连接池：
oracle数据库连接池
	<Resource name="CSMISDS" auth="Container" type="javax.sql.DataSource"
		   maxTotal="100" maxIdle="30" maxWaitMillis="10000"
		   username="fast" password="csmis" 
		   driverClassName="oracle.jdbc.driver.OracleDriver"
		   url="jdbc:oracle:thin:@127.0.0.1:1521:orcl" />
mysql数据库连接池
	<Resource name="CSMISDS" auth="Container" type="javax.sql.DataSource"
		   maxTotal="100" maxIdle="30" maxWaitMillis="10000"
		   username="root" password="" 
		   driverClassName="com.mysql.jdbc.Driver"
		   url="jdbc:mysql://localhost:3306/jwfm?useServerPrepStmts=false&amp;useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true" />
创建mysql数据库
create database jwfm DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;