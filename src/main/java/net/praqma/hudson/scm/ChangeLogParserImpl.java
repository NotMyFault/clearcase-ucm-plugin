package net.praqma.hudson.scm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.umd.cs.findbugs.annotations.*;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.util.Digester2;
import java.util.logging.Level;

/**
 * 
 * @author Troels Selch
 * @author Margit Bennetzen
 * 
 */
@SuppressFBWarnings("")
public class ChangeLogParserImpl extends ChangeLogParser {

	protected static final Logger logger = Logger.getLogger( ChangeLogParserImpl.class.getName() );

	@Override
	public ChangeLogSet<? extends Entry> parse( AbstractBuild build, File changelogFile ) throws IOException, SAXException {
		List<ChangeLogEntryImpl> entries = new ArrayList<ChangeLogEntryImpl>();
		Digester digester = new Digester2();
		digester.push( entries );
		digester.addObjectCreate( "*/entry/activity", ChangeLogEntryImpl.class );
		digester.addSetProperties( "*/entry/activity" );
		digester.addBeanPropertySetter( "*/entry/activity/file", "nextFilepath" );
		digester.addBeanPropertySetter( "*/entry/activity/actName" );
		digester.addBeanPropertySetter( "*/entry/activity/actHeadline" );
		digester.addBeanPropertySetter( "*/entry/activity/author", "myAuthor" );
		digester.addSetNext( "*/entry/activity", "add" );
		FileReader reader = new FileReader( changelogFile );
        
		try {
			digester.parse( reader );
		} catch( Exception e ) {
			logger.log(Level.SEVERE, "Unable to parse change log", e);
		} finally {
			reader.close();
		}
			
		return new ChangeLogSetImpl( build, entries );
	}

}
