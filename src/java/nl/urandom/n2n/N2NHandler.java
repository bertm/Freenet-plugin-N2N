package nl.urandom.n2n;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

import freenet.node.Node;
import freenet.node.NodeToNodeMessageListener;
import freenet.node.PeerNode;

class N2NHandler implements NodeToNodeMessageListener {
    private static final int MESSAGE_TYPE = Node.N2N_MESSAGE_TYPE_FPROXY;
    
    private final Node node;
    private final NodeToNodeMessageListener origListener;
    private final Map<Integer, NodeToNodeMessageListener> listenersAlias;
    
    private final ArrayList<String> messages = new ArrayList<String>();

    public N2NHandler(Node node) {
        NodeToNodeMessageListener origListener = null;
        Map<Integer, NodeToNodeMessageListener> listenersAlias = null;
        
        try {
            Field listenersMapField = Node.class.getDeclaredField("n2nmListeners");
            synchronized(node) {
                listenersMapField.setAccessible(true);
                listenersAlias = (Map<Integer, NodeToNodeMessageListener>)listenersMapField.get(node);
                listenersMapField.setAccessible(false);
            }
        } catch (Exception e) {
            // Something failed. isInstalled() will return false.
        }
        this.listenersAlias = listenersAlias;
        this.node = node;
        this.origListener = getCurrentListener();
        putListener(this);
    }
    
    public boolean isInstalled() {
        return getCurrentListener() == this;
    }
    
    public void kill() {
        synchronized(node) {
            if (isInstalled()) {
                listenersAlias.put(MESSAGE_TYPE, origListener);
            }
        }
    }
    
    private NodeToNodeMessageListener getCurrentListener() {
        synchronized(node) {
            if (listenersAlias == null) {
                return null;
            }
            return listenersAlias.get(MESSAGE_TYPE);
        }
    }
    
    private void putListener(NodeToNodeMessageListener listener) {
        synchronized(node) {
            if (listenersAlias == null) {
                return;
            }
            listenersAlias.put(MESSAGE_TYPE, listener);
        }
    }
    
    public Collection<String> getMessages() {
        return messages;
    }
    
    @Override
	public void handleMessage(byte[] data, boolean fromDarknet, PeerNode src, int type) {
		try {
			messages.add("Received from " + src.getPeer() + ": " + new String(data, "UTF-8"));
		} catch (Exception e) {
			messages.add("Error while handling message: " + e);
		}
		if (origListener != null) {
		    origListener.handleMessage(data, fromDarknet, src, type);
		}
	}
}
