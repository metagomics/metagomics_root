package org.uwpr.metagomics.go_counter.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ProteinSearcherCache {

	private static ProteinSearcherCache _INSTANCE = new ProteinSearcherCache();
	public static ProteinSearcherCache getInstance() { return _INSTANCE; }
	
	private ProteinSearcherCache() {
		this.CACHE_MAP = new HashMap<>();
	}
	
	/**
	 * Add the proteinIds to the cache for this peptideId and fastaFileId. This will
	 * NOT overwrite existing proteinIds that have been cached.
	 * 
	 * @param fastaFileId
	 * @param peptideId
	 * @param proteinIds
	 */
	public void addToCache( int fastaFileId, int peptideId, Collection<Integer> proteinIds ) {

		if( proteinIds == null ) return;
		
		if( !this.CACHE_MAP.containsKey( fastaFileId ) )
			this.CACHE_MAP.put( fastaFileId,  new HashMap<>() );
		
		if( !this.CACHE_MAP.get( fastaFileId ).containsKey( peptideId ) )
			this.CACHE_MAP.get( fastaFileId ).put( peptideId,  new HashSet<>() );
		
		
		this.CACHE_MAP.get( fastaFileId ).get( peptideId ).addAll( proteinIds );
	}

	
	/**
	 * Get the proteinIds for the given fastaFileId and peptideId. Returns null if nothing
	 * has been cached.
	 * 
	 * @param fastaFileId
	 * @param peptideId
	 * @return
	 */
	public Collection<Integer> getProteinIdsFromCache( int fastaFileId, int peptideId ) {
		if( this.CACHE_MAP.containsKey( fastaFileId ) &&
			this.CACHE_MAP.get( fastaFileId ).containsKey( peptideId ) ) {
			
			return this.CACHE_MAP.get( fastaFileId ).get( peptideId );
		}
		
		return null;
	}
	
	
	
	private Map<Integer, Map<Integer, Collection<Integer>>> CACHE_MAP;
}
