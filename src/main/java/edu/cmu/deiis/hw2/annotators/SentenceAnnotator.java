package edu.cmu.deiis.hw2.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.*;


public class SentenceAnnotator extends JCasAnnotator_ImplBase  {
	Pattern lineSep = Pattern.compile("[\\w\\d \\t\\?\\.,']+");
	final String annotatorName = "SentenceAnnotator";
	
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.println("Entering SentenceAnnotator");
		String text = aJCas.getDocumentText();
		Question q;
		Answer ans;

		Matcher mlines = lineSep.matcher(text);
		char c;
		
		String l;
		while(mlines.find()) {
			c = text.charAt(mlines.start());
			l = text.substring(mlines.start(), mlines.end());
			System.out.println("match!: " +c +" [" +mlines.start() +"," +mlines.end()+"] " +l);
			if(c=='Q') {
				q = new Question(aJCas);
				q.setBegin(mlines.start() + 2);
				q.setEnd(mlines.end());
				q.setCasProcessorId(this.annotatorName);
				q.setConfidence(1.0); //TODO: calcular
				q.addToIndexes();
			}
			else if(c=='A') {
				ans = new Answer(aJCas);
				ans.setBegin(mlines.start() + 4);
				ans.setIsCorrect(text.charAt(mlines.start() + 2)=='1');
				ans.setEnd(mlines.end());
				ans.setCasProcessorId(this.annotatorName);
				ans.setConfidence(1.0); //TODO: calcular
				ans.addToIndexes();
			}
		}
		System.out.println("Leaving SentenceAnnotator");
	}

}
