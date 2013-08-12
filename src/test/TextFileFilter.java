package test;

import java.io.File;

import javax.swing.filechooser.FileFilter;

class TextFileFilter extends FileFilter {
	
	public boolean accept( File f ) {
		
		boolean retVal = false;
		
		String ext = f.getName().substring( f.getName().length() - 3 );
		
		if ( ext.equalsIgnoreCase( "txt" ) || f.isDirectory() )
			retVal = true;

		return retVal;
	}
	
	public String getDescription() {
		
		return "Text Files ( *.txt )";
	}
} 