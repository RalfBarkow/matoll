package de.citec.sc.matoll.io;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import de.citec.sc.matoll.core.Language;

import de.citec.sc.matoll.core.LexicalEntry;
import de.citec.sc.matoll.core.Lexicon;
import de.citec.sc.matoll.core.Provenance;
import de.citec.sc.matoll.core.Reference;
import de.citec.sc.matoll.core.Restriction;
import de.citec.sc.matoll.core.Sense;
import de.citec.sc.matoll.core.SenseArgument;
import de.citec.sc.matoll.core.SimpleReference;
import de.citec.sc.matoll.core.SyntacticArgument;
import de.citec.sc.matoll.core.SyntacticBehaviour;
import de.citec.sc.matoll.vocabularies.LEMON;
import de.citec.sc.matoll.vocabularies.LEXINFO;
import de.citec.sc.matoll.vocabularies.OWL;
import de.citec.sc.matoll.vocabularies.PROVO;


public class LexiconLoader {

	public  LexiconLoader()
	{
		
	}
	
	public Lexicon loadFromFile(String file)
	{
		
		Model model = RDFDataMgr.loadModel(file);
		
		 Statement stmt;
		 Resource subject;
		 
		 Lexicon lexicon = new Lexicon();
		 
		 LexicalEntry entry;
		 
		 StmtIterator iter = model.listStatements(null, LEMON.canonicalForm, (RDFNode) null);
		 
		 while (iter.hasNext()) {
			 
			 stmt = iter.next();
			 
			 subject = stmt.getSubject();
			 
			 // //System.out.println("Processing entry "+subject.toString());

			 List<SyntacticBehaviour> behaviours = getSyntacticArguments(subject,model);
			 
			 List<Sense> senses = getSenseArguments(subject,model);
			 
			 // //System.out.println(behaviours.size()+" synargs extracted");
			 
			 // //System.out.println(senses.size()+" senses extracted");
			 
                         //System.out.println("Confidence:"+getConfidence(subject,model));
			 
			 HashMap<String,String> map;
                         
                         Language language = getLanguage(subject,model);
			 
                         
			 if (behaviours.size() > 0)
			 {      entry = new LexicalEntry(language);
				for (SyntacticBehaviour behaviour: behaviours)
				 {
					 for (Sense sense: senses)
					 { 
						 //entry = new LexicalEntry();
                                                 
                                                 Provenance provenance = new Provenance();
                                                 provenance.setConfidence(Double.valueOf(getConfidence(subject,model)));
                                                 provenance.setFrequency(getFrequency(subject,model));
                                                 System.err.println("Adapt to new provenance style");
						 //entry.setProvenance(provenance);
                                                 
						 entry.setURI(subject.toString());
					 
						 entry.setCanonicalForm(getCanonicalForm(subject,model));
					 			 
						 entry.setPOS(getPOS(subject,model));
					 
                                                 System.err.println("Adapt to new sense/bahaviour style");
						 //entry.addSyntacticBehaviour(behaviour);
						 
						 map = entry.computeMappings(sense);
						 
						 if (map.keySet().size() > 0)
						 {
							 //entry.addSense(sense);
							 //entry.setMappings(map);
							 //lexicon.addEntry(entry);
						 }
					 }
				 }
                                if(entry.getPOS()!=null && entry.getReferences().size()>0){
                                    boolean add_entry = true;
                                    for(Reference ref : entry.getReferences()){
                                        try{
                                            if(ref.getURI()==null)add_entry = false;
                                        }
                                        catch (Exception ex){ add_entry = false;};
                                        
                                    }
                                    if(add_entry)lexicon.addEntry(entry);
                                }
			 }
				 
			else
			{
				
				 entry = new LexicalEntry(language);
				 
				 entry.setURI(subject.toString());
			 
				 entry.setCanonicalForm(getCanonicalForm(subject,model));
			 				 
				 entry.setPOS(getPOS(subject,model));
				 
				 for (Sense sense: senses)
				 {
					 //entry.addSense(sense);
				 }
                                 if(entry.getPOS()!=null && entry.getReferences().size()>0){
                                    boolean add_entry = true;
                                    for(Reference ref : entry.getReferences()){
                                        try{
                                            if(ref.getURI()==null)add_entry = false;
                                        }
                                        catch (Exception ex){ add_entry = false;};
                                        
                                    }
                                    if(add_entry)lexicon.addEntry(entry);
                                }
			 				
			}
				 
				 
		 }
			 
		 
		 return lexicon;
		 
	}
	
	
	private String getPOS(Resource subject, Model model) {
		
		Resource pos;
		
		Statement stmt;
		
		stmt = subject.getProperty(LEXINFO.partOfSpeech);
		
		if (stmt != null)
		{
			pos = (Resource) stmt.getObject();
			
			return pos.toString();
		}
		else
		{
			return null;
		}
		
	}

	private List<Sense> getSenseArguments(Resource subject, Model model) {
		
		List<Sense> senses = new ArrayList<Sense>();
		
		Sense sen;
		
		Resource sense;
		
		Resource object;
		
		Statement stmt;
				
		Set<SenseArgument> senseArguments = new HashSet<SenseArgument>();
		
		Statement senseArg;
		
		StmtIterator iter = model.listStatements(subject, LEMON.sense, (RDFNode) null);
		 
		 while (iter.hasNext()) {
		
			 stmt = iter.next();
			 
			 sense = (Resource) stmt.getObject();
			 					
			 sen = new Sense();
			
			 senseArguments = new HashSet<SenseArgument>();
			

	 		StmtIterator it = sense.listProperties(LEMON.isA);
		    while( it.hasNext() ) {
		    
		    	senseArg = it.next();
		    	
		    	object = (Resource) senseArg.getObject();
		    	
		    	senseArguments.add(new SenseArgument(senseArg.getPredicate().toString(),object.toString()));
		    }	
		    	
	    	it = sense.listProperties(LEMON.subjOfProp);
		    while( it.hasNext() ) {
		    
		    	senseArg = it.next();
		    	
		    	object = (Resource) senseArg.getObject();
		    	
		    	senseArguments.add(new SenseArgument(senseArg.getPredicate().toString(),object.toString()));
		    	
		    }
		    
		   
	    	it = sense.listProperties(LEMON.objOfProp);
		    while( it.hasNext() ) {
		    
		    	senseArg = it.next();
		    	
		    	object = (Resource) senseArg.getObject();
		    	
		  
		    	senseArguments.add(new SenseArgument(senseArg.getPredicate().toString(),object.toString()));	

		    }
		   
		    sen.setSenseArgs(senseArguments);
                    
                    Resource reference = getReference(sense,model);
                    
                    if (reference != null) {
                        
                        // check whether it's a restriction class
                        String property = getPropertyObject(reference,OWL.onProperty);
                        String value    = getPropertyObject(reference,OWL.hasValue);
                        
                        if (property != null && value != null) {
                            sen.setReference(new Restriction(reference.toString(),property,value));
                        }
                        else {
                            sen.setReference(new SimpleReference(reference.toString()));
                        }
                    }
                   
		    senses.add(sen);
		    	
		}
	
		return senses;	
	}

	private static List<SyntacticBehaviour> getSyntacticArguments(Resource subject, Model model) {
		
		Resource synBehaviour;
		
		Resource object;
		
		Resource prepositionEntry;
		
		Statement prepStatement;
		
		String preposition;
		
		Statement synArg;
				
		List<SyntacticBehaviour> behaviours = new ArrayList<SyntacticBehaviour>();
		
		SyntacticBehaviour behaviour;
		
		Property predicate;

		Statement stmt;
		
		StmtIterator iter = model.listStatements(subject, LEMON.syntacticBehaviour, (RDFNode) null);
		 
		while (iter.hasNext()) {
		
			 stmt = iter.next();
		
			 behaviour = new SyntacticBehaviour();
                         
                         synBehaviour = (Resource) stmt.getObject();
			 
			 StmtIterator it = model.listStatements(synBehaviour, null, (RDFNode) null); 
					 
			 while( it.hasNext() ) {
		    	
				synArg = it.next();
		    	
                                object = (Resource) synArg.getObject();
                               
                                
			 	predicate = synArg.getPredicate();
			 	
                                prepStatement = object.getProperty(LEMON.marker);
                                
			 	
			 	preposition = null;
			 
			 	if (prepStatement != null)
			 	{
	
			 		prepositionEntry = (Resource) prepStatement.getObject();
			 	
			 		if (prepositionEntry != null)
			 		{
			 			preposition = getCanonicalForm(prepositionEntry,model);
		    		
			 			// //System.out.print("Preposition: "+preposition+"\n");
		    		
			 		}
			 		else
			 		{
			 			preposition = null;
			 		}
			 	}
			 		
			 	if (!predicate.toString().equals(RDF.type.toString())){
                                    behaviour.add(new SyntacticArgument(predicate.toString(),object.toString(),preposition));
                                }
                                    
			 	
                                
			 	
		    }	
		    	
	    
	    	behaviour.setFrame(getFrame(synBehaviour,model));
	    	
	    	behaviours.add(behaviour);
		   
		}
	
		   
		return behaviours;
	}
	

	private static Resource getReference(Resource sense, Model model) {

            Resource uri; 
            
            try {
                uri = (Resource) sense.getProperty(LEMON.reference).getObject();
                
                if (uri.isAnon()) {
                   return null;
                } 
                else {
                   return uri;
                }
            } 
            catch (Exception e) {
                return null;
            }
        }
            
        private static String getPropertyObject(Resource r, Property p) {
            
            try {
                return r.getProperty(p).getObject().toString();
            }
            catch (Exception e) {
                return null;
            }
        }
	

	private static String getFrame(Resource syntacticBehaviour, Model model) {
				
		String value = null;
		
		Statement stmt;
		
                StmtIterator it = syntacticBehaviour.listProperties(RDF.type);
                while( it.hasNext() ) {

                    stmt = it.next();

                    value = stmt.getObject().toString();

                    if (!value.equals("http://lemon-model.net/lemon#Frame"))
                    {
                            // //System.out.print(value+"\n");
                            return value;

                    }
                }

		return value;
	}

	private static String getCanonicalForm(Resource subject, Model model) {
		
		Resource canonicalForm;
		
		Statement stmt;
		
		Literal form;
		

		stmt = subject.getProperty(LEMON.canonicalForm);
		
		if (stmt != null)
		{
			canonicalForm = (Resource) stmt.getObject();
			
			if (canonicalForm != null)
			{
				stmt = canonicalForm.getProperty(LEMON.writtenRep);
				
				if (stmt != null)
				{
				form = (Literal) canonicalForm.getProperty(LEMON.writtenRep).getObject();
					return form.toString();
				}
				else
				{
					return null;
				}
				
			}
			else
			{
				return null;
			}
		}
		else
		{
			// //System.out.print("Entry "+subject+" has no canonical form!!!\n");
			return null;
		}		
	}
        
        private static double getConfidence(Resource subject, Model model) {
		
		Resource prov_activity;
		
		Statement stmt;
		
		Literal form;
		
                /*
                if no confidence is given in .ttl file return 0.0
                */
                double return_vale = 0.0;

		stmt = subject.getProperty(PROVO.generatedBy);
		
		if (stmt != null)
		{
			prov_activity = (Resource) stmt.getObject();
			
			if (prov_activity != null)
			{
				stmt = prov_activity.getProperty(PROVO.confidence);
				
				if (stmt != null)
				{
				return prov_activity.getProperty(PROVO.confidence).getDouble();
				}
				else
				{
					return return_vale;
				}
				
			}
			else
			{
				return return_vale;
			}
		}
		else
		{
			// //System.out.print("Entry "+subject+" has no canonical form!!!\n");
			return return_vale;
		}
	}
        
        private static int getFrequency(Resource subject, Model model) {
		
		Resource prov_activity;
		
		Statement stmt;
		
		Literal form;
		
                /*
                if no confidence is given in .ttl file return 0.0
                */
                int return_vale = 0;

		stmt = subject.getProperty(PROVO.generatedBy);
		
		if (stmt != null)
		{
			prov_activity = (Resource) stmt.getObject();
			
			if (prov_activity != null)
			{
				stmt = prov_activity.getProperty(PROVO.frequency);
				
				if (stmt != null)
				{
				return prov_activity.getProperty(PROVO.frequency).getInt();
					//return form.toString();
				}
				else
				{
					return return_vale;
				}
				
			}
			else
			{
				return return_vale;
			}
		}
		else
		{
			// //System.out.print("Entry "+subject+" has no canonical form!!!\n");
			return return_vale;
		}
	}

    private Language getLanguage(Resource subject, Model model) {
        /*
        TODO: implement function;
        return as default EN
        */
        return Language.EN;
    }
        
        
        
}

