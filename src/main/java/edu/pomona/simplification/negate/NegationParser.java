package edu.pomona.simplification.negate;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class NegationParser {
	private Annotator morphAnnotator = new MorphNegationAnnotator();
	private Annotator sentAnnotator = new SententialNegationAnnotator();
	private Annotator doubleAnnotator = new DoubleNegationAnnotator();

	private StanfordCoreNLP pipeline;

	public NegationParser() {
		// creates a StanfordCoreNLP object, with POS tagging, parsing
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, parse");
		pipeline = new StanfordCoreNLP(props);
	}

	public void annotate(String text){
		// Parse out the sentences
		Annotation antdLine = new Annotation(text);

		// run the selected Stanford Annotators on the text
		pipeline.annotate(antdLine);

		List<CoreMap> coreSents = antdLine
				.get(CoreAnnotations.SentencesAnnotation.class);

		// Declare a Sentence Object for each Sentence
		for(CoreMap coreSent : coreSents) {
			Sentence s = new Sentence(coreSent);

			// Do the annotations
			morphAnnotator.annotate(s);
			sentAnnotator.annotate(s);
			doubleAnnotator.annotate(s);
		}
	}
}
