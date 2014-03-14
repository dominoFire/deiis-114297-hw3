package edu.cmu.deiis.hw2.annotators;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.*;

import java.util.*;

public class EvaluatorAnnotator 
		extends JCasAnnotator_ImplBase 
{
	Integer n = null;

	@Override
	public void initialize(UimaContext uimaContext)
			throws ResourceInitializationException
	{
		super.initialize(uimaContext);
		this.n = (Integer)uimaContext.getConfigParameterValue("N");
	}
	
	@Override
	public void process(JCas aJCas)
			throws AnalysisEngineProcessException 
	{
		AnnotationIndex<Annotation> ais = aJCas.getAnnotationIndex(AnswerScore.type);
		FSIterator<Annotation> it = ais.iterator();
		ArrayList<AnswerScore> top_n_scores = new ArrayList<AnswerScore>();
		AnswerScore as = null;
		int out = 0;
		while(it.hasNext()) {
			as = (AnswerScore)it.next();
			top_n_scores.add(as);
			out++;
		}
		
		if(out<this.n)
			System.out.println("Warning. Not enough answers to calculate precision@N");
		else {
			//si fueran muchas preguntas, deberia pensar otra forma de ordenarlas
			Collections.sort(top_n_scores, new Comparator<AnswerScore>() {
				@Override
				public int compare(AnswerScore o1, AnswerScore o2) {
					int res = 0;
					if(Math.abs(o1.getScore() - o2.getScore()) <= 0.00001)
						res = 0;
					if(o1.getScore() < o2.getScore())
						res = -1;
					else
						res = 1;
					return res;
				}
			});
			
			int good = 0;
			for(int i=0; i<this.n; i++) 
				if(top_n_scores.get(i).getAnswer().getIsCorrect())
					good++;
			System.out.printf("Precition@(N=%d): %.3f\n", this.n, (double)good/this.n);
		}
	}

}
