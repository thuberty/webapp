package demo.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;

import demo.model.Model;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;



/**
 * Servlet that reacts to HTML5 websocket connections.
 * When a new websocket connection is established, the members data structure
 * is updated with a chat member and its socket. 
 */
public class WebSocketTrafficController extends HttpServlet 
{
    // used to bringup web socket functionality
	private WebSocketFactory _wsFactory;
	Model model;
	// holds a hashmap of all chat members currently active
    private final Map<String,ChatMember> members = new ConcurrentHashMap<String,ChatMember>();
    
    /** 
     * Initialize the servlet by creating the WebSocketFactory.
     */
    @Override
    public void init() throws ServletException
    {
    	// configure wordnet C:\Program Files (x86)\WordNet\2.1\dict
    	System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");

        // Create and configure WS factory
    	model = new Model(getServletConfig());
    	ChatMember.setMembers(members);
    	ChatMember.setDAO(model);
        _wsFactory=new WebSocketFactory(new WebSocketFactory.Acceptor()
        
        {
            public boolean checkOrigin(HttpServletRequest request, String origin)
            {
                // Allow all origins
                return true;
            }
            
            public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol)
            {
            	
                // only consider connections that use "chat" protocol
                if ("chat".equals(protocol)) {
                	String sessionId = request.getSession().getId();
                	ChatWebSocket memberSocket = new ChatWebSocket();
                	
                	// check if ChatMember already has a session
                	if (members.containsKey(sessionId)) {
                		// member has session, add socket to member
                		ChatMember member = members.get(sessionId);
                		memberSocket.setMember(member);
                	}
                	else {
                		// member does not have session, create member and socket
                		ChatMember member = new ChatMember(request.getSession());
                		memberSocket.setMember(member);
                		members.put(request.getSession().getId(), member);
                	}
                	
                    return memberSocket;
                }
                
                return null;
            }
        });
        
        _wsFactory.setBufferSize(4096);
        _wsFactory.setMaxIdleTime(6000000);
    }
    
    /** 
     * Handle the handshake GET request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {        
        // If the WebSocket factory accepts the connection, then return
        if (_wsFactory.acceptWebSocket(request,response))
            return;
        // Otherwise send an HTTP error.
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"Websocket only");
    }

    

}
