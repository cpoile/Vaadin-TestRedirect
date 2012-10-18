package org.test.TestRedirect

import com.vaadin.ui._
import com.vaadin.server.{Page, VaadinRequest}
import com.vaadin.server.Page.{FragmentChangedEvent, FragmentChangedListener}
import collection.JavaConverters._
import collection.mutable
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import java.util.UUID
import com.vaadin.annotations.PreserveOnRefresh
import UIExtRefresher.SetRefresher
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.Button
import scala.Some
import com.vaadin.ui.TextField
import com.vaadin.ui

@PreserveOnRefresh
class TestRedirectUI extends UI {
  var sessionInProgress = false

  @Override
  def init(request: VaadinRequest) {
    println("UI init called.")
    val path = request.getRequestPathInfo
    val params = request.getParameterMap.asScala
    println("UI Init handled request with url: " + path + " and params: " + params)

    getPage.addFragmentChangedListener(new FragmentChangedListener {
      def fragmentChanged(event: FragmentChangedEvent) {
        println("FragmentChangeEvent fired.")
        handleFragment(event.getFragment, event.getPage)
      }
    })

    val frags = getPage.getFragment
    // if no fragments, create a new experiment.
    if (frags == null || frags.length == 0) {
      newExperimentUser()
    }
    else // handle the new UI's fragment(s) now -- they could be a returning user, or an admin msg:
      handleFragment(Page.getCurrent.getFragment, Page.getCurrent)
  }

  def handleFragment(frag: String, page: Page) {
    println("Fragment handled: " + frag)
    val paramMap = ParamHandler.extractMoreKVs(frag.split('&').toList)
    println("Frag as paramMap: " + paramMap)
    page.setFragment("", false)
    if (paramMap.contains("uuid")) {
      val uuid = paramMap("uuid")
      println("received uuid: " + uuid)
      UserStore.retUsers.get(uuid) match {
        case None => {
          println("received uuid: " + uuid + " which was not found in the DB. Treating as a new user.")
          newExperimentUser()
        }
        case Some(retUserInfo) if (sessionInProgress && paramMap.contains("content")) => {
          println("Returning user in their old session")
          UserStore.retUsers -= uuid
          // Do some system-specific work to get the user's view, and then set it.
          returningExperimentUser(paramMap("content"))
        }
        case Some(retUserInfo) if (!sessionInProgress && paramMap.contains("content")) => {
          println("Returning user in a new session. uuid, ReturningUserInfo: " + uuid + ", " + retUserInfo)
          UserStore.retUsers -= uuid
          returningExperimentUser(paramMap("content"))
        }
      }
    }  else {
      newExperimentUser()
    }
  }

  def newExperimentUser() {
    //new SetRefresher(this)
    sessionInProgress = true
    val layout = buildRedirect
    setContent(layout)
  }

  def returningExperimentUser(tfData: String) {
    //new SetRefresher(this)
    val layout = buildRedirect
    val notice = new Label("Welcome back to the experiment. You had entered: " + tfData)
    layout.addComponent(notice)
    setContent(layout)
  }

  def buildRedirect: AbstractLayout = {
    val layout = new ui.VerticalLayout()
    val surveyLocation = "http://fluidsurveys.usask.ca/s/endOfRnd/"

    val tf = new TextField("Enter Some Info to be sent with you.")
    tf.setValue("exampleContent")
    val button = new Button("Take a survey offsite, then return.", new ClickListener {
      def buttonClick(event: ClickEvent) {
        val uuid = UUID.randomUUID().toString
        UserStore.retUsers += (uuid -> tf.getValue)
        Page.getCurrent.setLocation(surveyLocation + "?uuid=" + uuid + "&content=" + tf.getValue)
      }
    })

    layout.addComponents(tf, button)
    layout
  }

}