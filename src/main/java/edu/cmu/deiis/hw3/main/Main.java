package edu.cmu.deiis.hw3.main;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.util.XMLInputSource;

class Main {
	public static void main(String[] args) throws Exception {
		//parse CPE descriptor in file specified on command line
		CpeDescription cpeDesc = UIMAFramework.getXMLParser().
		parseCpeDescription(new XMLInputSource(
			"/home/perez/ITAM/DEIIS/workspace/hw3-114297/src/main/resources/descriptors/cpe.xml"
		));
		//instantiate CPE
		CollectionProcessingEngine mCPE = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);
		//Create and register a Status Callback Listener
		mCPE.addStatusCallbackListener(new StatusCallbackListener() {

			@Override
			public void initializationComplete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void batchProcessComplete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void collectionProcessComplete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void paused() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void resumed() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aborted() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void entityProcessComplete(CAS aCas,
					EntityProcessStatus aStatus) {
				// TODO Auto-generated method stub
				
			}
			
		});
		//Start Processing
		mCPE.process();
	}

}
