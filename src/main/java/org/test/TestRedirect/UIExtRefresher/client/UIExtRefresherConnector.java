package org.test.TestRedirect.UIExtRefresher.client;


import com.google.gwt.user.client.Timer;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import org.test.TestRedirect.UIExtRefresher.UIExtRefresher;

@Connect(UIExtRefresher.class)
public class UIExtRefresherConnector extends AbstractExtensionConnector {
   private static final long serialVersionUID = -4925903737141520789L;
   private UIExtRefresherRpc rpc = RpcProxy.create(UIExtRefresherRpc.class, this);

   private Timer timer = new Timer() {
      @Override
      public void run() {
         rpc.refresh();
      }
   };

   @Override
   public void onStateChanged(StateChangeEvent stateChangeEvent) {
      super.onStateChanged(stateChangeEvent);
      timer.cancel();
      if (isEnabled()) {
         timer.scheduleRepeating(this.getState().interval);
      }
   }

   @Override
   public void onUnregister() {
      timer.cancel();
   }


   @Override
   public UIExtRefresherState getState() {
      return (UIExtRefresherState) super.getState();
   }
}
