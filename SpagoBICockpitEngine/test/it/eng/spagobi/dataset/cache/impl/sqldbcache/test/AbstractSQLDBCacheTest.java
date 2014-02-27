/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.dataset.cache.impl.sqldbcache.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.FileCreatorConfiguration;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.dataset.cache.CacheFactory;
import it.eng.spagobi.dataset.cache.ICache;
import it.eng.spagobi.dataset.cache.impl.sqldbcache.DataType;
import it.eng.spagobi.dataset.cache.impl.sqldbcache.SQLDBCache;
import it.eng.spagobi.dataset.cache.test.FakeDatamartRetriever;
import it.eng.spagobi.dataset.cache.test.TestConstants;
import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tenant.TenantManager;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.FlatDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import junit.framework.TestCase;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public abstract class AbstractSQLDBCacheTest extends TestCase {
	protected static ICache cache = null;
	
	protected JDBCDataSet sqlDataset;
	protected QbeDataSet qbeDataset;
	protected FileDataSet fileDataset;
	protected FlatDataSet flatDataset;
	protected ScriptDataSet scriptDataset;
	protected DataSource dataSourceReading;
	protected DataSource dataSourceWriting;
	
	static private Logger logger = Logger.getLogger(AbstractSQLDBCacheTest.class);
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("AF_CONFIG_FILE", TestConstants.AF_CONFIG_FILE);
    	ConfigSingleton.setConfigurationCreation( new FileCreatorConfiguration( TestConstants.WEBCONTENT_PATH ) );

    	TenantManager.setTenant(new Tenant("SPAGOBI"));

		//Creating DataSources and DataSets
		this.createDataSources();
		this.createDatasets();
		
		if (cache == null){
			CacheFactory cacheFactory = new CacheFactory();
			cache = cacheFactory.getCache(dataSourceWriting);
			if (cache instanceof SQLDBCache){
				DataType dataType = new DataType(); //class used for setting data type dimension properties
				((SQLDBCache)cache).setObjectsTypeDimension(dataType.getProps());
			}
		}


	}
	
	//Test cases
	
	public void testCacheInit(){
		assertNotNull("Cache correctly initialized", cache );
	}
	
	public void testCachePutJDBCDataSet(){
		IDataStore resultset;	
		
		sqlDataset.loadData();
		resultset = sqlDataset.getDataStore();
		cache.put(sqlDataset, sqlDataset.getSignature(), resultset);
		assertNotNull(cache.get(sqlDataset.getSignature()));
		logger.debug("JDBCDataset inserted inside cache");
	}
	
	public void testCachePutFileDataSet(){
		IDataStore resultset;

		fileDataset.loadData();
		resultset =	fileDataset.getDataStore();
		cache.put(fileDataset, fileDataset.getSignature(), resultset);
		assertNotNull(cache.get(fileDataset.getSignature()));
		logger.debug("FileDataSet inserted inside cache");
	}
	
	public void testCachePutQbeDataSet(){		
		IDataStore resultset;

		qbeDataset.loadData();
		resultset =	qbeDataset.getDataStore();
		cache.put(qbeDataset, qbeDataset.getSignature(), resultset);
		assertNotNull(cache.get(qbeDataset.getSignature()));
		logger.debug("QbeDataSet inserted inside cache");
		
	}
	
	public void testCachePutFlatDataSet(){		
		IDataStore resultset;

		flatDataset.loadData();
		resultset =	flatDataset.getDataStore();
		cache.put(flatDataset, flatDataset.getSignature(), resultset);
		assertNotNull(cache.get(flatDataset.getSignature()));
		logger.debug("FlatDataSet inserted inside cache");
		
	}
	
	public void testCachePutScriptDataSet(){		
		IDataStore resultset;

		scriptDataset.loadData();
		resultset =	scriptDataset.getDataStore();
		cache.put(scriptDataset, scriptDataset.getSignature(), resultset);
		assertNotNull(cache.get(scriptDataset.getSignature()));
		logger.debug("ScriptDataset inserted inside cache");
		
	}	
	
	public void testCacheGetJDBCDataSet(){
		String signature = sqlDataset.getSignature();
		IDataStore dataStore = cache.get(signature);
		assertNull(dataStore);
		if (dataStore == null){
			sqlDataset.loadData();
			dataStore =	sqlDataset.getDataStore();
			cache.put(sqlDataset,signature,dataStore);
		}
		dataStore = cache.get(signature);
		assertNotNull(dataStore);
	}
	
	public void testCacheGetFileDataSet(){
		String signature = fileDataset.getSignature();
		IDataStore dataStore = cache.get(signature);
		assertNull(dataStore);
		if (dataStore == null){
			fileDataset.loadData();
			dataStore =	fileDataset.getDataStore();
			cache.put(fileDataset,signature,dataStore);
		}
		dataStore = cache.get(signature);
		assertNotNull(dataStore);
	}
	
	public void testCacheGetQbeDataSet(){
		String signature = qbeDataset.getSignature();
		IDataStore dataStore = cache.get(signature);
		assertNull(dataStore);
		if (dataStore == null){
			qbeDataset.loadData();
			dataStore =	qbeDataset.getDataStore();
			cache.put(qbeDataset,signature,dataStore);
		}
		dataStore = cache.get(signature);
		assertNotNull(dataStore);
	}	
	
	public void testCacheGetFlatDataSet(){
		String signature = flatDataset.getSignature();
		IDataStore dataStore = cache.get(signature);
		assertNull(dataStore);
		if (dataStore == null){
			flatDataset.loadData();
			dataStore =	flatDataset.getDataStore();
			cache.put(flatDataset,signature,dataStore);
		}
		dataStore = cache.get(signature);
		assertNotNull(dataStore);
	}
	
	public void testCacheGetScriptDataSet(){
		String signature = scriptDataset.getSignature();
		IDataStore dataStore = cache.get(signature);
		assertNull(dataStore);
		if (dataStore == null){
			scriptDataset.loadData();
			dataStore =	scriptDataset.getDataStore();
			cache.put(scriptDataset,signature,dataStore);
		}
		dataStore = cache.get(signature);
		assertNotNull(dataStore);
	}	
	
	//------------------------------------------------------------------------------
	
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		cache.deleteAll();
		//clean cache in memory and on db
	}
	
	/*
	* ----------------------------------------------------
	* Initialization Methods
	* ----------------------------------------------------
	*/
	
	public void createDataSources(){
		//Must be overridden by specific implementation
		//dataSourceReading = TestDataSourceFactory.createDataSource(TestConstants.DatabaseType.MYSQL, false);
		//dataSourceWriting = TestDataSourceFactory.createDataSource(TestConstants.DatabaseType.MYSQL, true);
		logger.error("Specific DataSource must be specified in specialized Test");
	}
	
	public JDBCDataSet createJDBCDataset(){
		//Create JDBCDataSet
		sqlDataset = new JDBCDataSet();
		sqlDataset.setQuery("select * from customer");
		sqlDataset.setQueryScript("");
		sqlDataset.setQueryScriptLanguage("");
		sqlDataset.setDataSource(dataSourceReading);
		sqlDataset.setLabel("test_jdbcDataset");
		return sqlDataset;
	}
	
	public FileDataSet createFileDataset(){
		fileDataset = new FileDataSet();

		try {
			fileDataset.setFileType("CSV");
			JSONObject jsonConf = new JSONObject();
			jsonConf.put("fileType", "CSV");
			jsonConf.put("fileName", "customers.csv");
			jsonConf.put("csvDelimiter", ",");
			jsonConf.put("csvDelimiter", ",");
			jsonConf.put("csvQuote", "\"");
			jsonConf.put("csvEncoding", "UTF-8");
			jsonConf.put("DS_SCOPE", "USER");	
			fileDataset.setDsMetadata("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><META version=\"1\"><COLUMNLIST><COLUMN alias=\"customer_id\" fieldType=\"ATTRIBUTE\" name=\"customer_id\" type=\"java.lang.Integer\"/><COLUMN alias=\"lname\" fieldType=\"ATTRIBUTE\" name=\"lname\" type=\"java.lang.String\"/><COLUMN alias=\"fname\" fieldType=\"ATTRIBUTE\" name=\"fname\" type=\"java.lang.String\"/><COLUMN alias=\"num_children_at_home\" fieldType=\"ATTRIBUTE\" name=\"num_children_at_home\" type=\"java.lang.Integer\"/></COLUMNLIST><DATASET><PROPERTY name=\"resultNumber\" value=\"49\"/> </DATASET></META>");			
			fileDataset.setConfiguration(jsonConf.toString());
			fileDataset.setResourcePath(TestConstants.RESOURCE_PATH);
			fileDataset.setFileName("customers.csv");
			fileDataset.setLabel("test_fileDataset");

		}
		catch(JSONException e){
			logger.error("JSONException when creating a FileDataset");
		}
		return fileDataset;


	}
	
	public QbeDataSet createQbeDataset(){
		qbeDataset = new QbeDataSet();
		qbeDataset.setJsonQuery("{\"catalogue\": {\"queries\": [{\"id\":\"q1390389018208\",\"distinct\":false,\"isNestedExpression\":false,\"fields\":[{\"alias\":\"Lname\",\"visible\":true,\"include\":true,\"type\":\"datamartField\",\"id\":\"it.eng.spagobi.meta.Customer:lname\",\"entity\":\"Customer\",\"field\":\"Lname\",\"longDescription\":\"Customer : Lname\",\"group\":\"\",\"funct\":\"NONE\",\"iconCls\":\"attribute\",\"nature\":\"attribute\"},{\"alias\":\"Fname\",\"visible\":true,\"include\":true,\"type\":\"datamartField\",\"id\":\"it.eng.spagobi.meta.Customer:fname\",\"entity\":\"Customer\",\"field\":\"Fname\",\"longDescription\":\"Customer : Fname\",\"group\":\"\",\"funct\":\"NONE\",\"iconCls\":\"attribute\",\"nature\":\"attribute\"},{\"alias\":\"City\",\"visible\":true,\"include\":true,\"type\":\"datamartField\",\"id\":\"it.eng.spagobi.meta.Customer:city\",\"entity\":\"Customer\",\"field\":\"City\",\"longDescription\":\"Customer : City\",\"group\":\"true\",\"funct\":\"NONE\",\"iconCls\":\"attribute\",\"nature\":\"attribute\"}],\"filters\":[],\"expression\":{},\"havings\":[],\"subqueries\":[]}]}, \t\"version\":7,\t\"generator\": \"SpagoBIMeta\" }\t");
		qbeDataset.setResourcePath(TestConstants.RESOURCE_PATH);
		qbeDataset.setDatamarts("MyModel41");
		qbeDataset.setDataSource(dataSourceReading);
		qbeDataset.setDataSourceForWriting(dataSourceWriting);
		Map params = new HashMap();
		FakeDatamartRetriever fakeDatamartRetriever = new FakeDatamartRetriever();
		fakeDatamartRetriever.setResourcePath(TestConstants.RESOURCE_PATH);
		params.put(SpagoBIConstants.DATAMART_RETRIEVER, fakeDatamartRetriever);
		qbeDataset.setParamsMap(params);
		qbeDataset.setLabel("test_qbeDataset");
		return qbeDataset;
	}
	
	public FlatDataSet createFlatDataset(){
		flatDataset = new FlatDataSet();
		flatDataset.setDataSource(dataSourceReading);
		flatDataset.setTableName("department"); //name of the table corresponding to the flat dataset (persisted dataset)
		flatDataset.setLabel("test_flatDataset");
		return flatDataset;
	}
	
	public ScriptDataSet createScriptDataSet(){
		scriptDataset=new ScriptDataSet();
		scriptDataset.setScriptLanguage("groovy");
		scriptDataset.setScript("returnValue(new Double(5).toString());\n");
		scriptDataset.setLabel("test_scriptDataset");
		return scriptDataset;
	}
	
	public void createDatasets() throws JSONException{
		createJDBCDataset();
		createFileDataset();
		createQbeDataset();
		createFlatDataset();
		createScriptDataSet();

	}

}