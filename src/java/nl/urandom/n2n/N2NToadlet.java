package nl.urandom.n2n;

import java.io.IOException;
import java.net.URI;

import freenet.clients.http.*;
import freenet.pluginmanager.FredPluginL10n;
import freenet.support.HTMLNode;
import freenet.support.api.HTTPRequest;
import freenet.support.plugins.helpers1.PluginContext;
import freenet.support.plugins.helpers1.WebInterfaceToadlet;

/**
 * FIXME Javadoc
 *
 * @author bertm
 */
public class N2NToadlet extends WebInterfaceToadlet {
    private final FredPluginL10n l10n;
    private final N2NHandler n2n;

    protected N2NToadlet(PluginContext pluginContext, FredPluginL10n l10n, N2NHandler n2n) {
        super(pluginContext, N2N.PLUGIN_ROOT, "");
        this.l10n = l10n;
        this.n2n = n2n;
    }

    @Override
    public void handleMethodGET(URI uri, HTTPRequest httpRequest, ToadletContext toadletContext)
            throws ToadletContextClosedException, IOException, RedirectException {
        PageNode page =
                pluginContext.pageMaker.getPageNode(_("Page.N2N.Title"), toadletContext);
        HTMLNode outer = page.outer;
        HTMLNode contentNode = page.content;
        InfoboxNode box = pluginContext.pageMaker.getInfobox(_("Infobox.N2N.Threads.Title"));
        HTMLNode content = box.content;

        addP(content, n2n.isInstalled() ? "N2N handler is installed" : "N2N installer NOT installed");

        for (String message : n2n.getMessages()) {
            addP(content, message);
        }

        contentNode.addChild(box.outer);
        writeHTMLReply(toadletContext, 200, "OK", outer.generate()); //NON-NLS
    }
    
    private void addP(HTMLNode content, String text) {
        HTMLNode p = new HTMLNode("p"); //NON-NLS
        p.addChild("#", text);
        content.addChild(p);
    }

    private String _(String key) {
        return l10n.getString(key);
    }
}
