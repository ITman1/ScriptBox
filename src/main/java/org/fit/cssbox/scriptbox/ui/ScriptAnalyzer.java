/**
 * ScriptAnalyzer.java
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

package org.fit.cssbox.scriptbox.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URL;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.Viewport;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.swingbox.util.DefaultAnalyzer;

public class ScriptAnalyzer extends DefaultAnalyzer {
	BrowsingContext context;
	
	public ScriptAnalyzer(BrowsingContext context) {
		this.context = context;
	}
	
    @Override
    public Viewport analyze(DocumentSource d, Dimension dim)
            throws Exception
    {
    	w3cdoc = context.getActiveDocument();
    	URL address = ((Html5DocumentImpl)w3cdoc).getAddress();

        // Create the CSS analyzer
        DOMAnalyzer da = new DOMAnalyzer(w3cdoc, address);
        da.attributesToStyles();
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.getStyleSheets();
        
        BufferedImage tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        canvas = new BrowserCanvas(da.getRoot(), da, address);
        canvas.setImage(tmpImg);
        canvas.getConfig().setLoadImages(true);
        canvas.getConfig().setLoadBackgroundImages(true);
        canvas.createLayout(dim);

        return canvas.getViewport();
    }
}
