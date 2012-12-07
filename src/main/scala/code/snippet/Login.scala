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

package com.dmp.snippet

import scala.xml.{ NodeSeq }
import net.liftweb.util.Helpers._
import com.dmp.model.Student
import net.liftweb.http.{ RequestVar, S, SHtml }
import net.liftweb.common.{ Full, Logger }
import net.liftweb.mapper.{ By, IHaveValidatedThisSQL, BySql }

// RequestVar for Student
object currentStudentVar extends RequestVar[Student](null)

class Login extends Logger {

  def login(xhtml: NodeSeq): NodeSeq = {
    var user = "";
    var pass = "";

    /**
     * This is a horrible authentication method.  It's sole purpose is to demo SQL Injection
     */
    def badAuth() = {
      // ***Deliberate insecure SQL***
      // e.g. password field can be passed in as ' or '1' = '1
      Student.find(BySql("user_id = '" + user + "' AND password = '" + pass + "'",
        IHaveValidatedThisSQL("Not Smart!", "2011-06-13"))) match {
        case Full(student) => S.redirectTo("/user", () => currentStudentVar(student))
        case _ => {
          S.error("Invalid Login!")
          S.redirectTo("/index")
        }
      }
    }

    /**
     * This still isn't a good method for authentication however since it is using Mapper to generate
     * the SQL statements, it is no longer susceptible to SQL Injection
     */
    def betterAuth() = {
      Student.find(By(Student.userId, user)) match {
        case Full(student) => {
          if (student.password.is.equals(pass)) {
            S.redirectTo("/user", () => currentStudentVar(student))
          } else {
            S.error("Invalid Password!")
            S.redirectTo("/index")
          }
        }
        case _ => {
          S.error("Unknown username!")
          S.redirectTo("/index")
        }
      }
    }

    bind("login", xhtml,
      "user" -> SHtml.text(user, user = _, "maxlength" -> "10", "tabindex" -> "1", "class" -> "user",
        "id" -> "user_id"),
      "pass" -> SHtml.password(pass, pass = _, "maxlength" -> "10", "tabindex" -> "2", "class" -> "pass",
        "id" -> "password"),
      "submit" -> SHtml.submit("Login", badAuth, "tabindex" -> "3", "class" -> "button"))
  }
}