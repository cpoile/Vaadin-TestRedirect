package org.test.TestRedirect.UIExtRefresher;

import com.vaadin.ui.UI;

public class SetRefresher {

   public UIExtRefresher uiRefresher;

   public SetRefresher(UI theUI) {
      uiRefresher = new UIExtRefresher();
      uiRefresher.extend(theUI);
      uiRefresher.setInterval(500);
      uiRefresher.setEnabled(true);
   }
}
