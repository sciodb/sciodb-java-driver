package org.sciodb.client;

import org.apache.log4j.Logger;
import org.sciodb.utils.CommandEncoder;
import org.sciodb.utils.models.StatusCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author jesus.navarrete  (26/02/16)
 */
public class Client {

    final static private Logger logger = Logger.getLogger(Client.class);

    final static String sms = "{ method : '//echo'}";

    public static void main(String[] args) throws IOException {
        final InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9090);

        long init = System.currentTimeMillis();

        int total = 100;
        final SocketChannel client = SocketChannel.open(hostAddress);
        for (int i = 0; i < total; i++) {

            new Thread(i + ""){
                @Override
                public synchronized void start() {
                    super.start();
                    try {

//                        logger.info("Client... started");

                        final StatusCommand status = createCommand(this.getName());

                        final byte [] message = CommandEncoder.encode(status).getBytes();
                        final ByteBuffer buffer = ByteBuffer.wrap(message);
                        final String headerSize = String.format("%04d", message.length);
                        final ByteBuffer header = ByteBuffer.wrap(headerSize.getBytes());
                        client.write(header);
                        client.write(buffer);
                        buffer.clear();

                        final ByteBuffer response = ByteBuffer.allocate(1024);

                        int currentSize = client.read(response);
                        byte [] data = new byte[currentSize];
                        System.arraycopy(response.array(), 0, data, 0, currentSize);

                        final String str = new String(data);
                        logger.debug("Got: " + str);

//                        logger.info("Message sent! ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
        client.close();
        long end = System.currentTimeMillis() - init;
        logger.info(total + " message in " + end + "ms");
    }

    public static void threads() {
        Runnable client = new Runnable() {
            public void run() {
                try {
                    new Client().startClient();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        new Thread(client, "client-A").start();
        new Thread(client, "client-B").start();

    }

    public void startClient() throws IOException, InterruptedException {

        final InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9090);
        final SocketChannel client = SocketChannel.open(hostAddress);

        logger.info("Client... started");

        final String threadName = Thread.currentThread().getName();

        int counter = 0;
        for (; counter < 100; counter++) {
            byte [] message = (threadName + " message " + counter).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);

            logger.info(threadName + " message " + counter);

            buffer.clear();
        }
        client.close();

        logger.info(threadName + " sent " + counter + " messages.");
    }

    private static StatusCommand createCommand(final String id) {
//        try {
//            String id = Inet4Address.getLocalHost().getHostAddress() + System.currentTimeMillis();

            final StatusCommand status = new StatusCommand();
            status.setOperationID("status");
            status.setMessageID(id);
            return status;
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//            return null;
    }

}
