package org.test.TestRedirect

import com.vaadin.ui._
import com.vaadin.server._
import com.vaadin.ui.Button.{ClickEvent, ClickListener}
import java.util.UUID
import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.Button
import com.vaadin.ui.TextField
import com.vaadin.ui
import com.vaadin.server.Page
import scala.Some
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinService
import com.vaadin.server.VaadinServletRequest
import javax.servlet.http.Cookie

@PreserveOnRefresh
class TestRedirectUI extends UI {
  var sessionInProgress = false

  @Override
  def init(request: VaadinRequest) {
    println("UI init called.")

    // Try the cookie method.
    val cookies = request.asInstanceOf[VaadinServletRequest].getCookies
    val retCode = cookies filter (_.getName == "ExpServerRetCode")
    val retContent = cookies filter (_.getName == "ExpServerContent")
    if (retCode.length > 0) {
      val content = if (retContent.length > 0) retContent(0).getValue else ""
      handleRetCode(retCode(0).getValue, content, VaadinService.getCurrentResponse.asInstanceOf[VaadinServletResponse])
    }
  }

  def handleRetCode(uuid: String, content: String, response: VaadinServletResponse) {
    println("Found return code: " + uuid)
    UserStore.retUsers.get(uuid) match {
      case None => {
        println("received uuid: " + uuid + " which was not found in the DB. Treating as a new user.")
        newExperimentUser()
      }
      case Some(retUserInfo) => {
        println("Returning user in a new session. uuid, ReturningUserInfo: " + uuid + ", " + retUserInfo)
        UserStore.retUsers -= uuid
        response.addCookie(makeCookie("ExpServerRetCode", "", 0, "/"))
        response.addCookie(makeCookie("ExpServerContent", "", 0, "/"))
        returningExperimentUser(content)
      }
    }
  }

  def newExperimentUser() {
    // Problem happens with or without the UI refresher.
    //new SetRefresher(this)
    sessionInProgress = true

    val layout = buildRedirect
    setContent(layout)
  }

  def returningExperimentUser(tfData: String) {
    // Problem happens with or without the UI refresher.
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

        /**
         * One solution is to delete all cookies before sending the user away.
         * This way we hit the UI init funtions above and can extract the return code uuid
         * But would this cause unanticipated problems with the way Vaadin works?
         */
        val cookies = VaadinService.getCurrentRequest.asInstanceOf[VaadinServletRequest].getCookies
        println("cookies: " + cookies)
        for (c <- cookies) {
          c.setMaxAge(0)
          c.setPath("/")
          VaadinService.getCurrentResponse.asInstanceOf[VaadinServletResponse].addCookie(c)
        }
        VaadinService.getCurrentResponse.asInstanceOf[VaadinServletResponse].addCookie(makeCookie("ExpServerRetCode", uuid, 60*60, "/"))
        VaadinService.getCurrentResponse.asInstanceOf[VaadinServletResponse].addCookie(makeCookie("ExpServerContent", tf.getValue, 60*60, "/"))

        Page.getCurrent.setLocation(surveyLocation + "?uuid=" + uuid)
      }
    })

    layout.addComponents(tf, button)
    layout
  }

  def makeCookie(name: String, value: String, expiry: Int, path: String): Cookie = {
    val c = new Cookie(name, value)
    c.setMaxAge(expiry)
    c.setPath(path)
    c
  }
}