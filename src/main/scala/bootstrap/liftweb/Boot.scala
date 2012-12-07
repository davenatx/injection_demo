/*
* Copyright 2012 David Price
*
* This file is part of InjectionDemo.
*
* InjectionDemo is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.
*
* InjectionDemo is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with InjectionDemo.  If not, see <http://www.gnu.org/licenses/>.
*/
package bootstrap.liftweb

import net.liftweb._
import common.{ Loggable, Full }
import http._
import sitemap.{ SiteMap, Menu }
import net.liftweb.mapper.{ DB, DefaultConnectionIdentifier, StandardDBVendor, Schemifier, MapperRules }
import util.Helpers._
import util.Helpers
import net.liftmodules.widgets.tablesorter.TableSorter
import java.util.Date
import util.{ DefaultDateTimeConverter, Props, NamedPF }
import com.dmp.model._

class Boot extends Loggable {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.dmp")

    // build sitemap
    val entries = List(Menu("Login") / "index", Menu("User") / "user") ::: Nil

    LiftRules.uriNotFound.prepend(NamedPF("404handler") {
      case (req, failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions", "404"), "html", false, false))
    })

    LiftRules.setSiteMap(SiteMap(entries: _*))

    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // set the JNDI name
    DefaultConnectionIdentifier.jndiName = "jdbc/student_accounts"

    // handle JNDI not being available
    if (!DB.jndiJdbcConnAvailable_?) {
      logger.warn("No JNDI configured - making a direct application connection using H2")
      // setup the database params
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
      LiftRules.unloadHooks.append(() => DBVendor.closeAllConnections_!())
    }

    S.addAround(DB.buildLoanWrapper)

    MapperRules.columnName = (_, name) => snakify(name)

    Schemifier.schemify(true, Schemifier.infoF _,
      Student, Payment)

    // enable Notice AutoFadeOut
    LiftRules.noticesAutoFadeOut.default.set((noticeType: NoticeType.Value) => Full((2 seconds, 2 seconds)))

    // Init TableSorter
    TableSorter.init

    // Setup default date and time formatting to MM/dd/yyyy
    LiftRules.dateTimeConverter.default.set(() => new net.liftweb.util.DateTimeConverter {
      def dateTime = {
        val sdf = new java.text.SimpleDateFormat("M/d/yy hh:mm a")
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("America/Chicago"))
        sdf.setLenient(true)
        sdf
      }

      def date = {
        val sdf = new java.text.SimpleDateFormat("MM/dd/yyyy")
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("America/Chicago"))
        sdf.setLenient(true)
        sdf
      }

      def formatDateTime(d: Date) = dateTime.format(d)

      def formatDate(d: Date) = date.format(d)

      def formatTime(d: Date) = DefaultDateTimeConverter.formatTime(d)

      def parseDateTime(s: String) = Helpers.tryo {
        dateTime.parse(s)
      }

      def parseDate(s: String) = Helpers.tryo {
        date.parse(s)
      }

      def parseTime(s: String) = DefaultDateTimeConverter.parseTime(s)

    })
  }

  /*
   * DBVendor object created if JNDI Source was not found
   */
  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:student_accounts"),
    Props.get("db.user"),
    Props.get("db.pass"))
}