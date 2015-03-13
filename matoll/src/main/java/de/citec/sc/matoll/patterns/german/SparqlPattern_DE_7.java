package de.citec.sc.matoll.patterns.german;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.citec.sc.matoll.core.LexiconWithFeatures;
import de.citec.sc.matoll.patterns.SparqlPattern;

public class SparqlPattern_DE_7 extends SparqlPattern{

	
	Logger logger = LogManager.getLogger(SparqlPattern_DE_7.class.getName());
	
	/*
	 * 
	PropSubj:Tom Hayden
	PropObj:Barbara Williams
	sentence:Barbara Williams ist verheiratet mit dem sozialpolitischen Aktivisten Tom Hayden , mit dem sie gemeinsam in Los Angeles lebt . 
	1	Barbara	Barbara	N	NE	_|Nom|Sg	3	subj	_	_ 
	2	Williams	Williams	N	NE	_|Nom|Sg	1	app	_	_ 
	3	ist	sein	V	VAFIN	3|Sg|Pres|Ind	0	root	_	_ 
	4	verheiratet	verheiratet	ADV	ADJD	Pos|_	3	pred	_	_ 
	5	mit	mit	PREP	APPR	Dat	3	pp	_	_ 
	6	dem	das	ART	ART	Def|Masc|Dat|Sg	8	det	_	_ 
	7	sozialpolitischen	sozialpolitisch	ADJA	ADJA	Pos|Masc|Dat|Sg|_|	8	attr	_	_ 
	8	Aktivisten	Aktivist	N	NN	Masc|Dat|Sg	5	pn	_	_ 
	9	Tom	Tom	N	NE	Masc|Dat|Sg	8	app	_	_ 
	10	Hayden	Hayden	N	NE	Masc|Dat|Sg	9	app	_	_ 
	11	,	,	$,	$,	_	0	root	_	_ 
	12	mit	mit	PREP	APPR	Dat	19	pp	_	_ 
	13	dem	das	PRO	PRELS	Masc|Dat|Sg	12	pn	_	_ 
	14	sie	sie	PRO	PPER	3|Sg|Fem|Nom	19	subj	_	_ 
	15	gemeinsam	gemeinsam	ADV	ADJD	Pos|	16	adv	_	_ 
	16	in	in	PREP	APPR	_	19	pp	_	_ 
	17	Los	Los	N	NN	Neut|_|Sg	16	pn	_	_ 
	18	Angeles	Angeles	N	NE	Neut|_|Sg	17	app	_	_ 
	19	lebt	leben	V	VVFIN	3|Sg|Pres|Ind	10	rel	_	_ 
	20	.	.	$.	$.	_	0	root	_	_ 	
	----------------------
	 */
			String query = "SELECT ?class ?lemma_pos ?dobj_lemma ?lemma_grammar ?advmod_lemma ?lemma ?e1 ?e2 ?e1_form ?e2_form ?e1_grammar ?e2_grammar ?prep ?propSubj ?propObj ?lemma_addition WHERE"
					+ "{ "
					+ "?e1 <conll:form> ?e1_form . "
					+ "?e1 <conll:deprel> ?e1_grammar . "
					+ "FILTER regex(?e1_grammar, \"subj\") ."
					+ "?e1 <conll:cpostag> ?e1_pos . "
					+ "?e1 <conll:head> ?verb . "
					+ "?verb <conll:cpostag> \"V\" . "
					+ "?y <conll:head> ?verb . "
					+ "?y <conll:postag> ?lemma_pos . "
					+ "FILTER regex(?lemma_pos, \"ADJ\") ."
					+ "?y <conll:deprel> ?lemma_grammar . "
					+ "?y <conll:lemma> ?lemma . "
					+ "?p <conll:head> ?verb . "
					+ "?p <conll:deprel> \"pp\" . "
					+ "?p <conll:form> ?prep . "
					+ "?e2 <conll:head> ?p . "
					+ "?e2 <conll:deprel> ?e2_grammar . "
					+ "FILTER( regex(?e2_grammar, \"obj\") || regex(?e2_grammar, \"gmod\") || regex(?e2_grammar, \"pn\"))"
					//+ "{?e2 <conll:deprel> \"pn\" . }"
					//+ "UNION"
					//+ "FILTER regex(?e2_grammar, \"obj\") ."
					+ "?e2 <conll:form> ?e2_form . "
					+ "?y <own:partOf> ?class. "
					+ "?class <own:subj> ?propSubj. "
					+ "?class <own:obj> ?propObj. "
					+ "}";
			
	
	
	
	@Override
	public String getID() {
		return "SPARQLPattern_DE_7";
	}

	@Override
	public void extractLexicalEntries(Model model, LexiconWithFeatures lexicon) {
		// TODO Auto-generated method stub
		
	}

}
