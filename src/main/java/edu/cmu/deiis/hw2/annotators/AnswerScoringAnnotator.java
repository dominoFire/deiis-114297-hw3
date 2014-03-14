package edu.cmu.deiis.hw2.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;

import java.util.*;


public class AnswerScoringAnnotator
	extends JCasAnnotator_ImplBase 
{

	@Override
	//asumiremos una pregunta y varias respuestas por archivo
	public void process(JCas aJCas)
			throws AnalysisEngineProcessException 
	{
		System.out.println("Entering AnswerScoringAnnotator");
		AnnotationIndex<Annotation> aia = aJCas.getAnnotationIndex(Answer.type);
		AnnotationIndex<Annotation> ain = aJCas.getAnnotationIndex(NGram.type);
		FSIterator<Annotation> ait = aia.iterator();
		FSIterator<Annotation> nit = ain.iterator();
		NGram ngram = null;
		//vemos de donde vienen los ngrams
		ArrayList<NGram> question_ngrams = new ArrayList<NGram>();
		ArrayList<NGram> answer_ngrams = new ArrayList<NGram>();
		while(nit.hasNext()) {
			ngram = (NGram)nit.next();
			if(ngram.getElementType().equals("Question")) {
				question_ngrams.add(ngram);
			} else if(ngram.getElementType().equals("Answer")) {
				answer_ngrams.add(ngram);
			} else {
				System.err.println("A weird NGram appeared");
			}
		}
		//luego, ver a que respuesta pertenecen
		Answer ans = null;
		ArrayList<NGram> curr_ans_ngrams = new ArrayList<NGram>();
		int i = 0;
		double score = 0;
		AnswerScore ans_score = null;
		while(ait.hasNext()) {
			ans = (Answer)ait.next();
			curr_ans_ngrams.clear();
			while(i<answer_ngrams.size() && inAnnotation(ans, answer_ngrams.get(i))) {
				curr_ans_ngrams.add(answer_ngrams.get(i));
				i++;
			}
			score = scoreQuestionAnswer(question_ngrams, curr_ans_ngrams, aJCas.getDocumentText());
			//creamos la anotacion del score
			ans_score = new AnswerScore(aJCas);
			ans_score.setBegin(ans.getBegin());
			ans_score.setEnd(ans.getEnd());
			ans_score.setAnswer(ans);
			ans_score.setScore(score);
			ans_score.addToIndexes();
			ans_score.setConfidence(1.0);
			ans_score.setCasProcessorId("AnswerScoringAnnotator");
		}
		System.out.println("Leaving AnswerScoringAnnotator");
	}
	
	private double scoreQuestionAnswer(ArrayList<NGram> question_ngrams, ArrayList<NGram> answer_ngrams, String text)
	{
		HashSet<String> question_tokens = new HashSet<String>();
		HashSet<String> answer_tokens = new HashSet<String>();
		
		for(NGram n : question_ngrams)
			for(int i=0; i<n.getElements().size(); i++)
				question_tokens.add(
						text.substring(
								n.getElements(i).getBegin(), 
								n.getElements(i).getEnd()
						) );

		for(NGram n : answer_ngrams)
			for(int i=0; i<n.getElements().size(); i++)
				answer_tokens.add(
						text.substring(
								n.getElements(i).getBegin(), 
								n.getElements(i).getEnd()
						) );
		
		//indice de jacard con los tokens
		HashSet<String> all_tokens = new HashSet<String>();
		all_tokens.addAll(question_tokens);
		all_tokens.addAll(answer_tokens);
		
		int intersect = 0;
		for(String qtok: answer_tokens) {
			System.out.print(qtok + " ");
			if(question_tokens.contains(qtok))
				intersect++;
		}
		System.out.printf("; Score: %d/%d\n", intersect, all_tokens.size());
		return (double)intersect/all_tokens.size();
	}
	
	/**
	 * Determines whether the big annotation contains the little annotation
	 * @param big
	 * @param little
	 * @return
	 */
	private boolean inAnnotation(Annotation big, Annotation little)
	{
		return big.getBegin() <= little.getBegin() && little.getEnd() <= big.getEnd();
	}
}
