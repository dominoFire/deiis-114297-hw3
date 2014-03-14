package edu.cmu.deiis.hw2.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;


public class TokenAnnotator extends JCasAnnotator_ImplBase {
	Pattern punctPattern = Pattern.compile("[\\., \\?\\!']+");
	final String annotatorName = "TokenAnnotator";
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.println("Entering " +annotatorName);
		AnnotationIndex<Annotation> aiq = aJCas.getAnnotationIndex(Question.type);
		AnnotationIndex<Annotation> aia = aJCas.getAnnotationIndex(Answer.type);
		String text = aJCas.getDocumentText();
		this.iterateAnnotations(aiq, text, aJCas);
		this.iterateAnnotations(aia, text, aJCas);
		System.out.println("Leaving " +annotatorName);
	}
	
	private void iterateAnnotations(AnnotationIndex<Annotation> aiq, String text, JCas aJCas) {
		FSIterator<Annotation> it1 = aiq.iterator();
		Annotation an;
		while(it1.hasNext()) {
			an = it1.next();
			//this.createTokens(an, text, aJCas);
			this.createTokens2(an, text, aJCas);			
		}
	}
	
	//Silly but effective
	private void createTokens2(Annotation a, String text, JCas jcas)
	{
		int ini = a.getBegin();
		int fin = a.getEnd();
		String sentence = text.substring(ini, fin);
		String[] words = sentence.split(punctPattern.pattern());
		int i=0, j;
		Token tok;
		for(String w: words) {
			System.out.print(w + " ");
			i = sentence.indexOf(w, i);
			j = i + w.length();
			tok = new Token(jcas);
			tok.setBegin(i + ini);
			tok.setEnd(j + ini);
			tok.setCasProcessorId(this.annotatorName);
			tok.setConfidence(1.0);
			tok.addToIndexes();
		}
		System.out.println();
	}

	//efficient but incorrect
	private void createTokens(Annotation a, String text, JCas jcas) {
		int ini = a.getBegin();
		int fin = a.getEnd();
		String sentence = text.substring(ini, fin);
		Matcher m = punctPattern.matcher(sentence);
		Token tok;
		int ti=0, tf=0;
		int pi=0, pf;
		while(m.find()) {
			ti = m.start();
			tf = m.end();
			if(ti>0) {
				pf = ti-1;
				tok = new Token(jcas);
				tok.setBegin(pi);
				tok.setEnd(pf);
				tok.setCasProcessorId(this.annotatorName);
				tok.setConfidence(1.0);
				tok.addToIndexes();
				pi = tf + 1;
			}
		}
	}
}	
