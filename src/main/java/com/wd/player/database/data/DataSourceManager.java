package com.wd.player.database.data;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * @author lww
 * @date 2024-07-30 12:14 PM
 */
public class DataSourceManager {

	private static final String MAPPER_LOACTION = "com.wd.player.database.mapper";

	private static SqlSessionFactory sqlSessionFactory;

	static {
		try {
			sqlSessionFactory = DataSourceManager.getSqlSessionFactory();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SneakyThrows
	public static SqlSession getSession() {
		if (sqlSessionFactory == null) {
			sqlSessionFactory = DataSourceManager.getSqlSessionFactory();
		}
		return sqlSessionFactory.openSession(true);
	}

	private static SqlSessionFactory getSqlSessionFactory() throws IOException {
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		//这是mybatis-plus的配置对象，对mybatis的Configuration进行增强
		MybatisConfiguration configuration = new MybatisConfiguration();
		//这是初始化配置，后面会添加这部分代码
		initConfiguration(configuration);
		//这是初始化连接器，如mybatis-plus的分页插件
		configuration.addInterceptor(initInterceptor());
		//配置日志实现
		configuration.setLogImpl(Slf4jImpl.class);
		//扫描mapper接口所在包
		configuration.addMappers(MAPPER_LOACTION);
		//构建mybatis-plus需要的globalconfig
		GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
		//此参数会自动生成实现baseMapper的基础方法映射
		globalConfig.setSqlInjector(new DefaultSqlInjector());
		//设置id生成器
		globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
		//设置超类mapper
		globalConfig.setSuperMapperClass(BaseMapper.class);
		//设置数据源
		Environment environment = new Environment("1", new JdbcTransactionFactory(), initDataSource());
		configuration.setEnvironment(environment);
		registryMapperXml(configuration, "mapper");
		//构建sqlSessionFactory
		SqlSessionFactory sqlSessionFactory = builder.build(configuration);
		//创建session
		return sqlSessionFactory;
	}

	private static DataSource initDataSource() {
		try {
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.setDriverClassName("org.h2.Driver");
			String property = System.getProperty("user.dir");
			//dataSource.setUrl("jdbc:h2:file:" + property + "/player.db;MODE=MySQL;AUTO_SERVER=TRUE;");
			dataSource.setUrl("jdbc:h2:file:" + property + "/player.db;MODE=MySQL;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM 'classpath:schema.sql';");
			System.out.println(dataSource.getUrl());
			//dataSource.setUsername("root");
			//dataSource.setPassword("admin");
			dataSource.setInitialSize(1);
			dataSource.setMaxActive(20);
			dataSource.setMinIdle(1);
			dataSource.setMaxWait(60_000);
			dataSource.setPoolPreparedStatements(true);
			dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
			dataSource.setTimeBetweenEvictionRunsMillis(60_000);
			dataSource.setMinEvictableIdleTimeMillis(300_000);
			dataSource.setValidationQuery("SELECT 1");
			return dataSource;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * 初始化拦截器
	 */
	private static Interceptor initInterceptor() {
		//创建mybatis-plus插件对象
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		//构建分页插件
		PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
		paginationInnerInterceptor.setDbType(DbType.MYSQL);
		paginationInnerInterceptor.setOverflow(true);
		paginationInnerInterceptor.setMaxLimit(500L);
		interceptor.addInnerInterceptor(paginationInnerInterceptor);
		return interceptor;
	}

	/**
	 * 初始化配置
	 */
	private static void initConfiguration(MybatisConfiguration configuration) {
		//开启驼峰大小写转换
		configuration.setMapUnderscoreToCamelCase(true);
		//配置添加数据自动返回数据主键
		configuration.setUseGeneratedKeys(true);
	}

	/**
	 * 解析mapper.xml文件
	 */
	private static void registryMapperXml(MybatisConfiguration configuration, String classPath) throws IOException {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> mapper = contextClassLoader.getResources(classPath);
		while (mapper.hasMoreElements()) {
			URL url = mapper.nextElement();
			if (url.getProtocol().equals("file")) {
				String path = url.getPath();
				File file = new File(path);
				File[] files = file.listFiles();
				for (File f : files) {
					FileInputStream in = new FileInputStream(f);
					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, f.getPath(), configuration.getSqlFragments());
					xmlMapperBuilder.parse();
					in.close();
				}
			} else {
				JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
				JarFile jarFile = urlConnection.getJarFile();
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					if (jarEntry.getName().endsWith(".xml") && jarEntry.getName().startsWith(classPath)) {
						InputStream in = jarFile.getInputStream(jarEntry);
						XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, jarEntry.getName(), configuration.getSqlFragments());
						xmlMapperBuilder.parse();
						in.close();
					}
				}
			}
		}
	}

}
