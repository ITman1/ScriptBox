/**
 * EventDOMSource.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.deprecied.events;

import java.io.IOException;

import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventListener;
import org.xml.sax.SAXException;

public class EventDOMSource extends DOMSource
{
	private EventDOMParser _parser;
	
    public EventDOMSource(DocumentSource src)
    {
        super(src);
    }

	public void addDocumentEventListener(String type, EventListener listener, boolean useCapture) {
		getParser().addDocumentEventListener(type, listener, useCapture);
	}
	
	public void removeDocumentEventListener(EventListener listener) {
		getParser().removeDocumentEventListener(listener);
	}
	
    public void reset() {
    	getParser().reset();
    }
    
    @Override
    public Document parse() throws SAXException, IOException
    {        
    	EventDOMParser parser = getParser();
    	
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        
        if (charset != null) {
        	parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset);
        }
        		
        parser.parse(new org.xml.sax.InputSource(getDocumentSource().getInputStream()));
        
        return parser.getDocument();
    }
    
    protected EventDOMParser getParser() {
    	if (_parser == null) {
            _parser = instantizeEventDOMParser();
    	}
    	
    	return _parser;
    }
    
    protected EventDOMParser instantizeEventDOMParser() {
    	return new EventDOMParser();
    }

}
