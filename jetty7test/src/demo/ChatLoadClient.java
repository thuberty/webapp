package demo;

import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;


/* ------------------------------------------------------------ */
/** WebSocket Example Chat client.
 * <p>This class is a example of the Jetty WebSocket client API to
 * create a load tester for the simple {@link ChatServlet}.
 * It create a number of WebSocket chat connections and then picks random
 * connections to send messages on.   The received messages are simply counted.
 */
public class ChatLoadClient implements WebSocket.OnTextMessage
{
    private static final AtomicLong sent = new AtomicLong(0);
    private static final AtomicLong received = new AtomicLong(0);
    private static final Set<ChatLoadClient> members = new CopyOnWriteArraySet<ChatLoadClient>();
    private final String name;
    private final Connection connection;
    
    
    /* ------------------------------------------------------------ */
    /** Construct a Chat Load Client
     * @param username The username of the client
     * @param client The WebSocketClient to use for the connection.
     * @param host The host to connect to
     * @param port The port to connect to
     * @throws Exception
     */
    public ChatLoadClient(String username,WebSocketClient client,String host, int port)
    throws Exception
    {
        name=username;
        connection=client.open(new URI("ws://"+host+":"+port+"/chat"),this).get();
    }
    
    /* ------------------------------------------------------------ */
    /** Send a chat message from the user
     * @param message the message to send
     * @throws IOException
     */
    public void send(String message) throws IOException
    {
        connection.sendMessage(name+":"+message);
        sent.incrementAndGet();
    }
    
    /* ------------------------------------------------------------ */
    /** Callback on successful open of the websocket connection.
     */
    public void onOpen(Connection connection)
    {
        members.add(this);
    }

    /* ------------------------------------------------------------ */
    /** Callback on close of the WebSocket connection
     */
    public void onClose(int closeCode, String message)
    {
        members.remove(this);
    }

    /* ------------------------------------------------------------ */
    /** Callback on receiving a message
     */
    public void onMessage(String data)
    {
        received.incrementAndGet();
    }
    
    /* ------------------------------------------------------------ */
    /** Disconnect the client
     * @throws IOException
     */
    public void disconnect() throws IOException
    {
        connection.disconnect();
    }

    /* ------------------------------------------------------------ */
    /** Main method to create and use ChatLoadClient instances.
     * <p>The default is to connection to localhost:8080 with 1000 clients and to send 1000 messages.
     * @param arg The command line arguments are [ host [ port [ clients [ messages ]]]]. The default is to connection to localhost:8080 with 1000 clients and to send 1000 messages.
     * @throws Exception
     */
    public static void main(String... arg) throws Exception
    {
        String host=arg.length>0?arg[0]:"localhost";
        int port=arg.length>1?Integer.parseInt(arg[1]):8080;
        int clients=arg.length>2?Integer.parseInt(arg[2]):1000;
        int mesgs=arg.length>3?Integer.parseInt(arg[3]):1000;
        
        WebSocketClientFactory factory = new WebSocketClientFactory();
        factory.setBufferSize(4096);
        factory.start();
        
        WebSocketClient client = factory.newWebSocketClient();
        client.setMaxIdleTime(30000);
        client.setProtocol("chat");
        
        // Create client serially
        long start=System.currentTimeMillis();
        ChatLoadClient[] chat = new ChatLoadClient[clients];
        for (int i=0;i<chat.length;i++)
            chat[i]=new ChatLoadClient("user"+i,client,host,port);
        while(members.size()<clients)
        {
            if (System.currentTimeMillis()>(start+client.getMaxIdleTime()))
                break;
            Thread.sleep(10);
        }
        long end=System.currentTimeMillis();
        
        System.err.printf("Opened %d of %d connections to %s:%d in %dms\n",members.size(),clients,host,port,(end-start));
        
        // Send messages
        Random random = new Random();
        start = System.currentTimeMillis();
        for (int i=0;i<mesgs;i++)
        {
            ChatLoadClient c = chat[random.nextInt(chat.length)];
            String msg = "Hello random "+random.nextLong();
            c.send(msg);
        }
        long last=0;
        long progress=start;
        while(received.get()<(clients*mesgs))
        {
            if (System.currentTimeMillis()>(progress+client.getMaxIdleTime()))
                break;
            if (received.get()!=last)
            {
                progress=System.currentTimeMillis();
                last=received.get();
            }
            Thread.sleep(10);
        }
        end=System.currentTimeMillis();
        System.err.printf("Sent/Received %d/%d messages in %dms: %dmsg/s\n",sent.get(),received.get(),(end-start),(received.get()*1000)/(end-start));
        
        // Close all connections
        start=System.currentTimeMillis();
        for (int i=0;i<chat.length;i++)
            chat[i].disconnect();
        while(members.size()>0)
        {
            if (System.currentTimeMillis()>(start+client.getMaxIdleTime()))
                break;
            Thread.sleep(10);
        }
        end=System.currentTimeMillis();
        
        System.err.printf("Closed %d connections to %s:%d in %dms\n",clients,host,port,(end-start));
     
        factory.stop();
    }
    
}
