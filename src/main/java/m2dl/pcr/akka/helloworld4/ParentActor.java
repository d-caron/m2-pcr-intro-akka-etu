package m2dl.pcr.akka.helloworld4;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class ParentActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef helloActorRef;
    private ActorRef goodbyActorRef;

    public ParentActor() {
        log.info("HelloActor constructor");
        helloActorRef = getContext().actorOf(Props.create(HelloActor.class), "hello-actor");
        goodbyActorRef = getContext().actorOf(Props.create(GoodbyeActor.class), "goodbye-actor");
    }

    Procedure<Object> hello = new Procedure<Object>() {
        public void apply(Object msg) throws Exception {
            if (msg instanceof String) {
                helloActorRef.tell(msg, getSelf());
                getContext().become(goodbye, false);
            } else {
                unhandled(msg);
            }
        }
    };

    Procedure<Object> goodbye = new Procedure<Object>() {
        public void apply(Object msg) throws Exception {
            if (msg instanceof String) {
                goodbyActorRef.tell(msg, getSelf());
                getContext().unbecome();
            } else {
                unhandled(msg);
            }
        }
    };

    @Override
    public void onReceive(Object message) throws Exception {
        hello.apply(message);
    }
    
}
