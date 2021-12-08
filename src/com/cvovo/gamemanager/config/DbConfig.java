package com.cvovo.gamemanager.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableJpaRepositories("com.cvovo.gamemanager")
@EnableTransactionManagement
public class DbConfig {

	private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);

	@Value("${callBack.pay}")
	public String CALLBACK_PAY;

	@Value("${gm.db.address}")
	public String NETADDRESS;

	@Value("${gm.db.database}")
	public String DATABASE;

	@Value("${gm.db.username}")
	public String USERNAME;

	@Value("${gm.db.password}")
	public String PASSWORD;

	@Bean
	public DataSource dataSource() throws SQLException {
		logger.info("dataSource");
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://" + NETADDRESS + "?useUnicode=true&characterEncoding=utf-8");
		dataSource.setDefaultCatalog(DATABASE);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setFilters("stat");
		dataSource.setMaxActive(100);
		dataSource.setInitialSize(1);
		dataSource.setMaxWait(6000);
		dataSource.setMinIdle(1);
		dataSource.setTimeBetweenEvictionRunsMillis(3000);
		dataSource.setMinEvictableIdleTimeMillis(300000);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
		return dataSource;
	}

	@Bean
	public FactoryBean<EntityManagerFactory> entityManagerFactory(DataSource dataSource) throws SQLException {
		logger.info("entityManagerFactory");
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setPackagesToScan("com.cvovo.gamemanager");
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setJpaProperties(addJpaProperties());
		return entityManagerFactoryBean;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) throws Exception {
		logger.info("transactionManager");
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	private Properties addJpaProperties() {
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL57InnoDBDialect");
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", "update");
		jpaProperties.setProperty("hibernate.current_session_context_class", "thread");

		jpaProperties.setProperty("hibernate.max_fetch_depth", "1");
		jpaProperties.setProperty("hibernate.default_batch_fetch_size ", "4");
		jpaProperties.setProperty("hibernate.jdbc.fetch_size", "30");
		jpaProperties.setProperty("hibernate.jdbc.batch_size", "50");

		jpaProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
		jpaProperties.setProperty("net.sf.ehcache.configurationResourceName", "ehcache-hibernate.xml");
		jpaProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
		jpaProperties.setProperty("hibernate.cache.use_query_cache", "true");

		jpaProperties.setProperty("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

		jpaProperties.setProperty("hibernate.show_sql", "false");
		jpaProperties.setProperty("hibernate.format_sql", "false");
		jpaProperties.setProperty("hibernate.generate_statistics", "false");
		jpaProperties.setProperty("hibernate.use_sql_comments", "false");
		return jpaProperties;
	}

}
