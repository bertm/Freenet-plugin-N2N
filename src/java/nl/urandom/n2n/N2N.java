package nl.urandom.n2n;

import freenet.l10n.BaseL10n.LANGUAGE;
import freenet.l10n.PluginL10n;
import freenet.pluginmanager.*;
import freenet.support.api.HTTPRequest;
import freenet.support.plugins.helpers1.PluginContext;
import freenet.support.plugins.helpers1.WebInterface;


public class N2N implements FredPlugin, FredPluginThreadless, FredPluginVersioned,
        FredPluginHTTP, FredPluginL10n, FredPluginBaseL10n {

    public static final String PLUGIN_ROOT = "/N2N";
    private static final String PLUGIN_CATEGORY = "Navigation.Menu.N2N.Name";

    private PluginRespirator pluginRespirator;
    private N2NHandler n2nHandler;
    private PluginContext pluginContext;
    private WebInterface webInterface;
    private PluginL10n l10n;

    @Override
	public void terminate() {
		webInterface.kill();
		n2nHandler.kill();
	}

    @Override
	public void runPlugin(PluginRespirator pr) {
		pluginRespirator = pr;
		n2nHandler = new N2NHandler(pluginRespirator.getNode());
        pluginContext = new PluginContext(pluginRespirator);
        webInterface = new WebInterface(pluginContext);
        webInterface.addNavigationCategory(PLUGIN_ROOT + "/", PLUGIN_CATEGORY,
                "Navigation.Menu.N2N.Tooltip", this);
        webInterface.registerVisible(new N2NToadlet(pluginContext, this, n2nHandler), PLUGIN_CATEGORY,
                "Navigation.Menu.N2N.Item.N2N.Name",
                "Navigation.Menu.N2N.Item.N2N.Tooltip");
	}

    @Override
    public String handleHTTPGet(HTTPRequest request) throws PluginHTTPException {
        throw new RedirectPluginHTTPException("Redirecting to N2N plugin…", PLUGIN_ROOT);
    }

    @Override
    public String handleHTTPPost(HTTPRequest request) throws PluginHTTPException {
        throw new PluginHTTPException("POST requests not implemented", request.getPath());
    }

    @Override
    public String getVersion() {
        return Version.BUILD;
    }

    @Override
    public String getString(String key) {
        String s = l10n.getBase().getString(key);
        return s != null ? s : key;
    }

    @Override
    public void setLanguage(LANGUAGE selectedLanguage) {
        l10n = new PluginL10n(this, selectedLanguage);
    }

    @Override
    public String getL10nFilesBasePath() {
        return "nl/urandom/n2n/l10n"; //NON-NLS
    }

    @Override
    public String getL10nFilesMask() {
        return "${lang}.properties"; //NON-NLS
    }

    @Override
    public String getL10nOverrideFilesMask() {
        return "${lang}.override.properties"; //NON-NLS
    }

    @Override
    public ClassLoader getPluginClassLoader() {
        return this.getClass().getClassLoader();
    }
}
