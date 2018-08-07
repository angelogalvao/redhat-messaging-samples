package com.angelogalvao.samples.artemis;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.core.postoffice.RoutingStatus;
import org.apache.activemq.artemis.core.server.MessageReference;
import org.apache.activemq.artemis.core.server.RoutingContext;
import org.apache.activemq.artemis.core.server.ServerConsumer;
import org.apache.activemq.artemis.core.server.ServerSession;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.apache.activemq.artemis.core.transaction.Transaction;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * My custom broker plugin implementation. It only prints the message flowing in and out the broker.
 *
 * @author <a href="mailto:angelogalvao@gmail.com">Ângelo Galvão</a>
 */
public class MyBrokerPlugin implements ActiveMQServerPlugin {

    private static final Logger log = Logger.getLogger(MyBrokerPlugin.class);

    // In Java 9 this is significantly simpler
    private Set<String> blackList = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{"activemq.notifications"})));

    public void afterSend(ServerSession session, Transaction tx, Message message, boolean direct, boolean noAutoCreateQueue, RoutingStatus result) {

        // skip the messages that it is in black list, like activemq.notifications
        if( blackList.contains(message.getAddress()))
            return;

        log.info(String.format("MyBrokerPlugin.afterSend: Message getting OUT of the broker. Message: %s", message));

    }


    public void afterMessageRoute(Message message, RoutingContext context, boolean direct, boolean rejectDuplicates, RoutingStatus result) {

        // skip the messages that it is in black list, like activemq.notifications
        if( blackList.contains(message.getAddress()))
            return;

        log.info(String.format("MyBrokerPlugin.afterMessageRoute: Message getting IN of the broker. Message: %s", message));
    }
}
