package org.reldb.relui.dbui.html;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserNative implements HtmlBrowser {

	private Browser browser;
	private Style style;
	
	private static String cleanForJavascriptInsertion(String s) {
		return s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "");
	}
	
	@Override
	public boolean createWidget(Composite parent, Font font) {
		style = new Style(font, -3);
		try {
			browser = new Browser(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			browser.setJavascriptEnabled(true);
		} catch (Throwable t) {
			return false;
		}
		clear();
		return true;
	}
	
	@Override
	public void clear() {
		browser.setText(style.getEmptyHTMLDocument());
	}
	
	@Override
	public void appendHtml(String s) {
		browser.execute(String.format("document.write('%s');", cleanForJavascriptInsertion(style.getHTMLDocument(s))));
	}

	@Override
	public void scrollToBottom() {
		browser.execute("window.scrollTo(0, document.body.scrollHeight)");
	}

	@Override
	public Control getWidget() {
		return browser;
	}

}