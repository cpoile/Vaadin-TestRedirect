package org.test.TestRedirect.UIExtRefresher;


import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.UI;
import org.test.TestRedirect.UIExtRefresher.client.UIExtRefresherRpc;
import org.test.TestRedirect.UIExtRefresher.client.UIExtRefresherState;

import java.util.ArrayList;
import java.util.List;

public class UIExtRefresher extends AbstractExtension {
   private static final long serialVersionUID = -4334091112142141600L;

   public void extend(UI target) {
      super.extend(target);
   }

   public interface UIExtRefresherListener {
      public void refresh(UIExtRefresher source);
   }
   private List<UIExtRefresherListener> listeners = new ArrayList<UIExtRefresherListener>();

   public UIExtRefresher() {
      registerRpc(new UIExtRefresherRpc() {
         private static final long serialVersionUID = 2820684509717566456L;

         @Override
         public void refresh() {
            for (UIExtRefresherListener listener: listeners) {
               listener.refresh(UIExtRefresher.this);
            }
         }
      });
   }

   @Override
   protected UIExtRefresherState getState() {
      return (UIExtRefresherState) super.getState();
   }
   public void addListener(UIExtRefresherListener listener) {
      listeners.add(listener);
   }
   public void removeListener(UIExtRefresherListener listener) {
      listeners.remove(listener);
   }

   public void setInterval(int millis) {
      getState().interval = millis;
   }
   public int getInterval() {
      return getState().interval;
   }
   public void setEnabled(boolean enabled) {
      getState().enabled = enabled;
   }
   public boolean isEnabled() {
      return getState().enabled;
   }
}
