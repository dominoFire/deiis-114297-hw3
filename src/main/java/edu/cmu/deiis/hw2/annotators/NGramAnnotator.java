package edu.cmu.deiis.hw2.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;

import java.util.*;

public class NGramAnnotator extends JCasAnnotator_ImplBase {
	Pattern newLinePattern = Pattern.compile("\\n");
	final String annotatorName = "NGramAnnotator";
	@Override
	
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		System.out.println("Entering NGramAnnotator");
		AnnotationIndex<Annotation> aiq = aJCas.getAnnotationIndex(Token.type);
		FSIterator<Annotation> it = aiq.iterator();
		String text = aJCas.getDocumentText();
		Matcher mat = newLinePattern.matcher(text);
		NGram ngram;
		Annotation ann;
		FSArray fs_arr;
		ArrayList<Annotation> arr = new ArrayList<Annotation>();
		int end;
		int ant = 0;
		boolean finalized;
		while(mat.find()) {
			System.out.printf("An enter [%d,%d] = (%c, %c)\n", mat.start(), mat.end(), 
				text.charAt(mat.start())=='\n'? '%': text.charAt(mat.start()),
				text.charAt(mat.end()-1)=='\n'? '%': text.charAt(mat.end()-1));
			end = mat.start();
			finalized = false;
			while(it.hasNext() && !finalized) {
				ann = it.next();
				if(ann.getEnd() <= end) {
					arr.add(ann);
					System.out.printf("An annot [%d, %d]\n", ann.getBegin(), ann.getEnd());
				} else {
					for(int i=0; i<arr.size(); i++) {
						fs_arr = new FSArray(aJCas, 1);
						fs_arr.set(0, arr.get(i));
						ngram = buildNGram(fs_arr, aJCas, text, ant, 1);
						ngram.addToIndexes();
					}
					for(int i=0; i<arr.size(); i++) {
						if((i+1)<arr.size()) {
							fs_arr = new FSArray(aJCas, 2);
							fs_arr.set(0, arr.get(i));
							fs_arr.set(1, arr.get(i+1));
							ngram = buildNGram(fs_arr, aJCas, text, ant, 2);
							ngram.addToIndexes();
						}
					}
					for(int i=0; i<arr.size(); i++) {
						if((i+1)<arr.size() && (i+2)<arr.size()) {
							fs_arr = new FSArray(aJCas, 3);
							fs_arr.set(0, arr.get(i));
							fs_arr.set(1, arr.get(i+1));
							fs_arr.set(2, arr.get(i+2));
							ngram = buildNGram(fs_arr, aJCas, text, ant, 3);
							ngram.addToIndexes();
						}
					}
					arr.clear();
					arr.add(ann);
					ant = end + 1;
					finalized = true;
				}
			}
		}
		while(it.hasNext()) {
			arr.add(it.next());
		}
		if(!arr.isEmpty()) {
			for(int i=0; i<arr.size(); i++) {
				fs_arr = new FSArray(aJCas, 1);
				fs_arr.set(0, arr.get(i));
				ngram = buildNGram(fs_arr, aJCas, text, ant, 1);
				ngram.addToIndexes();
			}
			for(int i=0; i<arr.size(); i++) {
				if((i+1)<arr.size()) {
					fs_arr = new FSArray(aJCas, 2);
					fs_arr.set(0, arr.get(i));
					fs_arr.set(1, arr.get(i+1));
					ngram = buildNGram(fs_arr, aJCas, text, ant, 2);
					ngram.addToIndexes();
				}
			}
			for(int i=0; i<arr.size(); i++) {
				if((i+1)<arr.size() && (i+2)<arr.size()) {
					fs_arr = new FSArray(aJCas, 3);
					fs_arr.set(0, arr.get(i));
					fs_arr.set(1, arr.get(i+1));
					fs_arr.set(2, arr.get(i+2));
					ngram = buildNGram(fs_arr, aJCas, text, ant, 3);
					ngram.addToIndexes();
				}
			}
		}
		System.out.println("Leaving NGramAnnotator");
	}	
	
	private NGram buildNGram(FSArray arr, JCas aJCas, String text, int ant, int size) {
		NGram ngram = new NGram(aJCas);
		ngram.setElements(arr);
		ngram.setConfidence(1.0);
		ngram.setCasProcessorId(this.annotatorName);
		if(text.charAt(ant)=='Q') {
			ngram.setElementType("Question");
		} else if(text.charAt(ant)=='A') {
			ngram.setElementType("Answer");
		} else {
			System.err.println("I dunno!: " +text.charAt(ant));
		}
		ngram.setBegin( ((Annotation)arr.get( 0 )).getBegin() );
		ngram.setEnd( ((Annotation)arr.get( size - 1 )).getEnd() );
		return ngram;
	}
}

