package edu.cmu.deiis.hw3.consumers;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceProcessException;

import edu.cmu.deiis.types.AnswerScore;

public class EvaluatorX extends CasConsumer_ImplBase {

	@Override
	public void processCas(CAS aCAS) throws ResourceProcessException {
		// TODO Auto-generated method stub
		JCas aJCas = null;
		try {
			aJCas = aCAS.getJCas();
		} catch (CASException ex) {
			System.err.println("I couldn't get the jCas");
			return;
		}
		AnnotationIndex<Annotation> ais = aJCas.getAnnotationIndex(AnswerScore.type);
		FSIterator<Annotation> it = ais.iterator();
		AnswerScore as = null;
		while(it.hasNext()) {
			as = (AnswerScore)it.next();
			System.out.println("AnswerScore: " +as.getScore());
		}
	}
}
