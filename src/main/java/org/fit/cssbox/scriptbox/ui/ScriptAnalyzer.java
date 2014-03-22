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
