package org.uwpr.metagomics.go_counter.program;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.uwpr.metagomics.dto.RunDTO;
import org.uwpr.metagomics.dto.UploadedFastaFileDTO;
import org.uwpr.metagomics.go_counter.database.RunDAO;
import org.uwpr.metagomics.utils.VersionUtils;
import org.uwpr.metaomics.molly.stats.StatsUtils;
import org.uwpr.metaproteomics.emma.go.GONode;
import org.uwpr.metaproteomics.emma.go.GONodeFactory;

public class RunComparisonProcessor {

	public static void generateRunComparisons( Map<String, Map<String, SingleRunGraphOb>> data, RunDTO run, UploadedFastaFileDTO upload, Map<String, HashMap<String, Collection<String>>> proteinInfoForGoNodes ) throws Exception {
		
		System.out.println( "Calling generateRunComparisons..." );
		
		Collection<String> aspects = new HashSet<>();
		aspects.add( "biological_process" );
		aspects.add( "molecular_function" );
		aspects.add( "cellular_component" );
		
		
		File dataDirectory = new File( Constants.DATA_DOWNLOAD_DIRECTORY, upload.getUniqueId() );
		Pattern p = Pattern.compile( "go_report_(\\d+)\\.txt" );

		for( File previousAnalysisFile : dataDirectory.listFiles() ) {
			
			System.out.println( "\tfound file: " + previousAnalysisFile.getName() );
			
			if( previousAnalysisFile.isDirectory() ) { continue; }
			Matcher m = p.matcher( previousAnalysisFile.getName() );
			
			if( m.matches() ) {

				System.out.println( "\t\tUsing this file." );
				
				// initialize the data comparing these runs
				Map<String, Map<String, TwoRunGraphOb>> COMPARE_DATA = new HashMap<>();
				for( String aspect : aspects ) {
					COMPARE_DATA.put( aspect, new HashMap<>() );
				}

				
				
				int otherRunId = Integer.parseInt( m.group( 1 ) );
				
				System.out.println( "\t\tRun id of uploaded run: " + run.getId() );
				System.out.println( "\t\tGot run id of " + otherRunId + " for other file." );
				
				
				RunDTO otherRun = RunDAO.getInstance().getRun( otherRunId );
						
				if( run.getId() == otherRunId ) {
					continue;
				}
				
				
				String outputFilenameBase = getOutputFileName( run.getId(), otherRunId );

				Map<String, Map<String, SingleRunGraphOb>> otherData = getDataFromOtherFile( previousAnalysisFile, otherRun );
				Map<String, HashMap<String, Collection<String>>> otherProteinData = getProteinDataFromOtherFile(previousAnalysisFile);
				
				// ensure we're also moving from lower run id to higher run id for comparisons
				Map<String, Map<String, SingleRunGraphOb>> dataSet1 = null;
				Map<String, Map<String, SingleRunGraphOb>> dataSet2 = null;
				
				RunDTO run1 = null;
				RunDTO run2 = null;

				Map<String, HashMap<String, Collection<String>>> run1ProteinData = null;
				Map<String, HashMap<String, Collection<String>>> run2ProteinData = null;


				if( run.getId() < otherRunId ) {
					dataSet1 = data;
					dataSet2 = otherData;
					
					run1 = run;
					run2 = otherRun;

					run1ProteinData = proteinInfoForGoNodes;
					run2ProteinData = otherProteinData;

				} else {
					dataSet2 = data;
					dataSet1 = otherData;
					
					run2 = run;
					run1 = otherRun;

					run2ProteinData = proteinInfoForGoNodes;
					run1ProteinData = otherProteinData;
				}
				
				for( String aspect : aspects ) {
					
					Map<String, SingleRunGraphOb> data1 = dataSet1.get( aspect );
					if( data1 == null ) { data1 = new HashMap<>(); }
					
					Map<String, SingleRunGraphOb> data2 = dataSet2.get( aspect );
					if( data2 == null ) { data2 = new HashMap<>(); }
					
					Collection<String> allGOAccs = new HashSet<>();
					allGOAccs.addAll( data1.keySet() );
					allGOAccs.addAll( data2.keySet() );
					
					int totalGOTermsTested = allGOAccs.size();		// used for bonferroni corrections
					
					List<BigDecimal> PVALUES_FOR_QVALUES = new ArrayList<>();
					
					
					for( String acc : allGOAccs ) {
						
						SingleRunGraphOb ob1 = data1.get( acc );
						SingleRunGraphOb ob2 = data2.get( acc );
						
						TwoRunGraphOb trgo = new TwoRunGraphOb();
						
						trgo.setOb1( ob1 );
						trgo.setOb2( ob2 );
						
						trgo.setNode( GONodeFactory.getInstance().getGONode( acc ) );
						
						trgo.setRun1( run1 );
						trgo.setRun2( run2 );
						
						long count1 = 0;
						if( ob1 != null ) { count1 = ob1.getCount(); }
						
						long count2 = 0;
						if( ob2 != null ) { count2 = ob2.getCount(); }

						// set the log2-fold change
						{
							if( ob1 != null && ob2 != null ) {
								
								double log2first = Math.log( ob1.getRatio() ) / Math.log( 2 );
								double log2second = Math.log( ob2.getRatio() ) / Math.log( 2 );
								
								trgo.setLogChange( log2second - log2first );								
							}
						}
						
						// set the pvalues
						{
							double pvalue = StatsUtils.proportionTest( count1, run1.getTotalPSMCount(), count2, run2.getTotalPSMCount() );
							
							double pvalueCorr = pvalue * totalGOTermsTested;
							if( pvalueCorr > 1 ) { pvalueCorr = 1; }
							
							
							trgo.setPvalue( pvalue );
							trgo.setPvalue_corr( pvalueCorr );
						}
						
						
						// set the laplacian-corrected values
						{

							long lcount1 = count1 + 1;
							long lcount2 = count2 + 1;
							
							long ltotal1 = run1.getTotalPSMCount() + totalGOTermsTested;
							long ltotal2 = run2.getTotalPSMCount() + totalGOTermsTested;
							
							double lratio1 = (double)lcount1 / (double)ltotal1;
							double lratio2 = (double)lcount2 / (double)ltotal2;
							
							double llog2first = Math.log( lratio1 ) / Math.log( 2 );
							double llog2second = Math.log( lratio2 ) / Math.log( 2 );
							
							double llogchange = llog2second - llog2first ;	
							
							double lpvalue = StatsUtils.proportionTest( lcount1, ltotal1, lcount2, ltotal2 );
							
							PVALUES_FOR_QVALUES.add( new BigDecimal( lpvalue ) );
							
							double lpvalueCorr = lpvalue * totalGOTermsTested;
							if( lpvalueCorr > 1 ) { lpvalueCorr = 1; }
							
							trgo.setLaplaceLogChange( llogchange );
							trgo.setLaplacePvalue( lpvalue );
							trgo.setLaplacePvalue_corr( lpvalueCorr );
							trgo.setLaplaceRatio1( lratio1 );
							trgo.setLaplaceRatio2( lratio2 );
						}
						
						
						COMPARE_DATA.get( aspect ).put( acc, trgo );

					}// end iterating over all accs with a given aspect		
					
					
					
					// calculate q-values from p-values
					Map<BigDecimal, Double> QVALUES = StatsUtils.convertPValuestoQValues( PVALUES_FOR_QVALUES );
					
					// go through and add q-values into the data object
					for( String goAcc : COMPARE_DATA.get( aspect ).keySet() ) {
						double pvalue = COMPARE_DATA.get( aspect ).get( goAcc ).getLaplacePvalue();						
						Double qvalue = QVALUES.get( new BigDecimal( pvalue ) );
						
						if( qvalue == null )
							throw new Exception( "Could not find a qvalue for pvalue: " + pvalue );
						
						COMPARE_DATA.get( aspect ).get( goAcc ).setLaplaceQvalue( qvalue );
					}					
					
				}// end iterating over aspects

				
				// write out report of this comparison
				{
				

					File outputFile = new File( dataDirectory, outputFilenameBase + ".txt" );
					FileWriter fw = null;

					try {
						
						fw = new FileWriter( outputFile );
						
						fw.write( "# MetaGOmics GO comparison report\n" );
						fw.write( "# MetaGOmics version: " + VersionUtils.getVersion() + "\n" );
						fw.write( "# Run date: " + new java.util.Date() + "\n" );
						
						fw.write( "# Run1 = " + run1.getNickname() + "\n" );
						fw.write( "# Run2 = " + run2.getNickname() + "\n" );
						
						fw.write( "GO acc\t");
						fw.write( "GO aspect\t");
						fw.write( "GO name\t");
						fw.write( "Run 1 ratio\t");
						fw.write( "Run 1 PSM count\t");
						fw.write( "Run 2 ratio\t");
						fw.write( "Run 2 PSM count\t");
						fw.write( "Log(2) fold change\t");
						fw.write( "Raw p-value\t");
						fw.write( "Bonf. corr. p-value\t");
						fw.write( "Laplace corr. Run 1 ratio\t");
						fw.write( "Laplace corr. Run 2 ratio\t");
						fw.write( "Laplace corr. Log(2) fold change\t");
						fw.write( "Laplace corr. Raw p-value\t");
						fw.write( "Laplace corr. Bonf. corr p-value\t");
						fw.write( "Laplace corr. q-value\t" );
						fw.write( "Run 1 peptides\t" );
						fw.write( "Run 2 peptides\t" );
						fw.write( "Run 1 proteins\t" );
						fw.write( "Run 2 proteins\t" );
						fw.write( "Run 1 blast hits\t" );
						fw.write( "Run 2 blast hits\n" );

						for( String aspect : aspects ) {
							
							for( String acc : COMPARE_DATA.get( aspect ).keySet() ) {
								
								TwoRunGraphOb trgo = COMPARE_DATA.get( aspect ).get( acc );
								
								SingleRunGraphOb ob1 = trgo.getOb1();
								SingleRunGraphOb ob2 = trgo.getOb2();
								
								long count1 = 0;
								if( ob1 != null ) { count1 = ob1.getCount(); }
								
								long count2 = 0;
								if( ob2 != null ) { count2 = ob2.getCount(); }
								
								Double ratio1 = 0.0;
								if( ob1 != null ) { ratio1 = ob1.getRatio(); }
								
								Double ratio2 = 0.0;
								if( ob2 != null ) { ratio2 = ob2.getRatio(); }
								
								fw.write( trgo.getNode().getAcc() + "\t" );
								fw.write( trgo.getNode().getTermType() + "\t" );
								fw.write( trgo.getNode().getName() + "\t" );
								fw.write( ratio1 + "\t" );
								fw.write( count1 + "\t" );
								fw.write( ratio2 + "\t" );
								fw.write( count2 + "\t" );
								
								fw.write( trgo.getLogChange() + "\t" );
								
								fw.write( String.format( "%3.2E", trgo.getPvalue() ) + "\t" );
								fw.write( String.format( "%3.2E", trgo.getPvalue_corr() ) + "\t" );
								
								fw.write( trgo.getLaplaceRatio1() + "\t" );
								fw.write( trgo.getLaplaceRatio2() + "\t" );
								
								fw.write( trgo.getLaplaceLogChange() + "\t" );
								
								fw.write( String.format( "%3.2E", trgo.getLaplacePvalue() ) + "\t" );
								fw.write( String.format( "%3.2E", trgo.getLaplacePvalue_corr() ) + "\t" );
								fw.write( String.format( "%3.2E", trgo.getLaplaceQvalue() ) + "\t" );

								if(run1ProteinData != null && run1ProteinData.containsKey(trgo.getNode().getAcc()))
									fw.write( String.join(",", run1ProteinData.get(trgo.getNode().getAcc()).get("peptides")) + "\t");
								else
									fw.write("\t");

								if(run2ProteinData != null && run2ProteinData.containsKey(trgo.getNode().getAcc()))
									fw.write( String.join(",", run2ProteinData.get(trgo.getNode().getAcc()).get("peptides")) + "\t");
								else
									fw.write("\t");

								if(run1ProteinData != null && run1ProteinData.containsKey(trgo.getNode().getAcc()))
									fw.write( String.join(",", run1ProteinData.get(trgo.getNode().getAcc()).get("proteins")) + "\t");
								else
									fw.write("\t");

								if(run2ProteinData != null && run2ProteinData.containsKey(trgo.getNode().getAcc()))
									fw.write( String.join(",", run2ProteinData.get(trgo.getNode().getAcc()).get("proteins")) + "\t");
								else
									fw.write("\t");

								if(run1ProteinData != null && run1ProteinData.containsKey(trgo.getNode().getAcc()))
									fw.write( String.join(",", run1ProteinData.get(trgo.getNode().getAcc()).get("blastHits")) + "\t");
								else
									fw.write("\t");

								if(run2ProteinData != null && run2ProteinData.containsKey(trgo.getNode().getAcc()))
									fw.write( String.join(",", run2ProteinData.get(trgo.getNode().getAcc()).get("blastHits")) + "\n");
								else
									fw.write("\n");

							}							
						}
						
						
						
					} finally {
						
						if( fw != null ) {
							try {
								fw.close();
								fw = null;
							} catch( Exception e ) {
								;
							}
						}
						
						
						
					}
					
				}// end writing report
				
				
				
				
				// write out images for this comparison
				
				boolean createImages = false;
				try {
					if( Class.forName( "org.uwpr.local.graph_image.TwoRunGOGraphGenerator", false, RunComparisonProcessor.class.getClassLoader() ) != null )
						createImages = true;
					
				} catch( Exception e ) {
					
				}
				
				
				if( createImages ) {
					
					for( String aspect : COMPARE_DATA.keySet() ) {
						
						Map<String, TwoRunGraphOb> compareData = COMPARE_DATA.get( aspect );
						
						System.out.println( "\t\t\t\t\tsize: " + compareData.keySet().size() );

						int itemsRemoved = TrimUtils.trimTree( compareData );
						while( itemsRemoved > 0 ) {
							itemsRemoved = TrimUtils.trimTree( compareData );
						}
						
						System.out.println( "\t\t\t\t\tsize: " + compareData.keySet().size() );

						
						if( compareData.keySet().size() > 0 ) {
							
							File imageFile = new File( dataDirectory, outputFilenameBase + "_" + aspect + ".png" );
			
							try {
								
								Class<?> cls = Class.forName( "org.uwpr.local.graph_image.TwoRunGOGraphGenerator", true, RunComparisonProcessor.class.getClassLoader() );														
								Object obj = cls.newInstance();

								Method method = cls.getDeclaredMethod( "getGOGraphImage", compareData.getClass().getInterfaces()[0] );

								BufferedImage image = (BufferedImage)method.invoke( obj, compareData );
								
							    ImageIO.write(image, "png", imageFile);
							    
							    
							    imageFile = new File( dataDirectory, outputFilenameBase + "_" + aspect + ".svg" );
							    
								Method method2 = cls.getDeclaredMethod( "saveGOGraphSVGImage", compareData.getClass().getInterfaces()[0], imageFile.getClass() );
								method2.invoke( obj, compareData, imageFile );
	
							} catch ( Exception e ) {
								System.out.println( "Error creating images: " + e.getMessage() );
								System.err.println( "Error creating images: " + e.getMessage() );
							}
						}
					}
				}//end writing out images
				
				
				
			}// end if filename matches pattern
		}//end iterating over files
		
		
		
	}	
	
	
	
	
	
	
	
	private static Map<String, Map<String, SingleRunGraphOb>> getDataFromOtherFile( File otherFile, RunDTO otherRun ) throws Exception {
		
		System.out.println( "\t\t\tCalling getDataFromOtherFile()..." );
		
		Map<String, Map<String, SingleRunGraphOb>> otherData = new HashMap<>();
		
		BufferedReader br = null;
		
		try {
			
			br = new BufferedReader( new FileReader( otherFile ) );
			
			String line = br.readLine();
			line = br.readLine(); //skip header line
			
			while( line != null ) {
			
				if( line.startsWith( "GO acc" ) ) {
					line = br.readLine();
					continue;
				}
				
				if( line.startsWith( "#" ) ) {
					line = br.readLine();
					continue;
				}
				
				
				String[] fields = line.split( "\\t" );
				
				String goAcc = fields[ 0 ];
				long count = Long.parseLong( fields[ 3 ] );
				long totalCount = Long.parseLong( fields[ 4 ] );
				double ratio = Double.parseDouble( fields[ 5 ] );
				
				otherRun.setTotalPSMCount( totalCount );
				
				GONode node = GONodeFactory.getInstance().getGONode( goAcc );
				String aspect = node.getTermType();
				
				if( !otherData.containsKey( aspect ) ) {
					otherData.put( aspect, new HashMap<>() );
				}
				
				SingleRunGraphOb ob = new SingleRunGraphOb();
				ob.setCount( count );
				ob.setNode( node );
				ob.setRatio( ratio );
				ob.setRunPsmTotal( totalCount );
				
				otherData.get( aspect ).put( node.getAcc(), ob );
				
				line = br.readLine();
			}
			
		} finally {
			
			if( br != null ) {
				try {
					br.close();
					br = null;
				} catch( Exception e ) { ; }
			}
		}
		
		
		
		
		return otherData;
	}


	private static Map<String, HashMap<String, Collection<String>>> getProteinDataFromOtherFile( File otherFile ) throws Exception {

		System.out.println( "\t\t\tCalling getProteinDataFromOtherFile()..." );

		Map<String, HashMap<String, Collection<String>>> proteinData = new HashMap<>();

		BufferedReader br = null;

		try {

			br = new BufferedReader( new FileReader( otherFile ) );

			String line = br.readLine();
			line = br.readLine(); //skip header line

			while( line != null ) {

				if( line.startsWith( "GO acc" ) ) {
					line = br.readLine();
					continue;
				}

				if( line.startsWith( "#" ) ) {
					line = br.readLine();
					continue;
				}


				String[] fields = line.split( "\\t" );
				if( fields.length != 9) {
					// old style data, just return empty map
					return proteinData;
				}

				String goAcc = fields[ 0 ];

				Collection<String> peptides = Arrays.asList(fields[6].split(","));
				Collection<String> proteins = Arrays.asList(fields[7].split(","));
				Collection<String> blastHits = Arrays.asList(fields[8].split(","));

				proteinData.put(goAcc, new HashMap<>());
				proteinData.get(goAcc).put("peptides", peptides);
				proteinData.get(goAcc).put("proteins", proteins);
				proteinData.get(goAcc).put("blastHits", blastHits);

				line = br.readLine();
			}

		} finally {

			if( br != null ) {
				try {
					br.close();
					br = null;
				} catch( Exception e ) { ; }
			}
		}

		return proteinData;
	}


	
	private static String getOutputFileName( int r1, int r2 ) {
		
		if( r1 <= r2 ) {
			return "go_compare_" + r1 + "_" + r2;
		}
		
		return "go_compare_" + r2 + "_" + r1;
	}
	
}
