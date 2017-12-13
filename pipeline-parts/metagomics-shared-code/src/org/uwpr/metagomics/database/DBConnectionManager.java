package org.uwpr.metagomics.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

public class DBConnectionManager {

	public static final String DB = "metagomics";
	public static final String GO_DB = "mygo_201512";	
	public static final String GO_ANNO_DB = "compgo_uniprot";	
	public static final String TAXONOMY_DB = "ncbi_taxonomy_201611";
	public static final String UNIPROT_TAXON_LOOKUP = "uniprot_taxa";	

	private Map<String, BasicDataSource> dataSources = new HashMap<String, BasicDataSource>();
	
	private static final DBConnectionManager _INSTANCE = new DBConnectionManager();
	public static DBConnectionManager getInstance() { return _INSTANCE; }
	
	
	private String _USERNAME;
	private String _PASSWORD;
	private String _HOST;
	
	/**
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String db) throws Exception {

		if( this._USERNAME == null )
			this.loadDatabaseProperties();
		
		try {

			if( !db.equals( DB ) && !db.equals( GO_DB ) && !db.equals( GO_ANNO_DB ) && !db.equals( TAXONOMY_DB ) &&
					!db.equals( UNIPROT_TAXON_LOOKUP ) )
				throw new Exception( "Did not get a valid database." );
			
			if( !dataSources.containsKey( db ) ) {
				BasicDataSource ds = new BasicDataSource();
		        ds.setDriverClassName("com.mysql.jdbc.Driver");
		        ds.setUsername( _USERNAME );
		        ds.setPassword( _PASSWORD );
		        
		        
		        ds.setUrl("jdbc:mysql://" + _HOST + "/" + db );
				
		        dataSources.put( db, ds );				
			}
			
			
			return dataSources.get( db ).getConnection();			
			
		} catch (Throwable t) {
			throw new SQLException("Exception: " + t.getMessage());
		}
	}
	
	private void loadDatabaseProperties() throws Exception {
		
		
		ClassLoader thisClassLoader = this.getClass().getClassLoader();
		InputStream propertiesFileAsStream = thisClassLoader.getResourceAsStream( "metagomics.db.properties" );

		if ( propertiesFileAsStream == null ) {

			throw new Exception( "Could not find metagomics.db.properties in class path." );
		}

		Properties properties = new Properties();
		properties.load( propertiesFileAsStream );

		this._USERNAME = properties.getProperty( "username" );
		this._PASSWORD = properties.getProperty( "password" );
		this._HOST = properties.getProperty( "host" );
		
		try {
			propertiesFileAsStream.close();
		} catch( Exception e ) { ; }
		
		
		if( this._USERNAME == null || this._PASSWORD == null || this._HOST == null )
			throw new Exception( "Could not load database properties. Did not find username, password, or host." );
		
	}
	

}